    private fun initPlayer() {
        val renderersFactory = DefaultRenderersFactory(context)
            .setEnableDecoderFallback(true)

        val trackSelector = DefaultTrackSelector(context).apply {
            setParameters(
                buildUponParameters()
                    // تێبینی: ئەو دێڕەی کە زۆری دەکرد بۆ بەرزترین کوالێتی سڕیمانەوە تا نەوەستێت
                    .setAllowVideoMixedMimeTypeAdaptiveness(true)
                    .setAllowVideoNonSeamlessAdaptiveness(true)
                    .setAllowAudioMixedMimeTypeAdaptiveness(true)
                    .setAllowAudioMixedSampleRateAdaptiveness(true)
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

        // کاتی چاوەڕوانیمان کرد بە ١٥ چرکە، ئەمە باشترین کاتە بۆ IPTV بێ ئەوەی زوو بپچڕێت
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(15_000) 
            .setReadTimeoutMs(15_000)    
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")

        val dataSourceFactory = DefaultDataSource.Factory(context, httpDataSourceFactory)
        val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)

        exoPlayer = ExoPlayer.Builder(context)
            .setRenderersFactory(renderersFactory)
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
