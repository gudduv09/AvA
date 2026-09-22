package com.example.data.mock

import com.example.R
import com.example.data.local.entity.CommentEntity
import com.example.data.local.entity.DraftEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.VideoEntity
import com.example.domain.model.SoundTrack
import com.example.domain.model.StoryItem
import com.example.domain.model.User

object DemoDataProvider {

    val currentUser = User(
        id = "user_me",
        username = "ava_creator",
        displayName = "AVA Official",
        avatarDrawableRes = R.drawable.ava_icon_foreground,
        bio = "Creating the future of short-form video on AVA ✨ MBS Group",
        website = "https://mbsgroup.example.com",
        followersCount = 14200,
        followingCount = 186,
        likesCount = 89400,
        isVerified = true,
        isFollowing = false
    )

    val creators = listOf(
        currentUser,
        User(
            id = "user_pinkwave",
            username = "pinkwave",
            displayName = "Pink Wave 🌊",
            avatarDrawableRes = R.drawable.ava_thumb_2,
            bio = "Choreographer • Street dance • Neon aesthetics • Tokyo/LA",
            website = "https://pinkwave.dance",
            followersCount = 85200,
            followingCount = 210,
            likesCount = 421000,
            isVerified = true,
            isFollowing = true
        ),
        User(
            id = "user_urbanframe",
            username = "urbanframe",
            displayName = "Urban Frame",
            avatarDrawableRes = R.drawable.ava_thumb_1,
            bio = "Cyberpunk streets & neon nightscapes 📸 Shot on 4K",
            website = "https://urbanframe.visuals",
            followersCount = 43700,
            followingCount = 142,
            likesCount = 215000,
            isVerified = true,
            isFollowing = false
        ),
        User(
            id = "user_dailyvibe",
            username = "dailyvibe",
            displayName = "Daily Vibe ☕",
            avatarDrawableRes = R.drawable.ava_thumb_3,
            bio = "Slow living • Coffee culture • Ambient beats",
            website = "",
            followersCount = 61900,
            followingCount = 330,
            likesCount = 398000,
            isVerified = false,
            isFollowing = false
        ),
        User(
            id = "user_summit",
            username = "summitview",
            displayName = "Summit Horizon",
            avatarDrawableRes = R.drawable.ava_thumb_4,
            bio = "Chasing dusk above 4000 meters ⛰️ Drone films",
            website = "https://summithorizon.film",
            followersCount = 29400,
            followingCount = 95,
            likesCount = 178000,
            isVerified = true,
            isFollowing = false
        )
    )

