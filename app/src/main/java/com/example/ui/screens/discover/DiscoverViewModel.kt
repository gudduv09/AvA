package com.example.ui.screens.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.mock.DemoDataProvider
import com.example.data.repository.UserRepository
import com.example.data.repository.VideoRepository
import com.example.domain.model.SoundTrack
import com.example.domain.model.User
import com.example.domain.model.Video
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SearchFilter {
    TOP,
    VIDEOS,
    USERS,
    SOUNDS,
    HASHTAGS
}

class DiscoverViewModel(
    private val videoRepository: VideoRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow(SearchFilter.TOP)
    val selectedFilter: StateFlow<SearchFilter> = _selectedFilter.asStateFlow()

    private val _recentSearches = MutableStateFlow(listOf("#AVA", "pinkwave", "cyberpunk", "#dance"))
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

    val trendingHashtags = DemoDataProvider.trendingHashtags
    val popularSounds = DemoDataProvider.popularSounds

    val allVideos: StateFlow<List<Video>> = videoRepository.getAllVideos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<User>> = userRepository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DemoDataProvider.creators)

    val filteredVideos: StateFlow<List<Video>> = combine(allVideos, _searchQuery) { list, query ->
        if (query.isBlank()) list
        else list.filter {
            it.caption.contains(query, ignoreCase = true) ||
            it.creator.username.contains(query, ignoreCase = true) ||
            it.hashtags.any { tag -> tag.contains(query, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredUsers: StateFlow<List<User>> = combine(allUsers, _searchQuery) { list, query ->
        if (query.isBlank()) list
        else list.filter {
            it.username.contains(query, ignoreCase = true) ||
            it.displayName.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectFilter(filter: SearchFilter) {
        _selectedFilter.value = filter
    }

    fun submitSearch(query: String) {
        if (query.isNotBlank() && !_recentSearches.value.contains(query)) {
            _recentSearches.value = listOf(query) + _recentSearches.value.take(5)
        }
    }

    fun clearRecentSearches() {
        _recentSearches.value = emptyList()
    }

    fun toggleFollow(user: User) {
        viewModelScope.launch {
            userRepository.toggleFollow(user.id, user.isFollowing)
        }
    }
}
