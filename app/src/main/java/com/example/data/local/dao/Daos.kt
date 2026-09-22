package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.CommentEntity
import com.example.data.local.entity.DraftEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.SavedVideoEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.VideoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserById(id: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserByIdOnce(id: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET isFollowing = :isFollowing, followersCount = followersCount + :delta WHERE id = :userId")
    suspend fun updateFollowState(userId: String, isFollowing: Boolean, delta: Int)
}

@Dao
interface VideoDao {
    @Query("SELECT * FROM videos ORDER BY createdAt DESC")
    fun getAllVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE creatorId = :creatorId ORDER BY createdAt DESC")
    fun getVideosByCreator(creatorId: String): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE isLiked = 1 ORDER BY createdAt DESC")
    fun getLikedVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE isSaved = 1 ORDER BY createdAt DESC")
    fun getSavedVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE id = :videoId")
    fun getVideoById(videoId: String): Flow<VideoEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideos(videos: List<VideoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: VideoEntity)

    @Query("UPDATE videos SET isLiked = :isLiked, likesCount = likesCount + :delta WHERE id = :videoId")
    suspend fun updateLikeState(videoId: String, isLiked: Boolean, delta: Int)

    @Query("UPDATE videos SET isSaved = :isSaved, savesCount = savesCount + :delta WHERE id = :videoId")
    suspend fun updateSaveState(videoId: String, isSaved: Boolean, delta: Int)

    @Query("UPDATE videos SET sharesCount = sharesCount + 1 WHERE id = :videoId")
    suspend fun incrementShareCount(videoId: String)

    @Query("UPDATE videos SET commentsCount = commentsCount + 1 WHERE id = :videoId")
    suspend fun incrementCommentCount(videoId: String)

    @Query("DELETE FROM videos WHERE id = :videoId")
    suspend fun deleteVideo(videoId: String)
}

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE videoId = :videoId ORDER BY isPinned DESC, createdAt DESC")
    fun getCommentsForVideo(videoId: String): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<CommentEntity>)

    @Query("UPDATE comments SET isLiked = :isLiked, likesCount = likesCount + :delta WHERE id = :commentId")
    suspend fun updateCommentLike(commentId: String, isLiked: Boolean, delta: Int)

    @Query("DELETE FROM comments WHERE id = :commentId")
    suspend fun deleteComment(commentId: String)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesForConversation(conversationId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)
}

@Dao
interface DraftDao {
    @Query("SELECT * FROM drafts ORDER BY updatedAt DESC")
    fun getAllDrafts(): Flow<List<DraftEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDraft(draft: DraftEntity)

    @Query("DELETE FROM drafts WHERE id = :draftId")
    suspend fun deleteDraft(draftId: String)
}

@Dao
interface ModerationDao {
    @Query("SELECT * FROM blocked_users")
    fun getAllBlockedUsers(): Flow<List<com.example.data.local.entity.BlockedUserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlockedUser(user: com.example.data.local.entity.BlockedUserEntity)

    @Query("DELETE FROM blocked_users WHERE userId = :userId")
    suspend fun unblockUser(userId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM blocked_users WHERE userId = :userId AND isMuted = 0)")
    suspend fun isUserBlocked(userId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: com.example.data.local.entity.ReportEntity)
}

@Dao
interface DownloadDao {
    @Query("SELECT * FROM downloads ORDER BY downloadedAt DESC")
    fun getAllDownloads(): Flow<List<com.example.data.local.entity.DownloadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(download: com.example.data.local.entity.DownloadEntity)

    @Query("DELETE FROM downloads WHERE videoId = :videoId")
    suspend fun deleteDownload(videoId: String)
}

@Dao
interface SavedVideoDao {
    @Query("SELECT * FROM saved_videos WHERE userId = :userId ORDER BY savedAt DESC")
    fun getSavedVideosForUser(userId: String): Flow<List<SavedVideoEntity>>

    @Query("SELECT v.* FROM videos v INNER JOIN saved_videos s ON v.id = s.videoId WHERE s.userId = :userId ORDER BY s.savedAt DESC")
    fun getSavedVideosWithDetails(userId: String): Flow<List<VideoEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM saved_videos WHERE userId = :userId AND videoId = :videoId)")
    fun isVideoSaved(userId: String, videoId: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM saved_videos WHERE userId = :userId AND videoId = :videoId)")
    suspend fun isVideoSavedOnce(userId: String, videoId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedVideo(savedVideo: SavedVideoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedVideos(savedVideos: List<SavedVideoEntity>)

    @Query("DELETE FROM saved_videos WHERE userId = :userId AND videoId = :videoId")
    suspend fun deleteSavedVideo(userId: String, videoId: String)

    @Query("DELETE FROM saved_videos WHERE videoId = :videoId")
    suspend fun deleteSavedVideoByVideoId(videoId: String)

    @Query("DELETE FROM saved_videos WHERE userId = :userId")
    suspend fun clearSavedVideosForUser(userId: String)
}

