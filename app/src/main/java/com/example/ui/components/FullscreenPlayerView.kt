package com.example.ui.components

import android.view.KeyEvent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.TvOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.exoplayer.ExoPlayer
import com.example.R
import com.example.data.model.Channel
import com.example.player.PlaybackUiState
import kotlinx.coroutines.delay

@Composable
fun FullscreenPlayerView(
    player: ExoPlayer,
    channel: Channel?,
    categoryName: String,
    playbackState: PlaybackUiState,
    showOsd: Boolean,
    onBackToPreview: () -> Unit,
    onChannelUp: () -> Unit,
    onChannelDown: () -> Unit,
    onToggleFavorite: () -> Unit,
    onTriggerOsd: () -> Unit,
    modifier: Modifier = Modifier
) {
    var logoResId: Int? = null
    var logoWidth = 0.dp
    var logoHeight = 0.dp
    var logoTop = 0.dp
    var logoRight = 0.dp

    when (channel?.name) {
        "Rebaz Sport 1" -> {
            logoResId = R.drawable.rebaz_sport_1
            logoWidth = 105.dp
            logoHeight = 27.dp
            logoTop = 15.dp
            logoRight = 120.dp
        }
        "Rebaz Sport 2" -> {
            logoResId = R.drawable.rebaz_sport_2
            logoWidth = 105.dp
            logoHeight = 27.dp
            logoTop = 15.dp
            logoRight = 120.dp
        }
        "Rebaz Sport 3" -> {
            logoResId = R.drawable.rebaz_sport_3
            logoWidth = 105.dp
            logoHeight = 27.dp
            logoTop = 15.dp
            logoRight = 120.dp
        }
        "Rebaz Sport 4" -> {
            logoResId = R.drawable.rebaz_sport_4
            logoWidth = 105.dp
            logoHeight = 27.dp
            logoTop = 12.dp
            logoRight = 100.dp
        }
        "Rebaz Sport 5" -> {
            logoResId = R.drawable.rebaz_sport_5
            logoWidth = 105.dp
            logoHeight = 27.dp
            logoTop = 12.dp
            logoRight = 100.dp
        }
        "Rebaz WWE" -> {
            logoResId = R.drawable.rebaz_wwe
            logoWidth = 105.dp
            logoHeight = 27.dp
            logoTop = 12.dp
            logoRight = 100.dp
        }
    }

    val finalLogoResId = logoResId

    BackHandler {
        onBackToPreview()
    }

    val focusRequester = remember { FocusRequester() }
    var showControls by remember { mutableStateOf(true) }
    var resetTimer by remember { mutableIntStateOf(0) }

    LaunchedEffect(showControls, resetTimer) {
        if (showControls) {
            delay(4000)
            showControls = false
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { keyEvent ->
                showControls = true
                resetTimer++
                
                if (keyEvent.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) {
                    when (keyEvent.nativeKeyEvent.keyCode) {
                        KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_CHANNEL_UP, KeyEvent.KEYCODE_PAGE_UP -> {
                            onChannelUp()
                            true
                        }
                        KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_CHANNEL_DOWN, KeyEvent.KEYCODE_PAGE_DOWN -> {
                            onChannelDown()
                            true
                        }
                        KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_NUMPAD_ENTER -> {
                            onTriggerOsd()
                            true
                        }
                        KeyEvent.KEYCODE_BACK, KeyEvent.KEYCODE_ESCAPE -> {
                            onBackToPreview()
                            true
                        }
                        else -> false
                    }
                } else false
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onTriggerOsd()
                showControls = !showControls
                if (showControls) resetTimer++
            }
            .testTag("fullscreen_player_view")
    ) {
        VideoPlayerSurface(
            player = player,
            modifier = Modifier.fillMaxSize()
        )

        // ⭐ باکگراوندێکی ڕەش دروست دەکەین کە لە لای ڕاستەوە درێژ دەبێتەوە بۆ شاردنەوەی لۆگۆی ئەسڵی
        if (finalLogoResId != null) {
            Box(
                modifier = Modifier
                    .align(AbsoluteAlignment.TopRight)
                    .absolutePadding(top = logoTop)
                    .width(logoWidth + logoRight) // پانتایی باکگراوندەکە بریتییە لە قەبارەی لۆگۆ + بۆشایی لای ڕاست
                    .height(logoHeight)
                    .background(Color.Black) // ڕەنگی ڕەش بۆ شاردنەوە
            ) {
                Image(
                    painter = painterResource(id = finalLogoResId),
                    contentDescription = "Channel Logo",
                    modifier = Modifier
                        .align(AbsoluteAlignment.CenterLeft) // لۆگۆکەی تۆ دەخەینە لای چەپی ئەم سندوقە ڕەشەوە
                        .width(logoWidth)
                        .height(logoHeight),
                    contentScale = ContentScale.FillBounds
                )
            }
        }

        if (playbackState is PlaybackUiState.Buffering) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x33000000)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color(0xFFFFD500),
                    strokeWidth = 4.dp,
                    modifier = Modifier.size(54.dp)
                )
            }
        }

        if (playbackState is PlaybackUiState.Error) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xDD080E1C)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xE60F172A))
                        .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(12.dp))
                        .padding(horizontal = 28.dp, vertical = 20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.TvOff,
                        contentDescription = "کارا نیە",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(44.dp)
                    )
                    Text(
                        text = "پەخشی ئەم کەناڵە لە ئێستادا کارا نیە",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = {
                        onChannelUp()
                        showControls = true
                        resetTimer++
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0x800B1A3B))
                        .border(1.dp, Color(0xFFFFD500), CircleShape)
                        .testTag("btn_fs_channel_up")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "کەناڵی پێشوو",
                        tint = Color(0xFFFFD500)
                    )
                }

                IconButton(
                    onClick = {
                        onChannelDown()
                        showControls = true
                        resetTimer++
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0x800B1A3B))
                        .border(1.dp, Color(0xFFFFD500), CircleShape)
                        .testTag("btn_fs_channel_down")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = "کەناڵی دواتر",
                        tint = Color(0xFFFFD500)
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = showOsd,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopStart) 
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xCC000000), Color.Transparent)
                        )
                    )
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0x990F244A))
                        .border(1.dp, Color(0xFFFFD500), RoundedCornerShape(20.dp))
                        .clickable { onBackToPreview() }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .testTag("btn_fs_back")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "گەڕانەوە بۆ شاشەی سەرەکی",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "گەڕانەوە بۆ پریڤیو (Back)",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "REBAZ TV • دۆخی تەواوی شاشە",
                    color = Color(0xFF93C5FD),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(end = 150.dp) 
                )
            }
        }

        AnimatedVisibility(
            visible = showOsd,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color(0xE608142A), Color(0xFA050E20))
                        )
                    )
                    .padding(horizontal = 28.dp, vertical = 18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${channel?.number?.toString()?.padStart(2, '0') ?: "01"} - ${channel?.name ?: "Channel"}",
                                    color = Color.White,
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF1D4ED8))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = categoryName,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "${channel?.subtitle ?: ""} • دوگمەی سەر/خوار  بۆ گۆڕینی کەناڵ",
                                color = Color(0xFFFFD500),
                                fontSize = 12.sp
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0x80132F61))
                                .border(1.dp, Color(0x60FFFFFF), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (channel?.isFavorite == true) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "دڵخواز",
                                tint = if (channel?.isFavorite == true) Color(0xFFEF4444) else Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                            onClick = onBackToPreview,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0x80132F61))
                                .border(1.dp, Color(0xFFFFD500), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FullscreenExit,
                                contentDescription = "Exit Fullscreen",
                                tint = Color(0xFFFFD500),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
