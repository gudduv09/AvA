package com.example.ui.screens.inbox

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.Conversation
import com.example.domain.model.DirectMessage
import com.example.domain.model.NotificationItem
import com.example.domain.model.NotificationType
import com.example.ui.theme.AvaBackground
import com.example.ui.theme.AvaBorder
import com.example.ui.theme.AvaPrimaryPink
import com.example.ui.theme.AvaSecondaryPink
import com.example.ui.theme.AvaSurfacePrimary
import com.example.ui.theme.AvaSurfaceSecondary
import com.example.ui.theme.AvaTextPrimary
import com.example.ui.theme.AvaTextSecondary

@Composable
fun InboxScreen(
    viewModel: InboxViewModel,
    modifier: Modifier = Modifier
) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val conversations by viewModel.conversations.collectAsStateWithLifecycle()
    val activeConversation by viewModel.activeConversation.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()

    if (activeConversation != null) {
        ChatScreen(
            conversation = activeConversation!!,
            messages = chatMessages,
            onBack = { viewModel.closeConversation() },
            onSendMessage = { text -> viewModel.sendMessage(text) }
        )
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(AvaBackground)
                .statusBarsPadding()
        ) {
            // Header Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    Text(
                        text = "Activity",
                        fontSize = 18.sp,
                        fontWeight = if (selectedTab == InboxTab.NOTIFICATIONS) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == InboxTab.NOTIFICATIONS) AvaTextPrimary else AvaTextSecondary,
                        modifier = Modifier
                            .testTag("inbox_tab_notifications")
                            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                viewModel.selectTab(InboxTab.NOTIFICATIONS)
                            }
                    )

                    Text(
                        text = "Messages",
                        fontSize = 18.sp,
                        fontWeight = if (selectedTab == InboxTab.MESSAGES) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == InboxTab.MESSAGES) AvaTextPrimary else AvaTextSecondary,
                        modifier = Modifier
                            .testTag("inbox_tab_messages")
                            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                viewModel.selectTab(InboxTab.MESSAGES)
                            }
                    )
                }

                if (selectedTab == InboxTab.NOTIFICATIONS) {
                    IconButton(
                        onClick = { viewModel.markAllNotificationsRead() },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DoneAll,
                            contentDescription = "Mark all read",
                            tint = AvaTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(AvaBorder)
            )

            // Content
            when (selectedTab) {
                InboxTab.NOTIFICATIONS -> {
                    if (notifications.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No notifications yet", color = AvaTextSecondary)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            items(notifications) { notif ->
                                NotificationRow(notif = notif)
                            }
                        }
                    }
                }
                InboxTab.MESSAGES -> {
                    if (conversations.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No direct messages yet", color = AvaTextSecondary)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            items(conversations) { conv ->
                                ConversationRow(
                                    conv = conv,
                                    onClick = { viewModel.openConversation(conv) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationRow(notif: NotificationItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar + Badge
        Box(modifier = Modifier.size(46.dp)) {
            Image(
                painter = painterResource(id = notif.user.avatarDrawableRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
            )

            // Badges
            val (icon, badgeColor) = when (notif.type) {
                NotificationType.LIKE -> Pair(Icons.Filled.Favorite, AvaPrimaryPink)
                NotificationType.COMMENT -> Pair(Icons.Filled.ChatBubble, AvaSecondaryPink)
                NotificationType.FOLLOW -> Pair(Icons.Filled.PersonAdd, Color(0xFF3B82F6))
                NotificationType.MENTION -> Pair(Icons.Filled.CheckCircle, Color(0xFF10B981))
                NotificationType.SYSTEM -> Pair(Icons.Filled.Warning, AvaPrimaryPink)
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(badgeColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Text Info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = notif.user.displayName,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = AvaTextPrimary
            )
            Text(
                text = notif.content,
                fontSize = 13.sp,
                color = AvaTextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Target Video Thumbnail if exists
        if (notif.targetVideoThumbnailRes != 0) {
            Spacer(modifier = Modifier.width(10.dp))
            Image(
                painter = painterResource(id = notif.targetVideoThumbnailRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(6.dp))
            )
        }
    }
}

@Composable
private fun ConversationRow(
    conv: Conversation,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = conv.user.avatarDrawableRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = conv.user.displayName,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = AvaTextPrimary
            )
            Text(
                text = conv.lastMessage,
                fontSize = 13.sp,
                color = AvaTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        if (conv.unreadCount > 0) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(AvaPrimaryPink),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = conv.unreadCount.toString(),
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ChatScreen(
    conversation: Conversation,
    messages: List<DirectMessage>,
    onBack: () -> Unit,
    onSendMessage: (String) -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    val quickEmojis = listOf("🔥", "❤️", "⚡", "👏", "✨", "💯")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AvaBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        // Chat Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }

            Image(
                painter = painterResource(id = conversation.user.avatarDrawableRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = conversation.user.displayName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AvaTextPrimary
                )
                Text(
                    text = "@${conversation.user.username}",
                    fontSize = 12.sp,
                    color = AvaTextSecondary
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(AvaBorder)
        )

        // Messages History
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { msg ->
                val isMe = msg.isFromCurrentUser
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .clip(
                                RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (isMe) 16.dp else 2.dp,
                                    bottomEnd = if (isMe) 2.dp else 16.dp
                                )
                            )
                            .background(if (isMe) AvaPrimaryPink else AvaSurfaceSecondary)
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = msg.text,
                            color = if (isMe) Color.White else AvaTextPrimary,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Quick Emojis Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(quickEmojis) { emoji ->
                Text(
                    text = emoji,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .clickable { onSendMessage(emoji) }
                        .padding(4.dp)
                )
            }
        }

        // Bottom Typing Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AvaSurfacePrimary)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = messageText,
                onValueChange = { messageText = it },
                placeholder = { Text("Send a message...", color = AvaTextSecondary, fontSize = 14.sp) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = AvaSurfaceSecondary,
                    unfocusedContainerColor = AvaSurfaceSecondary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = AvaTextPrimary,
                    unfocusedTextColor = AvaTextPrimary
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_field")
            )

            IconButton(
                onClick = {
                    if (messageText.isNotBlank()) {
                        onSendMessage(messageText.trim())
                        messageText = ""
                    }
                },
                enabled = messageText.isNotBlank()
            ) {
                Icon(
                    imageVector = Icons.Filled.Send,
                    contentDescription = "Send",
                    tint = if (messageText.isNotBlank()) AvaPrimaryPink else AvaTextSecondary.copy(alpha = 0.4f)
                )
            }
        }
    }
}
