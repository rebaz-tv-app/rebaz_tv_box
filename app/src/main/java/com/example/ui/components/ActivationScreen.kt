package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

    // لێرەدا کۆدە نهێنییەکە بنووسە کە دەتەوێت خەڵک پێی چالاک بکات
    val CORRECT_CODE = "REBAZ2026" 

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080D1A)), // ڕەنگی باکگراوندی دەرەوە
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(420.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF111827)) // ڕەنگی باکگراوندی بۆکسەکە
                .padding(32.dp)
        ) {
            // ئایکۆنی کلیلەکە
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color(0x20FFC107)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.VpnKey,
                    contentDescription = "Key",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // سەردێڕ
            Text(
                text = "چالاککردنی ئەپڵیکەیشن",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // تێکستی ڕوونکردنەوە
            Text(
                text = "تکایە کۆدی چالاکبوون بنووسە بۆ چالاککردنی تەواوی ئەپەکە و بینینی هەموو کەناڵەکان.",
                color = Color(0xFF9CA3AF),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // شوێنی نووسینی کۆد
            OutlinedTextField(
                value = codeInput,
                onValueChange = {
                    codeInput = it
                    isError = false
                },
                placeholder = {
                    Text("کۆدەکە لێرە بنووسە", color = Color(0xFF6B7280), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFF374151),
    unfocusedBorderColor = Color(0xFF1F2937),
    focusedContainerColor = Color(0xFF0B0F19),
    unfocusedContainerColor = Color(0xFF0B0F19),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White
),

                singleLine = true,
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontSize = 18.sp),
                isError = isError
            )

            if (isError) {
                Text("کۆدەکە هەڵەیە، تکایە دڵنیابەرەوە", color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // دوگمەکان
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // دوگمەی داواکردنی کۆد (تێلیگرام)
                Button(
                    onClick = {
                        // لێرەدا یوزەری تێلیگرامی خۆت دابنە
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/rebaz92"))
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "تکایە تێلیگرام دابەزێنە", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Send, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("داوای کۆد بکە", color = Color(0xFF60A5FA), fontSize = 14.sp)
                    }
                }

                // دوگمەی چالاککردن
                Button(
                    onClick = {
                        if (codeInput == CORRECT_CODE) { // ئەگەر کۆدەکە ڕاست بوو
                            // سەیڤی دەکات بۆ ئەوەی جاری داهاتوو داوای نەکاتەوە
                            val prefs = context.getSharedPreferences("rebaz_tv_prefs", Context.MODE_PRIVATE)
                            prefs.edit().putBoolean("is_activated", true).apply()
                            onActivated()
                        } else {
                            isError = true
                        }
                    },
                    modifier = Modifier
                        .weight(1.5f)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color(0xFFFF9800), Color(0xFFFFC107))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("چالاککردن", color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