    val initialVideos = listOf(
        VideoEntity(
            id = "vid_1",
            creatorId = "user_urbanframe",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            thumbnailResId = R.drawable.ava_thumb_1,
            caption = "Midnight reflections in Neo Shibuya. The city never sleeps when the neon glows pink 🌆 #AVA #cyberpunk #tokyo #vibe",
            hashtagsJson = "[\"#AVA\", \"#cyberpunk\", \"#tokyo\", \"#vibe\"]",
            soundTitle = "Midnight Pulse - Synthwave Mix",
            soundArtist = "AVA Originals",
            likesCount = 24800,
            commentsCount = 342,
            sharesCount = 1290,
            savesCount = 5120,
            isLiked = false,
            isSaved = false,
            durationSeconds = 15,
            visibility = "PUBLIC",
            allowComments = true,
            allowDuet = true,
            allowDownload = true,
            createdAt = System.currentTimeMillis() - 3600000
        ),
        VideoEntity(
            id = "vid_2",
            creatorId = "user_pinkwave",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
            thumbnailResId = R.drawable.ava_thumb_2,
            caption = "Testing the new choreography at twilight rooftop 🔥 Drop a ⚡ if you felt the rhythm! #dance #pinkwave #flow #freestyle",
            hashtagsJson = "[\"#dance\", \"#pinkwave\", \"#flow\", \"#freestyle\"]",
            soundTitle = "Electric Velocity (Pink Edit)",
            soundArtist = "SoundLab AVA",
            likesCount = 58200,
            commentsCount = 891,
            sharesCount = 3420,
            savesCount = 11400,
            isLiked = true,
            isSaved = true,
            durationSeconds = 24,
            visibility = "PUBLIC",
            allowComments = true,
            allowDuet = true,
            allowDownload = true,
            createdAt = System.currentTimeMillis() - 7200000
        ),
        VideoEntity(
            id = "vid_3",
            creatorId = "user_dailyvibe",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
            thumbnailResId = R.drawable.ava_thumb_3,
            caption = "Morning pour over ritual with oat milk microfoam ☕ Starting Monday right with good energy. What's your go-to roast? #coffeelover #aesthetic #dailyvibe #minimal",
            hashtagsJson = "[\"#coffeelover\", \"#aesthetic\", \"#dailyvibe\", \"#minimal\"]",
            soundTitle = "Lo-Fi Sunrise in Kyoto",
            soundArtist = "Chill Vibe Collective",
            likesCount = 19400,
            commentsCount = 215,
            sharesCount = 812,
            savesCount = 3890,
            isLiked = false,
            isSaved = false,
            durationSeconds = 18,
            visibility = "PUBLIC",
            allowComments = true,
            allowDuet = true,
            allowDownload = true,
            createdAt = System.currentTimeMillis() - 14400000
        ),
        VideoEntity(
            id = "vid_4",
            creatorId = "user_summit",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyBlazes.mp4",
            thumbnailResId = R.drawable.ava_thumb_4,
            caption = "Sunset at 3,800m altitude. The clouds turned pure magenta for exactly 90 seconds 🏔️ Nature is unreal. #mountains #drone #cinematic #sunset #peace",
            hashtagsJson = "[\"#mountains\", \"#drone\", \"#cinematic\", \"#sunset\", \"#peace\"]",
            soundTitle = "Horizon Echoes - Ambient Drone",
            soundArtist = "Apex Audio",
            likesCount = 32700,
            commentsCount = 478,
            sharesCount = 1940,
            savesCount = 7800,
            isLiked = false,
            isSaved = false,
            durationSeconds = 30,
            visibility = "PUBLIC",
            allowComments = true,
            allowDuet = true,
            allowDownload = true,
            createdAt = System.currentTimeMillis() - 28800000
        )
    )

    val initialComments = listOf(
        CommentEntity(
            id = "c_1",
            videoId = "vid_1",
            userId = "user_pinkwave",
            text = "The color grading on that neon is top tier 🔥",
            likesCount = 324,
            isLiked = true,
            isPinned = true,
            createdAt = System.currentTimeMillis() - 3200000
        ),
        CommentEntity(
            id = "c_2",
            videoId = "vid_1",
            userId = "user_dailyvibe",
            text = "Need this soundtrack on loop immediately! 🎧",
            likesCount = 89,
            isLiked = false,
            isPinned = false,
            createdAt = System.currentTimeMillis() - 2900000
        ),
        CommentEntity(
            id = "c_3",
            videoId = "vid_2",
            userId = "user_urbanframe",
            text = "That freeze at 0:14 was insane precision 👏",
            likesCount = 142,
            isLiked = true,
            isPinned = true,
            createdAt = System.currentTimeMillis() - 6500000
        ),
        CommentEntity(
            id = "c_4",
            videoId = "vid_3",
            userId = "user_me",
            text = "The pour technique is satisfying to watch ✨",
            likesCount = 56,
            isLiked = false,
            isPinned = false,
            createdAt = System.currentTimeMillis() - 12000000
        )
    )

    val initialNotifications = listOf(
        NotificationEntity(
            id = "notif_1",
            userId = "user_pinkwave",
            type = "LIKE",
            content = "liked your video",
            targetVideoThumbnailRes = R.drawable.ava_thumb_2,
            timestamp = System.currentTimeMillis() - 1800000,
            isRead = false
        ),
        NotificationEntity(
            id = "notif_2",
            userId = "user_urbanframe",
            type = "COMMENT",
            content = "commented: \"Amazing energy! Keep it up\"",
            targetVideoThumbnailRes = R.drawable.ava_thumb_1,
            timestamp = System.currentTimeMillis() - 5400000,
            isRead = false
        ),
        NotificationEntity(
            id = "notif_3",
            userId = "user_dailyvibe",
            type = "FOLLOW",
            content = "started following you",
            targetVideoThumbnailRes = 0,
            timestamp = System.currentTimeMillis() - 10800000,
            isRead = true
        ),
        NotificationEntity(
            id = "notif_4",
            userId = "user_me",
            type = "SYSTEM",
            content = "Welcome to AVA! Start discovering trending videos.",
            targetVideoThumbnailRes = R.drawable.ava_icon_foreground,
            timestamp = System.currentTimeMillis() - 86400000,
            isRead = true
        )
    )

