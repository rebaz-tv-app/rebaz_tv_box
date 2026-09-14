package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Category

@Composable
fun CategoryList(
    categories: List<Category>,
    selectedCategoryId: String,
    onCategoryClick: (String) -> Unit,
    onSearchCategoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(start = 4.dp, end = 6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Top Search in Categories pill
        CategorySearchCard(onClick = onSearchCategoryClick)

        // Categories list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxHeight()
        ) {
            items(categories, key = { it.id }) { cat ->
                CategoryItemCard(
                    category = cat,
                    isSelected = cat.id == selectedCategoryId,
                    onClick = { onCategoryClick(cat.id) }
                )
            }
        }
    }
}

@Composable
private fun CategorySearchCard(
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val borderColor = if (isFocused) Color(0xFFFFD500) else Color(0xFF1B3B6F)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(34.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF0C2042))
            .border(1.dp, borderColor, RoundedCornerShape(6.dp))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .focusable(interactionSource = interactionSource)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "گەڕان لە پۆلەکاندا",
            color = Color(0xFF94A3B8),
            fontSize = 12.sp,
            maxLines = 1,
            softWrap = false,
            textAlign = TextAlign.End
        )
        Spacer(modifier = Modifier.width(6.dp))
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = Color(0xFF64748B),
            modifier = Modifier.size(15.dp)
        )
    }
}

@Composable
private fun CategoryItemCard(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val hasActiveFocus = isFocused || isSelected

    val backgroundColor = when {
        isSelected -> Color(0xFF102D5E)
        isFocused -> Color(0xFF132F61)
        else -> Color(0xFF0C2042)
    }

    val borderColor = when {
        isSelected || isFocused -> Color(0xFFFFD500) // Golden yellow border from user's photo!
        else -> Color(0xFF173663)
    }

    val borderWidth = if (isSelected || isFocused) 2.2.dp else 1.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(borderWidth, borderColor, RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .focusable(interactionSource = interactionSource)
            .padding(horizontal = 12.dp)
            .testTag("category_item_${category.id}"),
        contentAlignment = Alignment.CenterEnd
    ) {
        Text(
            text = category.nameKurdish,
            color = if (hasActiveFocus) Color(0xFFFFE66D) else Color(0xFFCBD5E1),
            fontSize = 14.sp,
            fontWeight = if (hasActiveFocus) FontWeight.Bold else FontWeight.Medium,
            textAlign = TextAlign.End
        )
    }
}
