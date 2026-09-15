package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.TvDatabase
import com.example.data.model.Category
import com.example.data.model.Channel
import com.example.data.remote.PlaylistResult
import com.example.data.remote.RemotePlaylistFetcher
import com.example.data.repository.ChannelRepository
import com.example.player.PlaybackUiState
import com.example.player.TvPlayerManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TvViewModel(application: Application) : AndroidViewModel(application) {

    private val db = TvDatabase.getDatabase(application)
    private val repository = ChannelRepository(db.favoriteDao(), application)
    private val playlistFetcher = RemotePlaylistFetcher()
    val playerManager = TvPlayerManager(application)

    private val _categories = MutableStateFlow(repository.categories)
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow(repository.getLastCategoryId())
    val selectedCategoryId: StateFlow<String> = _selectedCategoryId.asStateFlow()

    private val _allChannels = MutableStateFlow<List<Channel>>(repository.getDefaultChannels())

    private val _focusedChannelId = MutableStateFlow<String?>(null)
    val focusedChannelId: StateFlow<String?> = _focusedChannelId.asStateFlow()

    private val _playingChannel = MutableStateFlow<Channel?>(null)
    val playingChannel: StateFlow<Channel?> = _playingChannel.asStateFlow()

    private val _isFullscreen = MutableStateFlow(false)
    val isFullscreen: StateFlow<Boolean> = _isFullscreen.asStateFlow()

    private val _showOsd = MutableStateFlow(false)
    val showOsd: StateFlow<Boolean> = _showOsd.asStateFlow()
    private var osdHideJob: Job? = null

    // Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearchDialogOpen = MutableStateFlow(false)
    val isSearchDialogOpen: StateFlow<Boolean> = _isSearchDialogOpen.asStateFlow()

    // Live clock string matching "04:27:31 EBL" in screenshot
    private val _currentTimeString = MutableStateFlow("")
    val currentTimeString: StateFlow<String> = _currentTimeString.asStateFlow()

    val playbackState: StateFlow<PlaybackUiState> = playerManager.playbackState

    val favoriteIds: StateFlow<List<String>> = repository.favoriteIds.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    // Current category's channels combined with favorites & custom channels
    val currentCategoryChannels: StateFlow<List<Channel>> = combine(
        _selectedCategoryId,
        _allChannels,
        repository.customChannels,
        favoriteIds
    ) { categoryId, defaultChans, customChans, favIds ->
        val merged = (defaultChans + customChans).map { ch ->
            ch.copy(isFavorite = favIds.contains(ch.id))
        }

        when (categoryId) {
            "all" -> merged
            "favorites" -> merged.filter { it.isFavorite }
            else -> merged.filter { it.category == categoryId }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    // Filtered channels for Search (by name or by number)
    val searchResults: StateFlow<List<Channel>> = combine(
        _allChannels,
        repository.customChannels,
        favoriteIds,
        _searchQuery
    ) { defaultChans, customChans, favIds, query ->
        if (query.isBlank()) return@combine emptyList<Channel>()
        val cleanQuery = query.trim().lowercase()
        val merged = (defaultChans + customChans).map { ch ->
            ch.copy(isFavorite = favIds.contains(ch.id))
        }
        merged.filter { ch ->
            ch.name.lowercase().contains(cleanQuery) ||
            ch.number.toString() == cleanQuery ||
            ch.subtitle.lowercase().contains(cleanQuery)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    init {
        // Start live clock
        viewModelScope.launch {
            val sdf = SimpleDateFormat("hh:mm:ss 'EBL'", Locale.US)
            while (isActive) {
                _currentTimeString.value = sdf.format(Date())
                delay(1000)
            }
        }

        // Resume last played channel
        viewModelScope.launch {
            val lastChanId = repository.getLastPlayedChannelId()
            val initialChannels = repository.getDefaultChannels()
            val initialChan = initialChannels.find { it.id == lastChanId }
                ?: initialChannels.find { it.category == _selectedCategoryId.value }
                ?: initialChannels.first()

            _focusedChannelId.value = initialChan.id
            playChannel(initialChan, saveAsLast = false)

            // Auto-fetch latest channels from GitHub playlist silently in background!
            startAutoSync()
        }
    }

    fun selectCategory(categoryId: String) {
        _selectedCategoryId.value = categoryId
        // Update focused channel to first channel in this category if available
        viewModelScope.launch {
            delay(50)
            val channels = currentCategoryChannels.value
            if (channels.isNotEmpty()) {
                val currentPlaying = _playingChannel.value
                if (currentPlaying != null && channels.any { it.id == currentPlaying.id }) {
                    _focusedChannelId.value = currentPlaying.id
                } else {
                    _focusedChannelId.value = channels.first().id
                }
            }
        }
    }

    /**
     * Handles OK click on a channel:
     * - 1st click: plays in preview screen
     * - 2nd click: switches to fullscreen
     */
    fun onChannelClick(channel: Channel) {
        _focusedChannelId.value = channel.id
        val currentlyPlaying = _playingChannel.value

        if (currentlyPlaying?.id == channel.id) {
            // Second click on same playing channel -> Enter Fullscreen!
            enterFullscreen()
        } else {
            // First click -> play in small preview window
            playChannel(channel, saveAsLast = true)
        }
    }

    fun playChannel(channel: Channel, saveAsLast: Boolean = true) {
        _playingChannel.value = channel
        playerManager.playStream(channel.streamUrl)
        if (saveAsLast) {
            repository.saveLastPlayedChannel(channel.id, _selectedCategoryId.value)
        }
        // If in fullscreen, show OSD banner briefly
        if (_isFullscreen.value) {
            triggerOsd()
        }
    }

    fun setFocusedChannel(channelId: String) {
        _focusedChannelId.value = channelId
    }

    fun enterFullscreen() {
        _isFullscreen.value = true
        triggerOsd()
    }

    fun exitFullscreen(): Boolean {
        if (_isFullscreen.value) {
            _isFullscreen.value = false
            _showOsd.value = false
            return true // handled
        }
        return false // not handled
    }

    /**
     * D-Pad Up / Down in Fullscreen:
     * Changes channel within the same package/category without leaving fullscreen!
     */
    fun onChannelUp() {
        switchChannelRelative(-1)
    }

    fun onChannelDown() {
        switchChannelRelative(1)
    }

    private fun switchChannelRelative(delta: Int) {
        val channels = currentCategoryChannels.value
        if (channels.isEmpty()) return

        val current = _playingChannel.value
        val currentIndex = channels.indexOfFirst { it.id == current?.id }
        val nextIndex = if (currentIndex == -1) {
            0
        } else {
            val newIdx = (currentIndex + delta) % channels.size
            if (newIdx < 0) channels.size - 1 else newIdx
        }

        val nextChannel = channels[nextIndex]
        _focusedChannelId.value = nextChannel.id
        playChannel(nextChannel, saveAsLast = true)
    }

    fun triggerOsd() {
        _showOsd.value = true
        osdHideJob?.cancel()
        osdHideJob = viewModelScope.launch {
            delay(3500)
            _showOsd.value = false
        }
    }

    fun toggleFavorite(channelId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(channelId)
            // Update currently playing favorite flag if needed
            val current = _playingChannel.value
            if (current?.id == channelId) {
                _playingChannel.value = current.copy(isFavorite = !current.isFavorite)
            }
        }
    }

    fun openSearch() {
        _isSearchDialogOpen.value = true
        _searchQuery.value = ""
    }

    fun closeSearch() {
        _isSearchDialogOpen.value = false
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun startAutoSync() {
        viewModelScope.launch {
            while (true) {
                try {
                    val result = playlistFetcher.fetchPlaylist(repository.getPlaylistUrl())
                    if (result is PlaylistResult.Success && result.channels.isNotEmpty()) {
                        _allChannels.value = result.channels

                        // دروستکردنی پاکێجەکان ڕاستەوخۆ لەناو کەناڵەکانەوە
val newCategories = result.channels.map { it.category }
    .distinct()
    .filter { it != "all" && it != "favorites" }
    .map { Category(id = it, nameKurdish = it) }
    .toMutableList()

if (newCategories.none { it.id == "favorites" }) {
    newCategories.add(Category("favorites", "دڵخوازەکان"))
}
if (newCategories.none { it.id == "all" }) {
    newCategories.add(0, Category("all", "هەموو کەناڵەکان")) 
}
_categories.value = newCategories


                        val currentPlaying = _playingChannel.value
                        val exists = result.channels.any { it.id == currentPlaying?.id }
                        if (currentPlaying != null && !exists) {
                            val matchingInCat = result.channels.find { it.category == _selectedCategoryId.value }
                                ?: result.channels.first()
                            _focusedChannelId.value = matchingInCat.id
                            playChannel(matchingInCat, saveAsLast = true)
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("TvViewModel", "Silent auto-sync error: ${e.message}")
                }
                delay(3 * 60 * 1000L) // Check every 3 minutes for instant updates from GitHub
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerManager.release()
    }
}
