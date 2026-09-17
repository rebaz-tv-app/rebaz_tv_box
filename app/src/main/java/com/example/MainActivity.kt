package com.example

import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ui.RebazTvMainScreen
import com.example.ui.TvViewModel
import com.example.ui.theme.MyApplicationTheme
import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.ui.components.ActivationScreen


class MainActivity : ComponentActivity() {

    private val viewModel: TvViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // ئەم بەشە نوێیە: بۆ شاردنەوەی شریتی سەرەوە و هێشتنەوەی دوگمەکانی خوارەوە
        WindowCompat.getInsetsController(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.statusBars())
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

                setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                val prefs = context.getSharedPreferences("rebaz_tv_prefs", Context.MODE_PRIVATE)
                
                // سەیر دەکات بزانێت پێشتر چالاک کراوە یان نا
                var isActivated by remember { mutableStateOf(prefs.getBoolean("is_activated", false)) }

                if (isActivated) {
                    // ئەگەر چالاک کرابوو، شاشەی کەناڵەکان دەکرێتەوە
                    RebazTvMainScreen(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // ئەگەر چالاک نەکرابوو، شاشەی قوفڵەکە دەکرێتەوە
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
