package com.example.data.remote

import com.example.data.model.Category
import com.example.data.model.Channel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemotePlaylistResponse(
    @Json(name = "categories") val categories: List<RemoteCategoryDto>? = null,
    @Json(name = "channels") val channels: List<RemoteChannelDto>? = null,
    @Json(name = "version") val version: Int? = null,
    @Json(name = "updated_at") val updatedAt: String? = null
)

@JsonClass(generateAdapter = true)
data class RemoteCategoryDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "name_kurdish") val nameKurdish: String? = null,
    @Json(name = "icon") val icon: String? = null
)

@JsonClass(generateAdapter = true)
data class RemoteChannelDto(
    @Json(name = "id") val id: String? = null,
    @Json(name = "number") val number: Int? = null,
    @Json(name = "name") val name: String,
    @Json(name = "category") val category: String? = null,
    @Json(name = "stream_url") val streamUrl: String? = null,
    @Json(name = "url") val url: String? = null,
    @Json(name = "logo") val logo: String? = null,
    @Json(name = "logo_badge") val logoBadge: String? = null,
    @Json(name = "subtitle") val subtitle: String? = null,
    @Json(name = "is_hd") val isHd: Boolean? = true
) {
    fun toChannel(calculatedNumber: Int): Channel {
        val stream = streamUrl ?: url ?: ""
        val rawBadge = logoBadge ?: ""
        val badge = when {
            rawBadge.isNotBlank() && !rawBadge.startsWith("http", ignoreCase = true) && !rawBadge.contains("/") -> rawBadge.take(6).uppercase()
            else -> {
                val clean = name.filter { it.isLetterOrDigit() }.take(5).uppercase()
                clean.ifBlank { "HD" }
            }
        }
        val cat = category?.lowercase() ?: "all"
        return Channel(
            id = id ?: "ch_${name.replace("\\s+".toRegex(), "_")}_$calculatedNumber",
            number = number ?: calculatedNumber,
            name = name,
            category = cat,
            streamUrl = stream,
            logoBadge = badge,
            subtitle = subtitle ?: "پەخشی ڕاستەوخۆ بە کوالێتی بەرز",
            isHd = isHd ?: true
        )
    }
}
