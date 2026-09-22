package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entity.VideoEntity
import com.example.data.mock.DemoDataProvider
import com.example.domain.model.User
import com.example.domain.model.Video
import com.example.domain.model.VideoVisibility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONArray

class VideoRepository(private val database: AppDatabase) {

    private val videoDao = database.videoDao()
    private val userDao = database.userDao()
    private val moderationDao = database.moderationDao()
    private val downloadDao = database.downloadDao()
    private val savedVideoDao = database.savedVideoDao()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfNeeded()
        }
    }

    private suspend fun seedInitialDataIfNeeded() {
        val existingVideos = videoDao.getAllVideos().first()
        if (existingVideos.isEmpty()) {
            userDao.insertUsers(DemoDataProvider.userEntities())
            videoDao.insertVideos(DemoDataProvider.initialVideos)
            database.commentDao().insertComments(DemoDataProvider.initialComments)
            database.notificationDao().insertNotifications(DemoDataProvider.initialNotifications)
            database.messageDao().insertMessages(DemoDataProvider.initialMessages)
            database.draftDao().insertDraft(DemoDataProvider.initialDrafts.first())
        }
    }

    fun getAllVideos(): Flow<List<Video>> {
        return combine(
            videoDao.getAllVideos(),
            userDao.getAllUsers(),
            moderationDao.getAllBlockedUsers()
        ) { videoEntities, userEntities, blockedEntities ->
            val blockedIds = blockedEntities.map { it.userId }.toSet()
            val userMap = userEntities.associateBy { it.id }
            videoEntities
                .filter { it.creatorId !in blockedIds }
                .map { entity ->
                    val userEntity = userMap[entity.creatorId] ?: DemoDataProvider.currentUser.let {
                        com.example.data.local.entity.UserEntity(
                            it.id, it.username, it.displayName, it.avatarDrawableRes,
                            it.bio, it.website, it.followersCount, it.followingCount,
                            it.likesCount, it.isVerified, it.isFollowing, it.isPrivate
                        )
                    }
                    entity.toDomain(userEntity.toDomain())
                }
        }
    }

    fun getDownloads(): Flow<List<com.example.data.local.entity.DownloadEntity>> {
        return downloadDao.getAllDownloads()
    }

    suspend fun saveDownload(video: Video, quality: String = "1080p HD") {
        downloadDao.insertDownload(
            com.example.data.local.entity.DownloadEntity(
                videoId = video.id,
                caption = video.caption,
                thumbnailResId = video.thumbnailResId,
                durationSeconds = video.durationSeconds,
                quality = quality
            )
        )
    }

    suspend fun removeDownload(videoId: String) {
        downloadDao.deleteDownload(videoId)
    }

    suspend fun deleteVideo(videoId: String) {
        videoDao.deleteVideo(videoId)
    }

    fun getLikedVideos(): Flow<List<Video>> {
        return combine(videoDao.getLikedVideos(), userDao.getAllUsers()) { videoEntities, userEntities ->
            val userMap = userEntities.associateBy { it.id }
            videoEntities.map { entity ->
                val userEntity = userMap[entity.creatorId]
                entity.toDomain(userEntity?.toDomain() ?: DemoDataProvider.currentUser)
            }
        }
    }

    fun getSavedVideos(): Flow<List<Video>> {
        return combine(videoDao.getSavedVideos(), userDao.getAllUsers()) { videoEntities, userEntities ->
            val userMap = userEntities.associateBy { it.id }
            videoEntities.map { entity ->
                val userEntity = userMap[entity.creatorId]
                entity.toDomain(userEntity?.toDomain() ?: DemoDataProvider.currentUser)
            }
        }
    }

    suspend fun toggleLike(videoId: String, currentIsLiked: Boolean) {
        val newLiked = !currentIsLiked
        val delta = if (newLiked) 1 else -1
        videoDao.updateLikeState(videoId, newLiked, delta)
    }

    suspend fun toggleSave(videoId: String, currentIsSaved: Boolean) {
        val newSaved = !currentIsSaved
        val delta = if (newSaved) 1 else -1
        videoDao.updateSaveState(videoId, newSaved, delta)
        val currentUserId = DemoDataProvider.currentUser.id
        if (newSaved) {
            savedVideoDao.insertSavedVideo(
                com.example.data.local.entity.SavedVideoEntity(
                    userId = currentUserId,
                    videoId = videoId
                )
            )
        } else {
            savedVideoDao.deleteSavedVideo(
                userId = currentUserId,
                videoId = videoId
            )
        }
    }

    suspend fun incrementShare(videoId: String) {
        videoDao.incrementShareCount(videoId)
    }

    suspend fun publishVideo(
        caption: String,
        hashtags: List<String>,
        visibility: VideoVisibility,
        allowComments: Boolean,
        allowDuet: Boolean,
        allowDownload: Boolean,
        thumbnailResId: Int,
        videoUrl: String = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
    ): Video {
        val jsonArray = JSONArray(hashtags).toString()
        val newId = "vid_" + System.currentTimeMillis()
        val newEntity = VideoEntity(
            id = newId,
            creatorId = DemoDataProvider.currentUser.id,
            videoUrl = videoUrl,
            thumbnailResId = thumbnailResId,
            caption = caption,
            hashtagsJson = jsonArray,
            soundTitle = "Original Sound - ${DemoDataProvider.currentUser.username}",
            soundArtist = DemoDataProvider.currentUser.displayName,
            likesCount = 0,
            commentsCount = 0,
            sharesCount = 0,
            savesCount = 0,
            isLiked = false,
            isSaved = false,
            durationSeconds = 15,
            visibility = visibility.name,
            allowComments = allowComments,
            allowDuet = allowDuet,
            allowDownload = allowDownload,
            createdAt = System.currentTimeMillis()
        )
        videoDao.insertVideo(newEntity)
        return newEntity.toDomain(DemoDataProvider.currentUser)
    }
}

fun VideoEntity.toDomain(creator: User): Video {
    val tags = try {
        val arr = JSONArray(hashtagsJson)
        List(arr.length()) { arr.getString(it) }
    } catch (_: Exception) {
        emptyList()
    }
    return Video(
        id = id,
        creatorId = creatorId,
        creator = creator,
        videoUrl = videoUrl,
        thumbnailResId = thumbnailResId,
        caption = caption,
        hashtags = tags,
        soundTitle = soundTitle,
        soundArtist = soundArtist,
        likesCount = likesCount,
        commentsCount = commentsCount,
        sharesCount = sharesCount,
        savesCount = savesCount,
        isLiked = isLiked,
        isSaved = isSaved,
        durationSeconds = durationSeconds,
        visibility = try { VideoVisibility.valueOf(visibility) } catch (_: Exception) { VideoVisibility.PUBLIC },
        allowComments = allowComments,
        allowDuet = allowDuet,
        allowDownload = allowDownload,
        createdAt = createdAt
    )
}

fun com.example.data.local.entity.UserEntity.toDomain(): User {
    return User(
        id = id,
        username = username,
        displayName = displayName,
        avatarDrawableRes = avatarDrawableRes,
        bio = bio,
        website = website,
        followersCount = followersCount,
        followingCount = followingCount,
        likesCount = likesCount,
        isVerified = isVerified,
        isFollowing = isFollowing,
        isPrivate = isPrivate
    )
}
