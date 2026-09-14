package com.example.ui

import android.view.KeyEvent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.BottomBar
import com.example.ui.components.CategoryList
import com.example.ui.components.ChannelCard
import com.example.ui.components.FullscreenPlayerView
import com.example.ui.components.PreviewPlayerBox
import com.example.ui.components.SearchDialog
import com.example.ui.components.TopBar

@Composable
fun RebazTvMainScreen(
    viewModel: TvViewModel,
    modifier: Modifier = Modifier
) {
    val isFullscreen by viewModel.isFullscreen.collectAsStateWithLifecycle()
    val playingChannel by viewModel.playingChannel.collectAsStateWithLifecycle()
    val focusedChannelId by viewModel.focusedChannelId.collectAsStateWithLifecycle()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsStateWithLifecycle()
    val channels by viewModel.currentCategoryChannels.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val showOsd by viewModel.showOsd.collectAsStateWithLifecycle()
    val currentTimeString by viewModel.currentTimeString.collectAsStateWithLifecycle()

    val isSearchDialogOpen by viewModel.isSearchDialogOpen.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()

    val categories by viewModel.categories.collectAsStateWithLifecycle()

    val currentCategory = categories.find { it.id == selectedCategoryId }
    val categoryName = currentCategory?.nameKurdish ?: "دۆکۆمێنتاری"

    if (isFullscreen) {
        FullscreenPlayerView(
            player = viewModel.playerManager.getPlayer(),
            channel = playingChannel,
            categoryName = categoryName,
            playbackState = playbackState,
            showOsd = showOsd,
            onBackToPreview = { viewModel.exitFullscreen() },
            onChannelUp = { viewModel.onChannelUp() },
            onChannelDown = { viewModel.onChannelDown() },
            onToggleFavorite = { playingChannel?.let { viewModel.toggleFavorite(it.id) } },
            onTriggerOsd = { viewModel.triggerOsd() }
        )
    } else {
        // Intercept Back button in main preview: if search is open, close it; else normal back
        BackHandler(enabled = isSearchDialogOpen) {
            if (isSearchDialogOpen) viewModel.closeSearch()
        }

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF061229),
                            Color(0xFF091A3A),
                            Color(0xFF06142B)
                        )
                    )
                )
                .onKeyEvent { keyEvent ->
                    if (keyEvent.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) {
                        when (keyEvent.nativeKeyEvent.keyCode) {
                            KeyEvent.KEYCODE_SEARCH -> {
                                viewModel.openSearch()
                                true
                            }
                            else -> false
                        }
                    } else false
                }
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Bar with TV icons, Search pill, REBAZ TV header
                TopBar(
                    onSearchClick = { viewModel.openSearch() },
                    onSettingsClick = { /* Settings */ }
                )

                // Main 3-column body matching screenshot
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // Left column: Video Preview Box (~46% width)
                    PreviewPlayerBox(
                        player = viewModel.playerManager.getPlayer(),
                        currentChannel = playingChannel,
                        playbackState = playbackState,
                        currentTimeString = currentTimeString,
                        onDoubleClickToFullscreen = { viewModel.enterFullscreen() },
                        modifier = Modifier.weight(0.46f)
                    )

                    // Middle column: Channels List (~26% width)
                    LazyColumn(
                        modifier = Modifier
                            .weight(0.26f)
                            .padding(horizontal = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(channels, key = { it.id }) { channel ->
                            ChannelCard(
                                channel = channel,
                                isPlaying = playingChannel?.id == channel.id,
                                isFocusedItem = focusedChannelId == channel.id,
                                onClick = { viewModel.onChannelClick(channel) },
                                onToggleFavorite = { viewModel.toggleFavorite(channel.id) }
                            )
                        }
                    }

                    // Right column: Categories List (~28% width)
                    CategoryList(
                        categories = categories,
                        selectedCategoryId = selectedCategoryId,
                        onCategoryClick = { viewModel.selectCategory(it) },
                        onSearchCategoryClick = { viewModel.openSearch() },
                        modifier = Modifier.weight(0.28f)
                    )
                }

                // Bottom Bar with quick actions ("دڵخوازەکان", "ئەڕشیف", "LGNANO")
                BottomBar(
                    onFavoritesClick = { viewModel.selectCategory("favorites") },
                    onArchiveClick = { viewModel.selectCategory("doc") }
                )
            }

            // Search Dialog
            if (isSearchDialogOpen) {
                SearchDialog(
                    searchQuery = searchQuery,
                    searchResults = searchResults,
                    onQueryChange = { viewModel.updateSearchQuery(it) },
                    onChannelSelect = { channel ->
                        viewModel.playChannel(channel, saveAsLast = true)
                    },
                    onDismiss = { viewModel.closeSearch() }
                )
            }
        }
    }
}
