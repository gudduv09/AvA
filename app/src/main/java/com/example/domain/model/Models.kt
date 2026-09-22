package com.example.domain.model

data class User(
    val id: String,
    val username: String,
    val displayName: String,
    val avatarUrl: String = "",
    val avatarDrawableRes: Int = 0,
    val bio: String = "",
    val website: String = "",
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val likesCount: Int = 0,
    val isVerified: Boolean = false,
    val isFollowing: Boolean = false,
    val isPrivate: Boolean = false
)

data class Video(
    val id: String,
    val creatorId: String,
    val creator: User,
    val videoUrl: String,
    val thumbnailResId: Int = 0,
    val caption: String,
    val hashtags: List<String> = emptyList(),
    val soundTitle: String = "Original Sound",
    val soundArtist: String = "AVA Original",
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val sharesCount: Int = 0,
    val savesCount: Int = 0,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val durationSeconds: Int = 15,
    val visibility: VideoVisibility = VideoVisibility.PUBLIC,
    val allowComments: Boolean = true,
    val allowDuet: Boolean = true,
    val allowDownload: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

enum class VideoVisibility {
    PUBLIC,
    FOLLOWERS,
    PRIVATE
}

data class Comment(
    val id: String,
    val videoId: String,
    val user: User,
    val text: String,
    val likesCount: Int = 0,
    val isLiked: Boolean = false,
    val isPinned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class DirectMessage(
    val id: String,
    val senderId: String,
    val recipientId: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFromCurrentUser: Boolean = false,
    val isRead: Boolean = true
)

data class Conversation(
    val id: String,
    val user: User,
    val lastMessage: String,
    val lastTimestamp: Long,
    val unreadCount: Int = 0
)

data class NotificationItem(
    val id: String,
    val user: User,
    val type: NotificationType,
    val content: String,
    val targetVideoThumbnailRes: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

enum class NotificationType {
    LIKE,
    COMMENT,
    FOLLOW,
    MENTION,
    SYSTEM
}

data class StoryItem(
    val id: String,
    val user: User,
    val imageResId: Int,
    val caption: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isViewed: Boolean = false
)

data class SoundTrack(
    val id: String,
    val title: String,
    val artist: String,
    val usageCount: Int,
    val duration: String,
    val isSaved: Boolean = false
)

data class Draft(
    val id: String,
    val title: String,
    val caption: String,
    val localUri: String,
    val thumbnailResId: Int = 0,
    val durationSeconds: Int = 15,
    val filterName: String = "Normal",
    val updatedAt: Long = System.currentTimeMillis()
)
