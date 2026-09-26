package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivationScreen(onActivated: () -> Unit) {
    val context = LocalContext.current
    var codeInput by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    // کۆدە چالاککردنەکەی ئێستات (دەتوانیت هەر کاتێک ویستت لێرە بیگۆڕیت)
    val CORRECT_CODE = "1122"

    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "glowScale"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "glowAlpha"
    )

    // ⭐ دروستکردنی InteractionSource بۆ زانینی ئەوەی پەنجەی لەسەرە یان نا
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // دیاریکردنی ڕەنگەکان بەپێی ئەوەی پەنجەی لەسەرە یان نا
    val buttonColors = if (isPressed) {
        listOf(Color(0xFF4CAF50), Color(0xFF81C784)) // سەوز لە کاتی دەست لێدان
    } else {
        listOf(Color(0xFFFF9800), Color(0xFFFFC107)) // زەرد لە باری ئاساییدا
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080D1A)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .widthIn(max = 500.dp) 
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF111827))
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp, horizontal = 20.dp)
        ) {
            
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(64.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .graphicsLayer { scaleX = glowScale; scaleY = glowScale; alpha = glowAlpha }
                        .clip(CircleShape)
                        .background(Brush.radialGradient(colors = listOf(Color(0xFFFFC107), Color.Transparent)))
                )
                
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1F2937))
                        .shadow(6.dp, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.VpnKey, contentDescription = "Key", tint = Color(0xFFFFC107), modifier = Modifier.size(24.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "چالاککردنی ئەپڵیکەیشن", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "تکایە کۆدی چالاکبوون بنووسە بۆ چالاککردنی تەواوی ئەپەکە و بینینی هەموو کەناڵەکان. تێبینی: کۆدی ئەکتیڤکردن مانگی جارێک دەگۆڕێت بۆ دەستگەیشتن بە کۆدی نوێ سەردانی چەناڵی REBAZ TV بکە لە تێلیگرام.",
                color = Color(0xFF9CA3AF), fontSize = 12.sp, textAlign = TextAlign.Center, lineHeight = 18.sp, modifier = Modifier.padding(horizontal = 4.dp)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(text = "https://t.me/rebaz_tv", color = Color(0xFF60A5FA), fontSize = 12.sp)

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = codeInput,
                onValueChange = { codeInput = it; isError = false },
                placeholder = { Text("کۆدەکە لێرە بنووسە", color = Color(0xFF6B7280), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                modifier = Modifier.fillMaxWidth(0.85f).height(54.dp),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF374151), unfocusedBorderColor = Color(0xFF1F2937),
                    focusedContainerColor = Color(0xFF0B0F19), unfocusedContainerColor = Color(0xFF0B0F19),
                    focusedTextColor = Color.White, unfocusedTextColor = Color.White
                ),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontSize = 16.sp, fontWeight = FontWeight.Bold),
                isError = isError
            )

            if (isError) {
                Text("کۆدەکە هەڵەیە، تکایە دڵنیابەرەوە", color = Color.Red, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp, bottom = 4.dp))
            } else {
                Spacer(modifier = Modifier.height(14.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/rebaz_tv"))
                        try { context.startActivity(intent) } catch (e: Exception) { Toast.makeText(context, "تکایە تێلیگرام دابەزێنە", Toast.LENGTH_SHORT).show() }
                    },
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        Icon(Icons.Default.Send, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("داوای کۆد بکە", color = Color(0xFF60A5FA), fontSize = 13.sp)
                    }
                }

                Button(
                    onClick = {
                        if (codeInput.trim() == CORRECT_CODE) { 
                            val prefs = context.getSharedPreferences("rebaz_tv_prefs", Context.MODE_PRIVATE)
                            prefs.edit().putBoolean("is_activated_v1", true).apply()
                            onActivated()
                        } else {
                            isError = true
                        }
                    },
                    modifier = Modifier.weight(1.3f).height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(),
                    interactionSource = interactionSource // ⭐ پێدانی InteractionSource بە دوگمەکە
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.horizontalGradient(colors = buttonColors)), // ⭐ بەکارهێنانی ڕەنگە گۆڕاوەکان
                        contentAlignment = Alignment.Center
                    ) {
                        // گۆڕینی ڕەنگی نوسینەکە بۆ سپی لە کاتی سەوزبوونی دوگمەکەدا
                        Text(
                            "چالاککردن", 
                            color = if (isPressed) Color.White else Color.Black, 
                            fontSize = 15.sp, 
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
