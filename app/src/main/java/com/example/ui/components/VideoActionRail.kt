package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.domain.model.Video
import com.example.ui.theme.AvaPrimaryPink
import com.example.ui.theme.AvaSecondaryPink

@Composable
fun VideoActionRail(
    video: Video,
    onAvatarClick: () -> Unit,
    onFollowClick: () -> Unit,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit,
    onSaveClick: () -> Unit,
    onSoundClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Rotating sound disc animation
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Column(
        modifier = modifier.padding(end = 12.dp, bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Creator Avatar with Follow Button
        Box(
            modifier = Modifier
                .testTag("action_avatar")
                .size(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, Color.White, CircleShape)
                    .clickable { onAvatarClick() }
            ) {
                if (video.creator.avatarDrawableRes != 0) {
                    Image(
                        painter = painterResource(id = video.creator.avatarDrawableRes),
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(46.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(AvaSecondaryPink),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = video.creator.displayName.take(1),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Follow button pill overlay if not yet following
            if (!video.creator.isFollowing) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 8.dp)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(AvaPrimaryPink)
                        .clickable { onFollowClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Follow",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Like button & count
        ActionItem(
            icon = if (video.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            iconTint = if (video.isLiked) AvaPrimaryPink else Color.White,
            count = formatCount(video.likesCount),
            testTag = "action_like",
            onClick = onLikeClick
        )

        // Comments button & count
        ActionItem(
            icon = Icons.Outlined.ChatBubbleOutline,
            iconTint = Color.White,
            count = formatCount(video.commentsCount),
            testTag = "action_comment",
            onClick = onCommentClick
        )

        // Save / Bookmark button & count
        ActionItem(
            icon = if (video.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
            iconTint = if (video.isSaved) AvaPrimaryPink else Color.White,
            count = formatCount(video.savesCount),
            testTag = "action_save",
            onClick = onSaveClick
        )

        // Share button & count
        ActionItem(
            icon = Icons.Filled.Share,
            iconTint = Color.White,
            count = formatCount(video.sharesCount),
            testTag = "action_share",
            onClick = onShareClick
        )

        // Rotating Sound Disc
        Box(
            modifier = Modifier
                .testTag("action_sound")
                .size(42.dp)
                .rotate(rotation)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFF222222), Color(0xFF0F0F0F))
                    )
                )
                .border(1.5.dp, Color.White.copy(alpha = 0.8f), CircleShape)
                .clickable { onSoundClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.MusicNote,
                contentDescription = "Sound",
                tint = AvaPrimaryPink,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun ActionItem(
    icon: ImageVector,
    iconTint: Color,
    count: String,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .testTag(testTag)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(34.dp)
        )
        Text(
            text = count,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
        else -> count.toString()
    }
}
