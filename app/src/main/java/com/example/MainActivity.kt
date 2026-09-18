package com.example

import android.os.Bundle
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.ui.RebazTvMainScreen
import com.example.ui.TvViewModel
import com.example.ui.components.ActivationScreen
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private val viewModel: TvViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // شاردنەوەی سەعات و پاتری بۆ ئەوەی ئەپەکە بە تەواوی فول شاشە بێت
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        setContent {
            MyApplicationTheme {
                // کۆنتڕۆڵی پیشاندانی لۆگۆی سەرەتا (Splash)
                var showSplash by remember { mutableStateOf(true) }

                LaunchedEffect(Unit) {
                    delay(2500) // لۆگۆکە بۆ ماوەی ٢.٥ چرکە دەمێنێتەوە (گۆڕدرا بۆ کاتێکی گونجاوتر)
                    showSplash = false
                }

                if (showSplash) {
                    // شاشەی سپلاش (لۆگۆ لەسەر باکگراوندی تۆخ)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF080D1A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            // ناوی وێنەکە دەبێت splash بێت (بێ پیتە گەورە و خاڵ)
                            painter = painterResource(id = R.drawable.splash),
                            contentDescription = "App Logo",
                            modifier = Modifier.size(140.dp)
                        )
                    }
                } else {
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
}
