package com.example.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.UserRepository
import com.example.data.repository.VideoRepository
import com.example.domain.model.Draft
import com.example.domain.model.User
import com.example.domain.model.Video
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ProfileTab {
    MY_VIDEOS,
    LIKED,
    SAVED,
    DRAFTS
}

class ProfileViewModel(
    private val videoRepository: VideoRepository,
    private val userRepository: UserRepository,
    private val authRepository: com.example.data.repository.AuthRepository? = null
) : ViewModel() {

    private val _currentUser = MutableStateFlow(
        authRepository?.getCurrentUser() ?: userRepository.getCurrentUser()
    )
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    init {
        if (authRepository != null) {
            viewModelScope.launch {
                authRepository.authState.collect { authState ->
                    authState.user?.let { user ->
                        _currentUser.value = user
                    }
                }
            }
        }
    }

    private val _selectedTab = MutableStateFlow(ProfileTab.MY_VIDEOS)
    val selectedTab: StateFlow<ProfileTab> = _selectedTab.asStateFlow()

    val myVideos: StateFlow<List<Video>> = videoRepository.getAllVideos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val likedVideos: StateFlow<List<Video>> = videoRepository.getLikedVideos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedVideos: StateFlow<List<Video>> = videoRepository.getSavedVideos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val drafts: StateFlow<List<Draft>> = userRepository.getAllDrafts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(tab: ProfileTab) {
        _selectedTab.value = tab
    }

    fun updateProfile(displayName: String, bio: String, website: String) {
        val curr = _currentUser.value
        _currentUser.value = curr.copy(
            displayName = displayName,
            bio = bio,
            website = website
        )
    }

    fun deleteDraft(draftId: String) {
        viewModelScope.launch {
            userRepository.deleteDraft(draftId)
        }
    }
}
