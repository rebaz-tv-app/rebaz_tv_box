package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.local.CustomChannelEntity
import com.example.data.local.FavoriteDao
import com.example.data.local.FavoriteEntity
import com.example.data.model.Category
import com.example.data.model.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChannelRepository(
    private val favoriteDao: FavoriteDao,
    context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("rebaz_tv_prefs", Context.MODE_PRIVATE)

    companion object {
        const val PREF_LAST_CHANNEL_ID = "last_channel_id"
        const val PREF_LAST_CATEGORY_ID = "last_category_id"
        const val DEFAULT_CATEGORY_ID = "doc"
        const val DEFAULT_CHANNEL_ID = "ch_doc_5" // Shna Documentary (from screenshot!)
        const val FIXED_PLAYLIST_URL = "http://arabitv5.com:8000/get.php?username=Montaha.Maarouf&password=Maarouf.56432976&type=m3u_plus"
    }

    val categories = listOf(
        Category("doc", "دۆکۆمێنتاری"),
        Category("news", "هەواڵ"),
        Category("kurdish", "کوردی ناوخۆیی"),
        Category("sports", "وەرزش"),
        Category("kids", "منداڵان"),
        Category("religion", "ئاینی"),
        Category("favorites", "دڵخوازەکان"),
        Category("all", "هەموو کەناڵەکان")
    )

    // Default channels matching the screenshot and Kurdish TV landscape
    private val defaultChannels = listOf(
        // Documentary (matching user's screenshot directly!)
        Channel(
            id = "ch_doc_1",
            number = 1,
            name = "Reng Documentary",
            category = "doc",
            streamUrl = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8",
            logoBadge = "RENG",
            subtitle = "بەڵگەنامەیی ڕەنگ - سروشت و ژینگە",
            isHd = true
        ),
        Channel(
            id = "ch_doc_2",
            number = 2,
            name = "Kurdbin-KURDsat documentary",
            category = "doc",
            streamUrl = "https://cph-p2p-msl.akamaized.net/hls/live/2000341/test/master.m3u8",
            logoBadge = "KURD",
            subtitle = "کوردسات بەڵگەنامەیی - دیرۆک و کلتوور",
            isHd = true
        ),
        Channel(
            id = "ch_doc_3",
            number = 3,
            name = "Astera Documentary",
            category = "doc",
            streamUrl = "https://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8",
            logoBadge = "ASTERA",
            subtitle = "ئەستێرە دۆکۆمێنتاری - زانست و گەردوون",
            isHd = true
        ),
        Channel(
            id = "ch_doc_4",
            number = 4,
            name = "K24 Documentary",
            category = "doc",
            streamUrl = "https://bitmovin-a.akamaihd.net/content/sintel/hls/playlist.m3u8",
            logoBadge = "K24",
            subtitle = "کوردستان ٢٤ دۆکیۆمێنتاری",
            isHd = true
        ),
        Channel(
            id = "ch_doc_5",
            number = 5,
            name = "Shna Documentary",
            category = "doc",
            streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            logoBadge = "Stv",
            subtitle = "شنا دۆکۆمێنتاری - بەڵگەنامەیی ژیان",
            isHd = true
        ),
        Channel(
            id = "ch_doc_6",
            number = 6,
            name = "Sima Documentary",
            category = "doc",
            streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            logoBadge = "sima+",
            subtitle = "سیما دۆکۆمێنتاری پەخشی ٢٤ کاتژمێری",
            isHd = true
        ),
        Channel(
            id = "ch_doc_7",
            number = 7,
            name = "MMN Documentary",
            category = "doc",
            streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            logoBadge = "MMN",
            subtitle = "ئێم ئێم ئێن - بەڵگەنامەی جیهانی",
            isHd = true
        ),

        // News (هەواڵ)
        Channel(
            id = "ch_news_1",
            number = 10,
            name = "Kurdistan 24 HD",
            category = "news",
            streamUrl = "https://live-hls-web-aje.getaj.net/AJE/01.m3u8",
            logoBadge = "K24",
            subtitle = "کوردستان ٢٤ - هەواڵی ٢٤ کاتژمێری",
            isHd = true
        ),
        Channel(
            id = "ch_news_2",
            number = 11,
            name = "Rudaw News HD",
            category = "news",
            streamUrl = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8",
            logoBadge = "RUDAW",
            subtitle = "تۆڕی میدیایی ڕووداو",
            isHd = true
        ),
        Channel(
            id = "ch_news_3",
            number = 12,
            name = "Kurdsat News",
            category = "news",
            streamUrl = "https://cph-p2p-msl.akamaized.net/hls/live/2000341/test/master.m3u8",
            logoBadge = "KURDSAT",
            subtitle = "کوردسات نیوز - دەنگی ڕاستەقینە",
            isHd = true
        ),
        Channel(
            id = "ch_news_4",
            number = 13,
            name = "NRT News HD",
            category = "news",
            streamUrl = "https://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8",
            logoBadge = "NRT",
            subtitle = "ئێن ئاڕ تی - پەخشی ڕاستەوخۆ",
            isHd = true
        ),

        // Local Kurdish (کوردی ناوخۆیی)
        Channel(
            id = "ch_kurd_1",
            number = 20,
            name = "Kurdistan TV",
            category = "kurdish",
            streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
            logoBadge = "KTV",
            subtitle = "کوردستان تیڤی - هەولێر",
            isHd = true
        ),
        Channel(
            id = "ch_kurd_2",
            number = 21,
            name = "Gali Kurdistan",
            category = "kurdish",
            streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
            logoBadge = "GK",
            subtitle = "گەلی کوردستان - سلێمانی",
            isHd = true
        ),
        Channel(
            id = "ch_kurd_3",
            number = 22,
            name = "Kirkuk TV",
            category = "kurdish",
            streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4",
            logoBadge = "KIRKUK",
            subtitle = "کەرکووک تیڤی",
            isHd = true
        ),
        Channel(
            id = "ch_kurd_4",
            number = 23,
            name = "Waar TV HD",
            category = "kurdish",
            streamUrl = "https://bitmovin-a.akamaihd.net/content/sintel/hls/playlist.m3u8",
            logoBadge = "WAAR",
            subtitle = "وار تیڤی - دهۆک",
            isHd = true
        ),

        // Sports (وەرزش)
        Channel(
            id = "ch_sport_1",
            number = 30,
            name = "KurdSport HD",
            category = "sports",
            streamUrl = "https://rbmn-live.akamaized.net/hls/live/590964/BoRB-AT/master.m3u8",
            logoBadge = "SPORT",
            subtitle = "کورد سپۆرت - ڕووماڵی وەرزشی",
            isHd = true
        ),
        Channel(
            id = "ch_sport_2",
            number = 31,
            name = "Rudaw Sports",
            category = "sports",
            streamUrl = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8",
            logoBadge = "SPORT",
            subtitle = "ڕووداو سپۆرت",
            isHd = true
        ),
        Channel(
            id = "ch_sport_3",
            number = 32,
            name = "RedBull Action Live",
            category = "sports",
            streamUrl = "https://rbmn-live.akamaized.net/hls/live/590964/BoRB-AT/master.m3u8",
            logoBadge = "REDBULL",
            subtitle = "وەرزشە جیهانییەکان",
            isHd = true
        ),

        // Kids (منداڵان)
        Channel(
            id = "ch_kids_1",
            number = 40,
            name = "Zarok TV",
            category = "kids",
            streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            logoBadge = "ZAROK",
            subtitle = "زارۆک تیڤی - ئەنیمەیشن بۆ منداڵان",
            isHd = true
        ),
        Channel(
            id = "ch_kids_2",
            number = 41,
            name = "Kurdmax Papula",
            category = "kids",
            streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            logoBadge = "PAPULA",
            subtitle = "کوردماکس پەپوولە",
            isHd = true
        ),

        // Religious (ئاینی)
        Channel(
            id = "ch_rel_1",
            number = 50,
            name = "Payam TV HD",
            category = "religion",
            streamUrl = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8",
            logoBadge = "PAYAM",
            subtitle = "پەیام تیڤی",
            isHd = true
        ),
        Channel(
            id = "ch_rel_2",
            number = 51,
            name = "Speda TV HD",
            category = "religion",
            streamUrl = "https://cph-p2p-msl.akamaized.net/hls/live/2000341/test/master.m3u8",
            logoBadge = "SPEDA",
            subtitle = "سپێدە تیڤی",
            isHd = true
        ),
        Channel(
            id = "ch_rel_3",
            number = 52,
            name = "Koran Kareem Live",
            category = "religion",
            streamUrl = "https://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8",
            logoBadge = "QURAN",
            subtitle = "قورئانی پیرۆز - پەخشی ٢٤ کاتژمێر",
            isHd = true
        )
    )

    val favoriteIds: Flow<List<String>> = favoriteDao.getAllFavoriteIds()

    val customChannels: Flow<List<Channel>> = favoriteDao.getAllCustomChannels().map { list ->
        list.map { entity ->
            Channel(
                id = entity.id,
                number = entity.number,
                name = entity.name,
                category = entity.category,
                streamUrl = entity.streamUrl,
                logoBadge = entity.logoBadge.ifBlank { "EXT" },
                subtitle = entity.subtitle,
                isHd = true
            )
        }
    }

    fun getDefaultChannels(): List<Channel> = defaultChannels

    suspend fun toggleFavorite(channelId: String) {
        if (favoriteDao.isFavorite(channelId)) {
            favoriteDao.removeFavorite(channelId)
        } else {
            favoriteDao.addFavorite(FavoriteEntity(channelId))
        }
    }

    suspend fun addCustomChannel(channel: Channel) {
        favoriteDao.insertCustomChannel(
            CustomChannelEntity(
                id = channel.id,
                number = channel.number,
                name = channel.name,
                category = channel.category,
                streamUrl = channel.streamUrl,
                logoBadge = channel.logoBadge,
                subtitle = channel.subtitle
            )
        )
    }

    fun saveLastPlayedChannel(channelId: String, categoryId: String) {
        prefs.edit()
            .putString(PREF_LAST_CHANNEL_ID, channelId)
            .putString(PREF_LAST_CATEGORY_ID, categoryId)
            .apply()
    }

    fun getPlaylistUrl(): String {
        return FIXED_PLAYLIST_URL
    }

    fun getLastPlayedChannelId(): String {
        return prefs.getString(PREF_LAST_CHANNEL_ID, DEFAULT_CHANNEL_ID) ?: DEFAULT_CHANNEL_ID
    }

    fun getLastCategoryId(): String {
        return prefs.getString(PREF_LAST_CATEGORY_ID, DEFAULT_CATEGORY_ID) ?: DEFAULT_CATEGORY_ID
    }
}
