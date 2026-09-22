package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entity.DraftEntity
import com.example.data.mock.DemoDataProvider
import com.example.domain.model.Draft
import com.example.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepository(private val database: AppDatabase) {

    private val userDao = database.userDao()
    private val draftDao = database.draftDao()
    private val moderationDao = database.moderationDao()

    fun getCurrentUser(): User {
        return DemoDataProvider.currentUser
    }

    fun getAllUsers(): Flow<List<User>> {
        return userDao.getAllUsers().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getBlockedUsers(): Flow<List<com.example.data.local.entity.BlockedUserEntity>> {
        return moderationDao.getAllBlockedUsers()
    }

    suspend fun blockUser(userId: String, username: String, displayName: String) {
        moderationDao.insertBlockedUser(
            com.example.data.local.entity.BlockedUserEntity(
                userId = userId,
                username = username,
                displayName = displayName,
                isMuted = false
            )
        )
    }

    suspend fun muteUser(userId: String, username: String, displayName: String) {
        moderationDao.insertBlockedUser(
            com.example.data.local.entity.BlockedUserEntity(
                userId = userId,
                username = username,
                displayName = displayName,
                isMuted = true
            )
        )
    }

    suspend fun unblockUser(userId: String) {
        moderationDao.unblockUser(userId)
    }

    suspend fun reportContent(targetId: String, targetType: String, reason: String, details: String = "") {
        moderationDao.insertReport(
            com.example.data.local.entity.ReportEntity(
                id = "rep_" + System.currentTimeMillis(),
                targetId = targetId,
                targetType = targetType,
                reason = reason,
                details = details
            )
        )
    }

    suspend fun toggleFollow(userId: String, isCurrentlyFollowing: Boolean) {
        val newFollowing = !isCurrentlyFollowing
        val delta = if (newFollowing) 1 else -1
        userDao.updateFollowState(userId, newFollowing, delta)
    }

    fun getAllDrafts(): Flow<List<Draft>> {
        return draftDao.getAllDrafts().map { entities ->
            entities.map { entity ->
                Draft(
                    id = entity.id,
                    title = entity.title,
                    caption = entity.caption,
                    localUri = entity.localUri,
                    thumbnailResId = entity.thumbnailResId,
                    durationSeconds = entity.durationSeconds,
                    filterName = entity.filterName,
                    updatedAt = entity.updatedAt
                )
            }
        }
    }

    suspend fun saveDraft(title: String, caption: String, thumbnailResId: Int, filter: String) {
        val draft = DraftEntity(
            id = "draft_" + System.currentTimeMillis(),
            title = title,
            caption = caption,
            localUri = "",
            thumbnailResId = thumbnailResId,
            durationSeconds = 15,
            filterName = filter,
            updatedAt = System.currentTimeMillis()
        )
        draftDao.insertDraft(draft)
    }

    suspend fun deleteDraft(draftId: String) {
        draftDao.deleteDraft(draftId)
    }
}
