package com.example.data.remote

import android.util.Log
import com.example.data.model.Category
import com.example.data.model.Channel
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class RemotePlaylistFetcher(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()
) {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
    private val playlistAdapter = moshi.adapter(RemotePlaylistResponse::class.java)

    suspend fun fetchPlaylist(rawUrl: String): PlaylistResult = withContext(Dispatchers.IO) {
        val targetUrl = normalizeGitHubUrl(rawUrl)
        Log.d("RemotePlaylistFetcher", "Fetching channels from: $targetUrl")

        try {
            val request = Request.Builder()
                .url(targetUrl)
                .cacheControl(CacheControl.FORCE_NETWORK) // Always fetch fresh from GitHub to get instant changes!
                .header("User-Agent", "RebazTV/1.0 (Android TV)")
                .header("Accept", "*/*")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext PlaylistResult.Error("کێشە هەیە لە پەیوەندی بە سێرڤەر: کودی ${response.code}")
                }

                val bodyString = response.body?.string()?.trim() ?: ""
                if (bodyString.isEmpty()) {
                    return@withContext PlaylistResult.Error("فایلی ناو لینکەکە بەتاڵە")
                }

                // Check if it's M3U / M3U8 format
                if (bodyString.startsWith("#EXTM3U", ignoreCase = true) || bodyString.contains("#EXTINF")) {
                    val channels = parseM3uPlaylist(bodyString)
                    if (channels.isEmpty()) {
                        return@withContext PlaylistResult.Error("هیچ کەناڵێک لە ناو فایلی M3U نەدۆزرایەوە")
                    }
                    return@withContext PlaylistResult.Success(channels = channels, categories = null)
                }

                // Try JSON format (object or array)
                try {
                    if (bodyString.startsWith("[")) {
                        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, RemoteChannelDto::class.java)
                        val adapter = moshi.adapter<List<RemoteChannelDto>>(listType)
                        val list = adapter.fromJson(bodyString)
                        if (!list.isNullOrEmpty()) {
                            val channels = list.mapIndexed { idx, dto ->
                                dto.toChannel(calculatedNumber = idx + 1)
                            }
                            return@withContext PlaylistResult.Success(channels = channels, categories = null)
                        }
                    } else {
                        val parsed = playlistAdapter.fromJson(bodyString)
                        if (parsed?.channels != null && parsed.channels.isNotEmpty()) {
                            val channels = parsed.channels.mapIndexed { idx, dto ->
                                dto.toChannel(calculatedNumber = idx + 1)
                            }
                            val categories = parsed.categories?.map {
                                Category(
                                    id = it.id,
                                    nameKurdish = it.nameKurdish ?: it.name
                                )
                            }
                            return@withContext PlaylistResult.Success(channels = channels, categories = categories)
                        }
                    }
                } catch (e: Exception) {
                    Log.w("RemotePlaylistFetcher", "JSON parsing failed, trying line-by-line fallback: ${e.message}")
                }

                // Fallback attempt: M3U or plain line list
                val fallbackChannels = parseM3uPlaylist(bodyString)
                if (fallbackChannels.isNotEmpty()) {
                    return@withContext PlaylistResult.Success(channels = fallbackChannels, categories = null)
                }

                val plainChannels = parsePlainLines(bodyString)
                if (plainChannels.isNotEmpty()) {
                    return@withContext PlaylistResult.Success(channels = plainChannels, categories = null)
                }

                return@withContext PlaylistResult.Error("فۆرماتی فایلی گیت هاب نەناسراوە. تکایە دڵنیابە فایلی JSON یان M3U/M3U8 بێت.")
            }
        } catch (e: Exception) {
            Log.e("RemotePlaylistFetcher", "Error fetching playlist", e)
            return@withContext PlaylistResult.Error("هەڵە لە داگرتنی لیست: ${e.localizedMessage ?: "پەیوەندی نییە"}")
        }
    }

    /**
     * Converts a standard GitHub web URL (e.g. github.com/user/repo/blob/main/channels.json)
     * to the direct raw content URL (raw.githubusercontent.com/user/repo/main/channels.json)
     */
    fun normalizeGitHubUrl(url: String): String {
        var clean = url.trim()
        // If user provided a github.com/.../blob/... link, rewrite to raw.githubusercontent.com
        if (clean.contains("github.com") && clean.contains("/blob/")) {
            clean = clean.replace("github.com", "raw.githubusercontent.com")
                .replace("/blob/", "/")
        }
        // Remove trailing query or fragments if any
        return clean
    }

    /**
     * Standard IPTV M3U / M3U8 parser supporting:
     * #EXTINF:-1 tvg-id="id" tvg-name="name" tvg-logo="logo" group-title="Category",Channel Name
     * http://stream-url.m3u8
     */
    private fun parseM3uPlaylist(m3uContent: String): List<Channel> {
        val channels = mutableListOf<Channel>()
        val lines = m3uContent.lines()
        var currentName = ""
        var currentCategory = "all"
        var currentLogo = ""
        var currentNumber = 1

        val groupRegex = """group-title="([^"]+)"""".toRegex(RegexOption.IGNORE_CASE)
        val logoRegex = """tvg-logo="([^"]+)"""".toRegex(RegexOption.IGNORE_CASE)
        val nameRegex = """tvg-name="([^"]+)"""".toRegex(RegexOption.IGNORE_CASE)

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) continue

            if (trimmed.startsWith("#EXTINF", ignoreCase = true)) {
                // Extract group-title
                val groupMatch = groupRegex.find(trimmed)
                currentCategory = groupMatch?.groupValues?.get(1)?.trim() ?: "all"

                // Extract logo
                val logoMatch = logoRegex.find(trimmed)
                currentLogo = logoMatch?.groupValues?.get(1)?.trim() ?: ""

                // Extract name (either tvg-name or after comma)
                val commaIndex = trimmed.lastIndexOf(',')
                val afterComma = if (commaIndex != -1 && commaIndex < trimmed.length - 1) {
                    trimmed.substring(commaIndex + 1).trim()
                } else ""

                val nameMatch = nameRegex.find(trimmed)
                currentName = when {
                    afterComma.isNotEmpty() -> afterComma
                    nameMatch != null -> nameMatch.groupValues[1].trim()
                    else -> "کەناڵی $currentNumber"
                }
            } else if (!trimmed.startsWith("#")) {
                // It's a stream URL!
                if (trimmed.startsWith("http://", ignoreCase = true) ||
                    trimmed.startsWith("https://", ignoreCase = true) ||
                    trimmed.startsWith("rtsp://", ignoreCase = true)
                ) {
                    val badge = when {
                        currentLogo.isNotEmpty() && !currentLogo.startsWith("http", ignoreCase = true) && !currentLogo.contains("/") -> currentLogo.take(6).uppercase()
                        else -> {
                            val clean = currentName.filter { it.isLetterOrDigit() }.take(5).uppercase()
                            clean.ifBlank { "HD" }
                        }
                    }
                    val catId = mapCategoryStringToId(currentCategory)
                    
// ئەم دێڕانە زیاد بکە:
val displayCatName = if (catId == "all") "هەموو کەناڵەکان" else currentCategory

channels.add(
    Channel(
        id = "m3u_${currentNumber}_${currentName.hashCode()}",
        number = currentNumber,
        name = currentName.ifEmpty { "کەناڵی $currentNumber" },
        category = catId, // ئێستا ئەمە ناوی پاکێجە ڕاستەقینەکەیە
        streamUrl = trimmed,
        logoBadge = badge,
        subtitle = displayCatName, // لێرەدا ناوەکە پیشان دەدات
        isHd = true
    )
)

                    currentNumber++
                    currentName = ""
                    currentCategory = "all"
                    currentLogo = ""
                }
            }
        }
        return channels
    }

    private fun parsePlainLines(content: String): List<Channel> {
        val channels = mutableListOf<Channel>()
        var count = 1
        for (line in content.lines()) {
            val trimmed = line.trim()
            if (trimmed.isEmpty() || trimmed.startsWith("#")) continue
            // Patterns like "Name,URL" or "Name|URL"
            val parts = when {
                trimmed.contains(",") -> trimmed.split(",", limit = 2)
                trimmed.contains("|") -> trimmed.split("|", limit = 2)
                else -> null
            }
            if (parts != null && parts.size == 2) {
                val name = parts[0].trim()
                val url = parts[1].trim()
                if (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("rtsp://")) {
                    channels.add(
                        Channel(
                            id = "plain_${count}_${name.hashCode()}",
                            number = count,
                            name = name.ifEmpty { "کەناڵی $count" },
                            category = "all",
                            streamUrl = url,
                            logoBadge = name.take(5).uppercase(),
                            subtitle = "پەخشی ڕاستەوخۆ",
                            isHd = true
                        )
                    )
                    count++
                }
            } else if (trimmed.startsWith("http://") || trimmed.startsWith("https://") || trimmed.startsWith("rtsp://")) {
                channels.add(
                    Channel(
                        id = "plain_${count}",
                        number = count,
                        name = "کەناڵی $count",
                        category = "all",
                        streamUrl = trimmed,
                        logoBadge = "CH$count",
                        subtitle = "پەخشی ڕاستەوخۆ",
                        isHd = true
                    )
                )
                count++
            }
        }
        return channels
    }

        private fun mapCategoryStringToId(catName: String): String {
        val cleanName = catName.trim()
        return if (cleanName.isEmpty() || cleanName.equals("all", ignoreCase = true)) {
            "all"
        } else {
            cleanName 
        }
    }
}

sealed class PlaylistResult {
    data class Success(
        val channels: List<Channel>,
        val categories: List<Category>?
    ) : PlaylistResult()

    data class Error(val message: String) : PlaylistResult()
}
