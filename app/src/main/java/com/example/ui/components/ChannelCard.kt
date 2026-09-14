package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Channel

@Composable
fun ChannelCard(
    channel: Channel,
    isPlaying: Boolean,
    isFocusedItem: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isSystemFocused by interactionSource.collectIsFocusedAsState()

    val hasActiveFocus = isSystemFocused || isFocusedItem

    // Card styling matching screenshot
    val backgroundColor = when {
        hasActiveFocus -> Color(0xFF132F61)
        isPlaying -> Color(0xFF0F2650)
        else -> Color(0xFF0B1F42)
    }

    val borderColor = when {
        hasActiveFocus -> Color(0xFFFFD500) // Golden yellow border from user's photo!
        isPlaying -> Color(0xFF2563EB)
        else -> Color(0xFF142C56)
    }

    val borderWidth = if (hasActiveFocus) 2.2.dp else 1.dp

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(borderWidth, borderColor, RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .focusable(interactionSource = interactionSource)
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("channel_card_${channel.id}"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Channel name & play indicator
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            if (isPlaying) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Playing",
                    tint = Color(0xFFFFD500),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }

            Text(
                text = channel.name,
                color = if (hasActiveFocus) Color(0xFFFFE66D) else Color.White,
                fontSize = 14.sp,
                fontWeight = if (hasActiveFocus || isPlaying) FontWeight.Bold else FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (channel.isFavorite) {
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "دڵخوازەکان",
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Right side badge/logo matching the screenshot
        ChannelLogoBadge(badgeText = channel.logoBadge, channelId = channel.id)
    }
}

@Composable
fun ChannelLogoBadge(badgeText: String, channelId: String) {
    val cleanText = if (badgeText.startsWith("http", ignoreCase = true) || badgeText.contains("/")) {
        "HD"
    } else {
        badgeText.take(5).uppercase().ifBlank { "HD" }
    }

    // Unique color theme for channel badges to look vivid like real TV logos
    val gradient = when {
        cleanText.contains("Stv", ignoreCase = true) -> listOf(Color(0xFFEAB308), Color(0xFFCA8A04))
        cleanText.contains("K24", ignoreCase = true) -> listOf(Color(0xFF0284C7), Color(0xFF0369A1))
        cleanText.contains("KURD", ignoreCase = true) -> listOf(Color(0xFF9333EA), Color(0xFF7E22CE))
        cleanText.contains("RENG", ignoreCase = true) -> listOf(Color(0xFF16A34A), Color(0xFF15803D))
        cleanText.contains("ASTERA", ignoreCase = true) -> listOf(Color(0xFF2563EB), Color(0xFF1D4ED8))
        cleanText.contains("sima", ignoreCase = true) -> listOf(Color(0xFFDC2626), Color(0xFFB91C1C))
        cleanText.contains("MMN", ignoreCase = true) -> listOf(Color(0xFFF97316), Color(0xFFC2410C))
        cleanText.contains("RUDAW", ignoreCase = true) -> listOf(Color(0xFFE11D48), Color(0xFFBE123C))
        else -> listOf(Color(0xFF334155), Color(0xFF1E293B))
    }

    Box(
        modifier = Modifier
            .size(38.dp, 28.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Brush.linearGradient(gradient))
            .border(1.dp, Color(0x40FFFFFF), RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = cleanText,
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1
        )
    }
}
