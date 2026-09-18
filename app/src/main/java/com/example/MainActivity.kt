package com.example

import android.os.Bundle
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.ui.RebazTvMainScreen
import com.example.ui.TvViewModel
import com.example.ui.components.ActivationScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: TvViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // شاردنەوەی سەعات، پاتری و هێڵەکانی سەرەوە بۆ ئەوەی ئەپەکە فول شاشە بێت
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                val prefs = context.getSharedPreferences("rebaz_tv_prefs", Context.MODE_PRIVATE)
                
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
    }
}
