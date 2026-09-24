package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.TvOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.exoplayer.ExoPlayer
import com.example.R
import com.example.data.model.Channel
import com.example.player.PlaybackUiState

@Composable
fun PreviewPlayerBox(
    player: ExoPlayer,
    currentChannel: Channel?,
    playbackState: PlaybackUiState,
    currentTimeString: String,
    onDoubleClickToFullscreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val logoResId = when (currentChannel?.name) {
        "Rebaz Sport 1" -> R.drawable.rebaz_sport_1
        "Rebaz Sport 2" -> R.drawable.rebaz_sport_2
        "Rebaz Sport 3" -> R.drawable.rebaz_sport_3
        "Rebaz Sport 4" -> R.drawable.rebaz_sport_4
        "Rebaz Sport 5" -> R.drawable.rebaz_sport_5
        "Rebaz WWE" -> R.drawable.rebaz_wwe
        else -> null
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9.5f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black)
                .border(1.5.dp, Color(0xFF1E3A6B), RoundedCornerShape(12.dp))
                .clickable { onDoubleClickToFullscreen() }
                .testTag("preview_player_box")
        ) {
            VideoPlayerSurface(
                player = player,
                modifier = Modifier.fillMaxSize()
            )

            if (playbackState is PlaybackUiState.Buffering) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x55000000)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFFFFD500),
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            if (playbackState is PlaybackUiState.Error) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xF0080E1C)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0x20EF4444)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TvOff,
                                contentDescription = "کارا نیە",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Text(
                            text = "پەخشی ئەم کەناڵە لە ئێستادا کارا نیە",
                            color = Color(0xFFF8FAFC),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            if (playbackState is PlaybackUiState.Playing) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .align(Alignment.TopStart) 
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0x99000000))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF22C55E))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "LIVE HD",
                            color = Color(0xFFE2E8F0),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // ⭐ لۆگۆکە هەمیشە دەردەکەوێت و سەد لە سەد دەچێتە لای ڕاست
            if (logoResId != null) {
                Image(
                    painter = painterResource(id = logoResId),
                    contentDescription = "Channel Logo",
                    modifier = Modifier
                        .align(Alignment.TopRight)
                        .absolutePadding(top = 10.dp, right = 25.dp)
                        .width(85.dp)
                        .height(35.dp)
                )
            }

            if (playbackState !is PlaybackUiState.Error) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color(0xE6000000), Color(0xF5000000))
                            )
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentChannel?.subtitle
                                ?: "ئاگاداری دڵیاکی و میواندۆستییە، بەڵام تەنها کاتێک بێکەرەوە کە زستان تەواو بوو. کاک محەمەد",
                            color = Color(0xFFFFE57F),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = currentTimeString.ifBlank { "04:27:31 EBL" },
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = currentChannel?.name ?: "Shna Documentary",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "کلیکی دووەم بکە بۆ پڕکردنی شاشە (Fullscreen) • دوگمەی OK",
                    color = Color(0xFF93C5FD),
                    fontSize = 11.sp
                )
            }

            IconButton(
                onClick = onDoubleClickToFullscreen,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF132F61))
                    .testTag("btn_expand_fullscreen")
            ) {
                Icon(
                    imageVector = Icons.Default.Fullscreen,
                    contentDescription = "Full Screen",
                    tint = Color(0xFFFFD500),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
