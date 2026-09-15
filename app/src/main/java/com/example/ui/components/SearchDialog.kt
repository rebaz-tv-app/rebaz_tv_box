package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Channel

@Composable
fun SearchDialog(
    searchQuery: String,
    searchResults: List<Channel>,
    onQueryChange: (String) -> Unit,
    onChannelSelect: (Channel) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, Color(0xFFFFD500), RoundedCornerShape(16.dp)),
            color = Color(0xFF08152E)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxHeight()
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "گەڕان",
                            tint = Color(0xFFFFD500),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "گەڕان بەپێی ناوی کەناڵ یان ژمارە",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF132F61))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "داخستن",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Search Input Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onQueryChange,
                    placeholder = {
                        Text(
                            text = "ناوی کەناڵ یان ژمارە بنووسە (بۆ نموونە: Shna یان 5)",
                            color = Color(0xFF64748B)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_input_field"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFFD500),
                        unfocusedBorderColor = Color(0xFF254B8C),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF0E234A),
                        unfocusedContainerColor = Color(0xFF0E234A)
                    ),
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "پاککردنەوە",
                                    tint = Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Number Dial for Remote D-Pad (0 - 9)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "ژمارەکان:",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    (1..9).forEach { num ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF132F61))
                                .clickable { onQueryChange(searchQuery + num) }
                                .border(1.dp, Color(0xFF254B8C), RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = num.toString(),
                                color = Color(0xFFFFD500),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF132F61))
                            .clickable { onQueryChange(searchQuery + "0") }
                            .border(1.dp, Color(0xFF254B8C), RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "0",
                            color = Color(0xFFFFD500),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Results header
                Text(
                    text = if (searchQuery.isBlank()) "هەموو کەناڵەکان بەردەستن لە لیستی سەرەکیدا" else "ئەنجامەکان (${searchResults.size}):",
                    color = Color(0xFF93C5FD),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Results List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(searchResults, key = { it.id }) { channel ->
                        SearchResultItem(
                            channel = channel,
                            onClick = {
                                onChannelSelect(channel)
                                onDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultItem(
    channel: Channel,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0F2753))
            .border(1.dp, Color(0xFF1E3D75), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF1D4ED8)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = channel.number.toString(),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = channel.name,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = channel.subtitle,
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
            }
        }
    }
}
