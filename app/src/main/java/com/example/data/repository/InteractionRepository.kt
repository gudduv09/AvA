package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entity.CommentEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.mock.DemoDataProvider
import com.example.domain.model.Comment
import com.example.domain.model.NotificationItem
import com.example.domain.model.NotificationType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class InteractionRepository(private val database: AppDatabase) {

    private val commentDao = database.commentDao()
    private val notificationDao = database.notificationDao()
    private val userDao = database.userDao()
    private val videoDao = database.videoDao()

    fun getComments(videoId: String): Flow<List<Comment>> {
        return combine(commentDao.getCommentsForVideo(videoId), userDao.getAllUsers()) { commentEntities, userEntities ->
            val userMap = userEntities.associateBy { it.id }
            commentEntities.map { entity ->
                val user = userMap[entity.userId]?.toDomain() ?: DemoDataProvider.currentUser
                Comment(
                    id = entity.id,
                    videoId = entity.videoId,
                    user = user,
                    text = entity.text,
                    likesCount = entity.likesCount,
                    isLiked = entity.isLiked,
                    isPinned = entity.isPinned,
                    createdAt = entity.createdAt
                )
            }
        }
    }

    suspend fun addComment(videoId: String, text: String): Comment {
        val newComment = CommentEntity(
            id = "c_" + System.currentTimeMillis(),
            videoId = videoId,
            userId = DemoDataProvider.currentUser.id,
            text = text,
            likesCount = 0,
            isLiked = false,
            isPinned = false,
            createdAt = System.currentTimeMillis()
        )
        commentDao.insertComment(newComment)
        videoDao.incrementCommentCount(videoId)
        return Comment(
            id = newComment.id,
            videoId = videoId,
            user = DemoDataProvider.currentUser,
            text = text,
            likesCount = 0,
            isLiked = false,
            isPinned = false,
            createdAt = newComment.createdAt
        )
    }

    suspend fun toggleCommentLike(commentId: String, currentIsLiked: Boolean) {
        val newLiked = !currentIsLiked
        val delta = if (newLiked) 1 else -1
        commentDao.updateCommentLike(commentId, newLiked, delta)
    }

    suspend fun deleteComment(commentId: String) {
        commentDao.deleteComment(commentId)
    }

    fun getNotifications(): Flow<List<NotificationItem>> {
        return combine(notificationDao.getAllNotifications(), userDao.getAllUsers()) { notifs, users ->
            val userMap = users.associateBy { it.id }
            notifs.map { entity ->
                val user = userMap[entity.userId]?.toDomain() ?: DemoDataProvider.currentUser
                val type = try {
                    NotificationType.valueOf(entity.type)
                } catch (_: Exception) {
                    NotificationType.SYSTEM
                }
                NotificationItem(
                    id = entity.id,
                    user = user,
                    type = type,
                    content = entity.content,
                    targetVideoThumbnailRes = entity.targetVideoThumbnailRes,
                    timestamp = entity.timestamp,
                    isRead = entity.isRead
                )
            }
        }
    }

    suspend fun markAllNotificationsAsRead() {
        notificationDao.markAllAsRead()
    }
}
