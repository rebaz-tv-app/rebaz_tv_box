        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                val prefs = context.getSharedPreferences("rebaz_tv_prefs", Context.MODE_PRIVATE)
                
                // ** لێرەش دەبێت هەمان ناو بێت **
                var isActivated by remember { mutableStateOf(prefs.getBoolean("is_activated_v1", false)) }

                if (isActivated) {
                    RebazTvMainScreen(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    ActivationScreen(
                        onActivated = {
                            isActivated = true
                        }
                    )
                }
            }
        }
