package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val displayName: String,
    val avatarDrawableRes: Int,
    val bio: String,
    val website: String,
    val followersCount: Int,
    val followingCount: Int,
    val likesCount: Int,
    val isVerified: Boolean,
    val isFollowing: Boolean,
    val isPrivate: Boolean
)

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey val id: String,
    val creatorId: String,
    val videoUrl: String,
    val thumbnailResId: Int,
    val caption: String,
    val hashtagsJson: String,
    val soundTitle: String,
    val soundArtist: String,
    val likesCount: Int,
    val commentsCount: Int,
    val sharesCount: Int,
    val savesCount: Int,
    val isLiked: Boolean,
    val isSaved: Boolean,
    val durationSeconds: Int,
    val visibility: String,
    val allowComments: Boolean,
    val allowDuet: Boolean,
    val allowDownload: Boolean,
    val createdAt: Long
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val id: String,
    val videoId: String,
    val userId: String,
    val text: String,
    val likesCount: Int,
    val isLiked: Boolean,
    val isPinned: Boolean,
    val createdAt: Long,
    val parentCommentId: String? = null
)

@Entity(
    tableName = "saved_videos",
    primaryKeys = ["userId", "videoId"]
)
data class SavedVideoEntity(
    val userId: String,
    val videoId: String,
    val savedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "blocked_users")
data class BlockedUserEntity(
    @PrimaryKey val userId: String,
    val username: String,
    val displayName: String,
    val isMuted: Boolean = false,
    val blockedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey val id: String,
    val targetId: String,
    val targetType: String, // "video", "user", "comment"
    val reason: String,
    val details: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey val videoId: String,
    val caption: String,
    val thumbnailResId: Int,
    val durationSeconds: Int,
    val quality: String,
    val downloadedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val type: String,
    val content: String,
    val targetVideoThumbnailRes: Int,
    val timestamp: Long,
    val isRead: Boolean
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val conversationId: String,
    val senderId: String,
    val recipientId: String,
    val text: String,
    val timestamp: Long,
    val isFromCurrentUser: Boolean,
    val isRead: Boolean
)

@Entity(tableName = "drafts")
data class DraftEntity(
    @PrimaryKey val id: String,
    val title: String,
    val caption: String,
    val localUri: String,
    val thumbnailResId: Int,
    val durationSeconds: Int,
    val filterName: String,
    val updatedAt: Long
)
