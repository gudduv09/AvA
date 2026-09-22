package com.example.ui.screens.feed

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.Video
import com.example.ui.components.AvaGeometricMark
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.components.ShareBottomSheet
import com.example.ui.components.SoundSheet
import com.example.ui.components.StoryBar
import com.example.ui.components.StoryViewerDialog
import com.example.ui.components.VideoActionRail
import com.example.ui.components.VideoOverlayInfo
import com.example.ui.components.VideoPlayerView
import com.example.ui.theme.AvaBackground
import com.example.ui.theme.AvaPrimaryPink
import com.example.ui.theme.AvaSecondaryPink
import com.example.ui.theme.AvaTextPrimary
import com.example.ui.theme.AvaTextSecondary

@Composable
fun FeedScreen(
    viewModel: FeedViewModel,
    onNavigateToSearch: () -> Unit,
    onNavigateToCreatorProfile: (String) -> Unit,
    onUseSound: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val videos by viewModel.displayedVideos.collectAsStateWithLifecycle()
    val stories by viewModel.stories.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()

    val activeCommentsVideo by viewModel.activeCommentsVideo.collectAsStateWithLifecycle()
    val comments by viewModel.commentsForActiveVideo.collectAsStateWithLifecycle()
    val activeShareVideo by viewModel.activeShareVideo.collectAsStateWithLifecycle()
    val activeSoundVideo by viewModel.activeSoundVideo.collectAsStateWithLifecycle()
    val viewingStoryIndex by viewModel.viewingStoryIndex.collectAsStateWithLifecycle()

    var showStoriesBar by remember { mutableStateOf(false) }

    // Video options & moderation state
    var activeOptionsVideo by remember { mutableStateOf<Video?>(null) }
    var reportingVideo by remember { mutableStateOf<Video?>(null) }
    var currentQuality by remember { mutableStateOf("Auto (1080p)") }
    var currentSpeed by remember { mutableStateOf(1.0f) }

    fun triggerHaptic() {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(35)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AvaBackground)
    ) {
        if (videos.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AvaGeometricMark(size = 48.dp, tint = AvaTextSecondary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (selectedTab == FeedTab.FOLLOWING) "No videos from creators you follow yet." else "Loading feed...",
                        color = AvaTextSecondary,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            val pagerState = rememberPagerState(pageCount = { videos.size })

            VerticalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("vertical_video_feed"),
                key = { page -> videos.getOrNull(page)?.id ?: page }
            ) { page ->
                val video = videos[page]
                val isActive = page == pagerState.currentPage

                Box(modifier = Modifier.fillMaxSize()) {
                    // Video View
                    VideoPlayerView(
                        video = video,
                        isActive = isActive,
                        onDoubleTapLike = {
                            triggerHaptic()
                            if (!video.isLiked) {
                                viewModel.onLikeVideo(video)
                            }
                        },
                        onLongPressOptions = {
                            activeOptionsVideo = video
                        }
                    )

                    // Right Action Rail
                    VideoActionRail(
                        video = video,
                        onAvatarClick = { onNavigateToCreatorProfile(video.creator.id) },
                        onFollowClick = {
                            triggerHaptic()
                            viewModel.onFollowCreator(video.creator.id, video.creator.isFollowing)
                        },
                        onLikeClick = {
                            triggerHaptic()
                            viewModel.onLikeVideo(video)
                        },
                        onCommentClick = { viewModel.openComments(video) },
                        onShareClick = { viewModel.openShare(video) },
                        onSaveClick = {
                            triggerHaptic()
                            viewModel.onSaveVideo(video)
                        },
                        onSoundClick = { viewModel.openSound(video) },
                        modifier = Modifier.align(Alignment.BottomEnd)
                    )

                    // Bottom Information Overlay
                    VideoOverlayInfo(
                        video = video,
                        onCreatorClick = { onNavigateToCreatorProfile(video.creator.id) },
                        onHashtagClick = { onNavigateToSearch() },
                        onSoundClick = { viewModel.openSound(video) },
                        modifier = Modifier.align(Alignment.BottomStart)
                    )
                }
            }
        }

        // Top Navigation Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Top Left: Live Stories button
                IconButton(
                    onClick = { showStoriesBar = !showStoriesBar },
                    modifier = Modifier.testTag("top_stories_toggle")
                ) {
                    Icon(
                        imageVector = Icons.Filled.LiveTv,
                        contentDescription = "Stories",
                        tint = if (showStoriesBar) AvaPrimaryPink else Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Center Tabs: Following | For You
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Following",
                        color = if (selectedTab == FeedTab.FOLLOWING) Color.White else Color.White.copy(alpha = 0.55f),
                        fontSize = 16.sp,
                        fontWeight = if (selectedTab == FeedTab.FOLLOWING) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier
                            .testTag("tab_following")
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                viewModel.selectTab(FeedTab.FOLLOWING)
                            }
                    )

                    Box(
                        modifier = Modifier
                            .size(3.dp)
                            .background(Color.White.copy(alpha = 0.4f), CircleShape)
                    )

                    Text(
                        text = "For You",
                        color = if (selectedTab == FeedTab.FOR_YOU) Color.White else Color.White.copy(alpha = 0.55f),
                        fontSize = 16.sp,
                        fontWeight = if (selectedTab == FeedTab.FOR_YOU) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier
                            .testTag("tab_for_you")
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                viewModel.selectTab(FeedTab.FOR_YOU)
                            }
                    )
                }

                // Top Right: Search Shortcut
                IconButton(
                    onClick = onNavigateToSearch,
                    modifier = Modifier.testTag("top_search_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Expandable Story Bar
            AnimatedVisibility(
                visible = showStoriesBar,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                StoryBar(
                    stories = stories,
                    onStoryClick = { index -> viewModel.openStoryViewer(index) },
                    onAddStoryClick = {
                        viewModel.addStory()
                        android.widget.Toast.makeText(context, "Story shared to AVA! ✨", android.widget.Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        // Active Comments Bottom Sheet
        activeCommentsVideo?.let { video ->
            CommentsBottomSheet(
                comments = comments,
                onDismiss = { viewModel.closeComments() },
                onAddComment = { text -> viewModel.addComment(video.id, text) },
                onLikeComment = { comment -> viewModel.toggleCommentLike(comment) }
            )
        }

        // Active Share Bottom Sheet
        activeShareVideo?.let { video ->
            ShareBottomSheet(
                video = video,
                onDismiss = { viewModel.closeShare() },
                onSaveToDevice = {
                    viewModel.downloadVideo(video, currentQuality)
                    android.widget.Toast.makeText(context, "Saved to Offline Downloads", android.widget.Toast.LENGTH_SHORT).show()
                },
                onReportVideo = {
                    reportingVideo = video
                }
            )
        }

        // Active Playback / Quality / Moderation Options Sheet
        activeOptionsVideo?.let { video ->
            com.example.ui.components.VideoOptionsMenuSheet(
                video = video,
                currentQuality = currentQuality,
                currentSpeed = currentSpeed,
                onQualitySelected = { q ->
                    currentQuality = q
                    activeOptionsVideo = null
                    android.widget.Toast.makeText(context, "Streaming quality: $q", android.widget.Toast.LENGTH_SHORT).show()
                },
                onSpeedSelected = { sp ->
                    currentSpeed = sp
                    activeOptionsVideo = null
                    android.widget.Toast.makeText(context, "Playback speed: ${sp}x", android.widget.Toast.LENGTH_SHORT).show()
                },
                onDownload = {
                    viewModel.downloadVideo(video, currentQuality)
                    activeOptionsVideo = null
                    android.widget.Toast.makeText(context, "Saved to Offline Downloads", android.widget.Toast.LENGTH_SHORT).show()
                },
                onNotInterested = {
                    viewModel.hideVideo(video.id)
                    activeOptionsVideo = null
                    android.widget.Toast.makeText(context, "Video hidden. We'll show fewer like this.", android.widget.Toast.LENGTH_SHORT).show()
                },
                onReport = {
                    activeOptionsVideo = null
                    reportingVideo = video
                },
                onDismiss = { activeOptionsVideo = null }
            )
        }

        // Report Dialog
        reportingVideo?.let { video ->
            com.example.ui.components.ReportDialog(
                targetId = video.id,
                targetType = "Video",
                targetTitle = video.caption.take(30),
                onDismiss = { reportingVideo = null },
                onSubmitReport = { reason, details ->
                    viewModel.reportVideo(video.id, reason, details)
                },
                onBlockUser = {
                    viewModel.blockCreator(video.creator.id, video.creator.username, video.creator.displayName)
                },
                onMuteUser = {
                    viewModel.muteCreator(video.creator.id, video.creator.username, video.creator.displayName)
                }
            )
        }

        // Active Sound Details Bottom Sheet
        activeSoundVideo?.let { video ->
            SoundSheet(
                video = video,
                onDismiss = { viewModel.closeSound() },
                onUseSound = { soundTitle -> onUseSound(soundTitle) }
            )
        }

        // Active Story Fullscreen Viewer
        viewingStoryIndex?.let { index ->
            StoryViewerDialog(
                stories = stories,
                initialIndex = index,
                onDismiss = { viewModel.closeStoryViewer() }
            )
        }
    }
}
