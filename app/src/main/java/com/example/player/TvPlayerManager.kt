package com.example.player

import android.content.Context
import android.net.Uri
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

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_BUFFERING -> _playbackState.value = PlaybackUiState.Buffering
                Player.STATE_READY -> _playbackState.value = PlaybackUiState.Playing
                Player.STATE_ENDED -> _playbackState.value = PlaybackUiState.Error("پەخشی ئەم کەناڵە لە ئێستادا کارا نیە")
                Player.STATE_IDLE -> {
                    if (exoPlayer?.playerError != null) {
                        _playbackState.value = PlaybackUiState.Error("پەخشی ئەم کەناڵە لە ئێستادا کارا نیە")
                    } else {
                        _playbackState.value = PlaybackUiState.Idle
                    }
                }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            _playbackState.value = PlaybackUiState.Error("پەخشی ئەم کەناڵە لە ئێستادا کارا نیە")
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
                10_000,
                45_000,
                800,
                1500
            )
            .build()

        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(20_000)
            .setReadTimeoutMs(20_000)
            .setUserAgent("Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Mobile Safari/537.36 ExoPlayer")

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
        val player = getPlayer()
        if (url.isBlank()) {
            _currentStreamUrl.value = null
            _playbackState.value = PlaybackUiState.Error("پەخشی ئەم کەناڵە لە ئێستادا کارا نیە")
            player.stop()
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
