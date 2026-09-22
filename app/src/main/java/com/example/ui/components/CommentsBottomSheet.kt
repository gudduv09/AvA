package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.Comment
import com.example.ui.theme.AvaBorder
import com.example.ui.theme.AvaPrimaryPink
import com.example.ui.theme.AvaSurfacePrimary
import com.example.ui.theme.AvaSurfaceSecondary
import com.example.ui.theme.AvaTextPrimary
import com.example.ui.theme.AvaTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsBottomSheet(
    comments: List<Comment>,
    onDismiss: () -> Unit,
    onAddComment: (String) -> Unit,
    onLikeComment: (Comment) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var commentText by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AvaSurfacePrimary,
        contentColor = AvaTextPrimary,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 6.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .background(AvaBorder, RoundedCornerShape(2.dp))
            )
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.65f)
                .imePadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${comments.size} Comments",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AvaTextPrimary
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = AvaTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(AvaBorder)
            )

            // Comments List
            if (comments.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No comments yet. Be the first to vibe!",
                        color = AvaTextSecondary,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(comments, key = { it.id }) { comment ->
                        CommentRow(
                            comment = comment,
                            onLike = { onLikeComment(comment) }
                        )
                    }
                }
            }

            // Bottom Input Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AvaSurfaceSecondary)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = {
                        Text(
                            text = "Add a comment...",
                            color = AvaTextSecondary,
                            fontSize = 14.sp
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = AvaTextPrimary,
                        unfocusedTextColor = AvaTextPrimary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_comment")
                )

                IconButton(
                    onClick = {
                        if (commentText.isNotBlank()) {
                            onAddComment(commentText.trim())
                            commentText = ""
                        }
                    },
                    enabled = commentText.isNotBlank(),
                    modifier = Modifier.testTag("button_send_comment")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Send,
                        contentDescription = "Send",
                        tint = if (commentText.isNotBlank()) AvaPrimaryPink else AvaTextSecondary.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}

@Composable
private fun CommentRow(
    comment: Comment,
    onLike: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
        ) {
            if (comment.user.avatarDrawableRes != 0) {
                Image(
                    painter = painterResource(id = comment.user.avatarDrawableRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(36.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(AvaPrimaryPink),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = comment.user.displayName.take(1),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Text & Username
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "@${comment.user.username}",
                    color = AvaTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                if (comment.isPinned) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Filled.PushPin,
                        contentDescription = "Pinned",
                        tint = AvaPrimaryPink,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "Pinned",
                        color = AvaPrimaryPink,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = comment.text,
                color = AvaTextPrimary,
                fontSize = 14.sp
            )
        }

        // Like comment button
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clickable { onLike() }
                .padding(start = 8.dp)
        ) {
            Icon(
                imageVector = if (comment.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = "Like Comment",
                tint = if (comment.isLiked) AvaPrimaryPink else AvaTextSecondary,
                modifier = Modifier.size(16.dp)
            )
            if (comment.likesCount > 0) {
                Text(
                    text = comment.likesCount.toString(),
                    color = AvaTextSecondary,
                    fontSize = 10.sp
                )
            }
        }
    }
}
