package com.example.player

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class PlaybackUiState {
    object Idle : PlaybackUiState()
    object Buffering : PlaybackUiState()
    object Playing : PlaybackUiState()
    object Ended : PlaybackUiState()
    data class Error(val message: String) : PlaybackUiState()
}

@OptIn(UnstableApi::class)
class TvPlayerManager(private val context: Context) {

    private var exoPlayer: ExoPlayer? = null

    private val _playbackState = MutableStateFlow<PlaybackUiState>(PlaybackUiState.Idle)
    val playbackState: StateFlow<PlaybackUiState> = _playbackState.asStateFlow()

    private val _currentStreamUrl = MutableStateFlow<String?>(null)
    val currentStreamUrl: StateFlow<String?> = _currentStreamUrl.asStateFlow()

    // سیستەمی زیرەک بۆ دووبارە هەوڵدانەوە کاتێک پەخش دەپچڕێت
    private var retryCount = 0
    private val maxRetries = 3 // کەممان کردەوە بۆ ٣ جار تا زووتر بزانێت ئەگەر لینکەکە مردووە

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_BUFFERING -> _playbackState.value = PlaybackUiState.Buffering
                Player.STATE_READY -> {
                    retryCount = 0 
                    _playbackState.value = PlaybackUiState.Playing
                }
                Player.STATE_ENDED -> attemptRetry()
                Player.STATE_IDLE -> {
                    if (exoPlayer?.playerError != null) attemptRetry()
                }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            attemptRetry()
        }

        private fun attemptRetry() {
            if (retryCount < maxRetries) {
                retryCount++
                _playbackState.value = PlaybackUiState.Buffering
                
                Handler(Looper.getMainLooper()).postDelayed({
                    _currentStreamUrl.value?.let {
                        exoPlayer?.setMediaItem(MediaItem.fromUri(it))
                        exoPlayer?.prepare()
                        exoPlayer?.play()
                    }
                }, 2000)
            } else {
                _playbackState.value = PlaybackUiState.Error("پەخشی ئەم کەناڵە لە ئێستادا کارا نیە")
                // گرنگ: وەستاندنی پلەیەرەکە کاتێک هەڵە ڕوودەدات تا ئامادە بێت بۆ کەناڵێکی تر
                exoPlayer?.stop()
                exoPlayer?.clearMediaItems()
            }
        }
    }

    fun getPlayer(): ExoPlayer {
        if (exoPlayer == null) {
            initPlayer()
        }
        return exoPlayer!!
    }

    private fun initPlayer() {
        val trackSelector = DefaultTrackSelector(context).apply {
            setParameters(
                buildUponParameters()
                    .setForceHighestSupportedBitrate(true)
                    .setAllowVideoMixedMimeTypeAdaptiveness(true)
                    .setAllowVideoNonSeamlessAdaptiveness(true)
            )
        }

        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                32_000, 
                65_536, 
                2500,
                5000
            )
            .build()

        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(8_000) // ⏳ گرنگ: کەمکرایەوە بۆ ٨ چرکە
            .setReadTimeoutMs(8_000)    // ⏳ گرنگ: کەمکرایەوە بۆ ٨ چرکە
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")

        val dataSourceFactory = DefaultDataSource.Factory(context, httpDataSourceFactory)
        val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)

        exoPlayer = ExoPlayer.Builder(context)
            .setTrackSelector(trackSelector)
            .setLoadControl(loadControl)
            .setMediaSourceFactory(mediaSourceFactory)
            .setAudioAttributes(
                androidx.media3.common.AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                    .build(),
                true
            )
            .setHandleAudioBecomingNoisy(true)
            .build()
            .apply {
                playWhenReady = true
                repeatMode = Player.REPEAT_MODE_ALL
                addListener(playerListener)
            }
    }

    fun playStream(url: String) {
        retryCount = 0 
        val player = getPlayer()
        
        // ⭐ گرنگ: پاککردنەوەی تەواوەتی پێش خستنەسەری کەناڵی نوێ
        player.stop()
        player.clearMediaItems()

        if (url.isBlank()) {
            _currentStreamUrl.value = null
            _playbackState.value = PlaybackUiState.Error("پەخشی ئەم کەناڵە لە ئێستادا کارا نیە")
            return
        }
        if (_currentStreamUrl.value == url && player.isPlaying) {
            return
        }
        
        _currentStreamUrl.value = url
        _playbackState.value = PlaybackUiState.Buffering

        val uri = Uri.parse(url)
        val mediaItemBuilder = MediaItem.Builder().setUri(uri)

        val path = uri.path?.lowercase() ?: ""
        when {
            path.endsWith(".m3u8") -> mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_M3U8)
            path.endsWith(".mpd") -> mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_MPD)
            path.endsWith(".mp4") -> mediaItemBuilder.setMimeType(MimeTypes.VIDEO_MP4)
        }

        val mediaItem = mediaItemBuilder.build()
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    fun retry() {
        retryCount = 0
        _currentStreamUrl.value?.let { playStream(it) }
    }

    fun pause() {
        exoPlayer?.pause()
    }

    fun resume() {
        exoPlayer?.play()
    }

    fun release() {
        exoPlayer?.removeListener(playerListener)
        exoPlayer?.release()
        exoPlayer = null
    }
}