    val initialMessages = listOf(
        MessageEntity(
            id = "msg_1",
            conversationId = "conv_pinkwave",
            senderId = "user_pinkwave",
            recipientId = "user_me",
            text = "Hey! Loved your new video format on AVA 🔥",
            timestamp = System.currentTimeMillis() - 7200000,
            isFromCurrentUser = false,
            isRead = true
        ),
        MessageEntity(
            id = "msg_2",
            conversationId = "conv_pinkwave",
            senderId = "user_me",
            recipientId = "user_pinkwave",
            text = "Thanks so much! Your rooftop choreography was next level!",
            timestamp = System.currentTimeMillis() - 5400000,
            isFromCurrentUser = true,
            isRead = true
        ),
        MessageEntity(
            id = "msg_3",
            conversationId = "conv_pinkwave",
            senderId = "user_pinkwave",
            recipientId = "user_me",
            text = "Let's do a collab next week with the new pink neon sound track! 🎵",
            timestamp = System.currentTimeMillis() - 1800000,
            isFromCurrentUser = false,
            isRead = false
        ),
        MessageEntity(
            id = "msg_4",
            conversationId = "conv_urbanframe",
            senderId = "user_urbanframe",
            recipientId = "user_me",
            text = "Are you joining the Tokyo cyber walk this weekend?",
            timestamp = System.currentTimeMillis() - 14400000,
            isFromCurrentUser = false,
            isRead = true
        )
    )

    val initialDrafts = listOf(
        DraftEntity(
            id = "draft_1",
            title = "Sunset Cyber Vibe",
            caption = "Testing neon filters on night streets #WIP",
            localUri = "",
            thumbnailResId = R.drawable.ava_thumb_1,
            durationSeconds = 15,
            filterName = "Cinematic Pink",
            updatedAt = System.currentTimeMillis() - 43200000
        )
    )

    val popularSounds = listOf(
        SoundTrack(
            id = "sound_1",
            title = "Midnight Pulse - Synthwave Mix",
            artist = "AVA Originals",
            usageCount = 42800,
            duration = "0:30",
            isSaved = true
        ),
        SoundTrack(
            id = "sound_2",
            title = "Electric Velocity (Pink Edit)",
            artist = "SoundLab AVA",
            usageCount = 124500,
            duration = "0:15",
            isSaved = false
        ),
        SoundTrack(
            id = "sound_3",
            title = "Lo-Fi Sunrise in Kyoto",
            artist = "Chill Vibe Collective",
            usageCount = 89200,
            duration = "0:45",
            isSaved = true
        ),
        SoundTrack(
            id = "sound_4",
            title = "Horizon Echoes - Ambient Drone",
            artist = "Apex Audio",
            usageCount = 31200,
            duration = "0:25",
            isSaved = false
        )
    )

    val stories = listOf(
        StoryItem(
            id = "story_1",
            user = creators[1],
            imageResId = R.drawable.ava_thumb_2,
            caption = "Backstage rehearsal live! ⚡"
        ),
        StoryItem(
            id = "story_2",
            user = creators[2],
            imageResId = R.drawable.ava_thumb_1,
            caption = "Rainy night scouting 🌃"
        ),
        StoryItem(
            id = "story_3",
            user = creators[3],
            imageResId = R.drawable.ava_thumb_3,
            caption = "Brew of the day ☕"
        ),
        StoryItem(
            id = "story_4",
            user = creators[4],
            imageResId = R.drawable.ava_thumb_4,
            caption = "Above the clouds today ☁️"
        )
    )

    val trendingHashtags = listOf(
        Pair("#AVA", "2.8M views"),
        Pair("#pinkwave", "1.4M views"),
        Pair("#dancechallenge", "980K views"),
        Pair("#cyberpunk", "750K views"),
        Pair("#coffeetime", "420K views"),
        Pair("#dronelife", "310K views"),
        Pair("#streetstyle", "290K views")
    )

    fun userEntities(): List<UserEntity> = creators.map { user ->
        UserEntity(
            id = user.id,
            username = user.username,
            displayName = user.displayName,
            avatarDrawableRes = user.avatarDrawableRes,
            bio = user.bio,
            website = user.website,
            followersCount = user.followersCount,
            followingCount = user.followingCount,
            likesCount = user.likesCount,
            isVerified = user.isVerified,
            isFollowing = user.isFollowing,
            isPrivate = user.isPrivate
        )
    }
}
