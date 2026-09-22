package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.entity.CommentEntity
import com.example.data.local.entity.SavedVideoEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.VideoEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RoomDatabaseTest {

    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testUserDaoPersistence() = runBlocking {
        val userDao = db.userDao()
        val user = UserEntity(
            id = "user_test_1",
            username = "ava_creator",
            displayName = "AVA Creator",
            avatarDrawableRes = 1,
            bio = "Official test creator",
            website = "https://ava.mbs.com",
            followersCount = 1200,
            followingCount = 45,
            likesCount = 9800,
            isVerified = true,
            isFollowing = false,
            isPrivate = false
        )
        userDao.insertUser(user)

        val retrieved = userDao.getUserByIdOnce("user_test_1")
        assertNotNull(retrieved)
        assertEquals("ava_creator", retrieved?.username)
        assertEquals(true, retrieved?.isVerified)

        userDao.updateFollowState("user_test_1", isFollowing = true, delta = 1)
        val updated = userDao.getUserByIdOnce("user_test_1")
        assertEquals(1201, updated?.followersCount)
        assertEquals(true, updated?.isFollowing)
    }

    @Test
    fun testVideoDaoPersistence() = runBlocking {
        val videoDao = db.videoDao()
        val video = VideoEntity(
            id = "vid_test_1",
            creatorId = "user_test_1",
            videoUrl = "https://example.com/test.mp4",
            thumbnailResId = 2,
            caption = "Amazing cyberpunk vibe #cyber #ava",
            hashtagsJson = "[\"#cyber\", \"#ava\"]",
            soundTitle = "Cyber Vibe 2026",
            soundArtist = "AVA Originals",
            likesCount = 10,
            commentsCount = 2,
            sharesCount = 5,
            savesCount = 3,
            isLiked = false,
            isSaved = false,
            durationSeconds = 15,
            visibility = "PUBLIC",
            allowComments = true,
            allowDuet = true,
            allowDownload = true,
            createdAt = System.currentTimeMillis()
        )
        videoDao.insertVideo(video)

        val allVideos = videoDao.getAllVideos().first()
        assertEquals(1, allVideos.size)
        assertEquals("vid_test_1", allVideos[0].id)

        videoDao.updateLikeState("vid_test_1", isLiked = true, delta = 1)
        val likedVideos = videoDao.getLikedVideos().first()
        assertEquals(1, likedVideos.size)
        assertEquals(11, likedVideos[0].likesCount)
    }

    @Test
    fun testCommentDaoPersistence() = runBlocking {
        val commentDao = db.commentDao()
        val comment = CommentEntity(
            id = "comment_test_1",
            videoId = "vid_test_1",
            userId = "user_test_1",
            text = "Incredible editing! 🔥",
            likesCount = 4,
            isLiked = false,
            isPinned = true,
            createdAt = System.currentTimeMillis()
        )
        commentDao.insertComment(comment)

        val comments = commentDao.getCommentsForVideo("vid_test_1").first()
        assertEquals(1, comments.size)
        assertEquals("Incredible editing! 🔥", comments[0].text)
        assertTrue(comments[0].isPinned)
    }

    @Test
    fun testSavedVideoDaoPersistence() = runBlocking {
        val savedVideoDao = db.savedVideoDao()
        val videoDao = db.videoDao()

        // Insert a video first
        val video = VideoEntity(
            id = "vid_save_test",
            creatorId = "user_test_1",
            videoUrl = "https://example.com/save.mp4",
            thumbnailResId = 2,
            caption = "Saved content test",
            hashtagsJson = "[]",
            soundTitle = "Audio",
            soundArtist = "Artist",
            likesCount = 100,
            commentsCount = 10,
            sharesCount = 5,
            savesCount = 50,
            isLiked = false,
            isSaved = true,
            durationSeconds = 30,
            visibility = "PUBLIC",
            allowComments = true,
            allowDuet = true,
            allowDownload = true,
            createdAt = System.currentTimeMillis()
        )
        videoDao.insertVideo(video)

        val savedEntry = SavedVideoEntity(
            userId = "user_test_1",
            videoId = "vid_save_test"
        )
        savedVideoDao.insertSavedVideo(savedEntry)

        val isSaved = savedVideoDao.isVideoSavedOnce("user_test_1", "vid_save_test")
        assertTrue(isSaved)

        val savedList = savedVideoDao.getSavedVideosForUser("user_test_1").first()
        assertEquals(1, savedList.size)
        assertEquals("vid_save_test", savedList[0].videoId)

        val savedVideosWithDetails = savedVideoDao.getSavedVideosWithDetails("user_test_1").first()
        assertEquals(1, savedVideosWithDetails.size)
        assertEquals("vid_save_test", savedVideosWithDetails[0].id)

        // Delete saved video
        savedVideoDao.deleteSavedVideo("user_test_1", "vid_save_test")
        val isStillSaved = savedVideoDao.isVideoSavedOnce("user_test_1", "vid_save_test")
        assertEquals(false, isStillSaved)
    }
}
