package com.example.ui.screens.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.mock.DemoDataProvider
import com.example.data.repository.InteractionRepository
import com.example.data.repository.UserRepository
import com.example.data.repository.VideoRepository
import com.example.domain.model.Comment
import com.example.domain.model.StoryItem
import com.example.domain.model.Video
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class FeedTab {
    FOR_YOU,
    FOLLOWING
}

class FeedViewModel(
    private val videoRepository: VideoRepository,
    private val userRepository: UserRepository,
    private val interactionRepository: InteractionRepository
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(FeedTab.FOR_YOU)
    val selectedTab: StateFlow<FeedTab> = _selectedTab.asStateFlow()

    private val _stories = MutableStateFlow(DemoDataProvider.stories)
    val stories: StateFlow<List<StoryItem>> = _stories.asStateFlow()

    val allVideos: StateFlow<List<Video>> = videoRepository.getAllVideos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val displayedVideos: StateFlow<List<Video>> = combine(allVideos, _selectedTab) { list, tab ->
        when (tab) {
            FeedTab.FOR_YOU -> list
            FeedTab.FOLLOWING -> list.filter { it.creator.isFollowing }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Modal Sheet states
    private val _activeCommentsVideo = MutableStateFlow<Video?>(null)
    val activeCommentsVideo: StateFlow<Video?> = _activeCommentsVideo.asStateFlow()

    private val _activeShareVideo = MutableStateFlow<Video?>(null)
    val activeShareVideo: StateFlow<Video?> = _activeShareVideo.asStateFlow()

    private val _activeSoundVideo = MutableStateFlow<Video?>(null)
    val activeSoundVideo: StateFlow<Video?> = _activeSoundVideo.asStateFlow()

    private val _viewingStoryIndex = MutableStateFlow<Int?>(null)
    val viewingStoryIndex: StateFlow<Int?> = _viewingStoryIndex.asStateFlow()

    private val _commentsForActiveVideo = MutableStateFlow<List<Comment>>(emptyList())
    val commentsForActiveVideo: StateFlow<List<Comment>> = _commentsForActiveVideo.asStateFlow()

    fun selectTab(tab: FeedTab) {
        _selectedTab.value = tab
    }

    fun onLikeVideo(video: Video) {
        viewModelScope.launch {
            videoRepository.toggleLike(video.id, video.isLiked)
        }
    }

    fun onSaveVideo(video: Video) {
        viewModelScope.launch {
            videoRepository.toggleSave(video.id, video.isSaved)
        }
    }

    fun onFollowCreator(creatorId: String, isFollowing: Boolean) {
        viewModelScope.launch {
            userRepository.toggleFollow(creatorId, isFollowing)
        }
    }

    fun openComments(video: Video) {
        _activeCommentsVideo.value = video
        viewModelScope.launch {
            interactionRepository.getComments(video.id).collect {
                _commentsForActiveVideo.value = it
            }
        }
    }

    fun closeComments() {
        _activeCommentsVideo.value = null
    }

    fun addComment(videoId: String, text: String) {
        viewModelScope.launch {
            interactionRepository.addComment(videoId, text)
        }
    }

    fun toggleCommentLike(comment: Comment) {
        viewModelScope.launch {
            interactionRepository.toggleCommentLike(comment.id, comment.isLiked)
        }
    }

    fun openShare(video: Video) {
        _activeShareVideo.value = video
    }

    fun closeShare() {
        _activeShareVideo.value = null
    }

    fun openSound(video: Video) {
        _activeSoundVideo.value = video
    }

    fun closeSound() {
        _activeSoundVideo.value = null
    }

    fun openStoryViewer(index: Int) {
        _viewingStoryIndex.value = index
    }

    fun closeStoryViewer() {
        _viewingStoryIndex.value = null
    }

    fun addStory(contentUrl: String = "", caption: String = "My Vibe") {
        val user = userRepository.getCurrentUser()
        val newStory = StoryItem(
            id = "story_" + System.currentTimeMillis(),
            user = user,
            imageResId = com.example.R.drawable.ava_thumb_1,
            caption = caption,
            timestamp = System.currentTimeMillis(),
            isViewed = false
        )
        _stories.value = listOf(newStory) + _stories.value
    }

    fun hideVideo(videoId: String) {
        viewModelScope.launch {
            // Can be filtered or deleted from feed
            videoRepository.deleteVideo(videoId)
        }
    }

    fun downloadVideo(video: Video, quality: String) {
        viewModelScope.launch {
            videoRepository.saveDownload(video, quality)
        }
    }

    fun reportVideo(videoId: String, reason: String, details: String) {
        viewModelScope.launch {
            userRepository.reportContent(videoId, "video", reason, details)
        }
    }

    fun blockCreator(creatorId: String, username: String, displayName: String) {
        viewModelScope.launch {
            userRepository.blockUser(creatorId, username, displayName)
        }
    }

    fun muteCreator(creatorId: String, username: String, displayName: String) {
        viewModelScope.launch {
            userRepository.muteUser(creatorId, username, displayName)
        }
    }
}
