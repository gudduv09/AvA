package com.example.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Drafts
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.Draft
import com.example.domain.model.User
import com.example.domain.model.Video
import com.example.ui.components.formatCount
import com.example.ui.theme.AvaBackground
import com.example.ui.theme.AvaBorder
import com.example.ui.theme.AvaPrimaryPink
import com.example.ui.theme.AvaSecondaryPink
import com.example.ui.theme.AvaSurfacePrimary
import com.example.ui.theme.AvaSurfaceSecondary
import com.example.ui.theme.AvaTextPrimary
import com.example.ui.theme.AvaTextSecondary

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToVideo: (String) -> Unit,
    onNavigateToAuth: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val user by viewModel.currentUser.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val myVideos by viewModel.myVideos.collectAsStateWithLifecycle()
    val likedVideos by viewModel.likedVideos.collectAsStateWithLifecycle()
    val savedVideos by viewModel.savedVideos.collectAsStateWithLifecycle()
    val drafts by viewModel.drafts.collectAsStateWithLifecycle()

    var showEditProfile by remember { mutableStateOf(false) }

    if (showEditProfile) {
        EditProfileScreen(
            user = user,
            onBack = { showEditProfile = false },
            onSave = { name, bio, web ->
                viewModel.updateProfile(name, bio, web)
                showEditProfile = false
            }
        )
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(AvaBackground)
                .statusBarsPadding()
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable(enabled = onNavigateToAuth != null) {
                            onNavigateToAuth?.invoke()
                        }
                        .padding(4.dp)
                ) {
                    Text(
                        text = "@${user.username}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = AvaTextPrimary
                    )
                    if (onNavigateToAuth != null) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Filled.SwapHoriz,
                            contentDescription = "Switch Account / Authenticate",
                            tint = AvaSecondaryPink,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onNavigateToDashboard,
                        modifier = Modifier.testTag("button_creator_dashboard")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.BarChart,
                            contentDescription = "Creator Dashboard",
                            tint = AvaPrimaryPink,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier.testTag("button_settings")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings",
                            tint = AvaTextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // User Info Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(86.dp)
                        .clip(CircleShape)
                        .border(2.dp, AvaPrimaryPink, CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = user.avatarDrawableRes),
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user.displayName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AvaTextPrimary
                    )
                    if (user.isVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Verified",
                            tint = AvaPrimaryPink,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stats: Following, Followers, Likes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ProfileStatItem(count = formatCount(user.followingCount), label = "Following")
                    ProfileStatItem(count = formatCount(user.followersCount), label = "Followers")
                    ProfileStatItem(count = formatCount(user.likesCount), label = "Likes")
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bio & Website
                if (user.bio.isNotBlank()) {
                    Text(
                        text = user.bio,
                        fontSize = 13.sp,
                        color = AvaTextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }

                if (user.website.isNotBlank()) {
                    Text(
                        text = user.website,
                        fontSize = 12.sp,
                        color = AvaSecondaryPink,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Edit Profile Button
                Button(
                    onClick = { showEditProfile = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AvaSurfaceSecondary,
                        contentColor = AvaTextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(38.dp)
                        .testTag("button_edit_profile")
                ) {
                    Text(text = "Edit Profile", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Bar: My Videos, Liked, Saved, Drafts
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.5.dp, AvaBorder),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ProfileTabButton(
                    icon = Icons.Filled.GridOn,
                    isSelected = selectedTab == ProfileTab.MY_VIDEOS,
                    testTag = "profile_tab_my_videos",
                    onClick = { viewModel.selectTab(ProfileTab.MY_VIDEOS) }
                )
                ProfileTabButton(
                    icon = Icons.Filled.Favorite,
                    isSelected = selectedTab == ProfileTab.LIKED,
                    testTag = "profile_tab_liked",
                    onClick = { viewModel.selectTab(ProfileTab.LIKED) }
                )
                ProfileTabButton(
                    icon = Icons.Filled.Bookmark,
                    isSelected = selectedTab == ProfileTab.SAVED,
                    testTag = "profile_tab_saved",
                    onClick = { viewModel.selectTab(ProfileTab.SAVED) }
                )
                ProfileTabButton(
                    icon = Icons.Filled.Drafts,
                    isSelected = selectedTab == ProfileTab.DRAFTS,
                    testTag = "profile_tab_drafts",
                    onClick = { viewModel.selectTab(ProfileTab.DRAFTS) }
                )
            }

            // Grid Content
            when (selectedTab) {
                ProfileTab.DRAFTS -> {
                    if (drafts.isEmpty()) {
                        EmptyTabMessage("No saved drafts")
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            contentPadding = PaddingValues(2.dp),
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(drafts) { draft ->
                                DraftGridItem(draft = draft, onDelete = { viewModel.deleteDraft(draft.id) })
                            }
                        }
                    }
                }
                else -> {
                    val activeList = when (selectedTab) {
                        ProfileTab.MY_VIDEOS -> myVideos
                        ProfileTab.LIKED -> likedVideos
                        ProfileTab.SAVED -> savedVideos
                        else -> emptyList()
                    }

                    if (activeList.isEmpty()) {
                        EmptyTabMessage("No videos found")
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            contentPadding = PaddingValues(2.dp),
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(activeList) { video ->
                                VideoGridItem(video = video, onClick = { onNavigateToVideo(video.id) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileStatItem(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = AvaTextPrimary
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = AvaTextSecondary
        )
    }
}

@Composable
private fun ProfileTabButton(
    icon: ImageVector,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .testTag(testTag)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() }
            .padding(vertical = 12.dp)
            .width(50.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) AvaPrimaryPink else AvaTextSecondary,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun VideoGridItem(video: Video, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(3f / 4f)
            .background(AvaSurfaceSecondary)
            .clickable { onClick() }
    ) {
        if (video.thumbnailResId != 0) {
            Image(
                painter = painterResource(id = video.thumbnailResId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = formatCount(video.likesCount),
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DraftGridItem(draft: Draft, onDelete: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(3f / 4f)
            .background(AvaSurfaceSecondary)
    ) {
        if (draft.thumbnailResId != 0) {
            Image(
                painter = painterResource(id = draft.thumbnailResId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(24.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.6f))
                .clickable { onDelete() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Delete",
                tint = Color(0xFFEF4444),
                modifier = Modifier.size(14.dp)
            )
        }
        Text(
            text = "Draft",
            color = AvaPrimaryPink,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(4.dp)
        )
    }
}

@Composable
private fun EmptyTabMessage(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = message, color = AvaTextSecondary, fontSize = 14.sp)
    }
}

@Composable
fun EditProfileScreen(
    user: User,
    onBack: () -> Unit,
    onSave: (displayName: String, bio: String, website: String) -> Unit
) {
    var displayName by remember { mutableStateOf(user.displayName) }
    var bio by remember { mutableStateOf(user.bio) }
    var website by remember { mutableStateOf(user.website) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AvaBackground)
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(text = "Edit Profile", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = AvaTextPrimary)
            IconButton(onClick = { onSave(displayName, bio, website) }) {
                Icon(imageVector = Icons.Filled.Check, contentDescription = "Save", tint = AvaPrimaryPink)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Display Name", color = AvaTextSecondary, fontSize = 12.sp)
        TextField(
            value = displayName,
            onValueChange = { displayName = it },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = AvaSurfaceSecondary,
                unfocusedContainerColor = AvaSurfaceSecondary,
                focusedTextColor = AvaTextPrimary,
                unfocusedTextColor = AvaTextPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 14.dp)
        )

        Text(text = "Bio", color = AvaTextSecondary, fontSize = 12.sp)
        TextField(
            value = bio,
            onValueChange = { bio = it },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = AvaSurfaceSecondary,
                unfocusedContainerColor = AvaSurfaceSecondary,
                focusedTextColor = AvaTextPrimary,
                unfocusedTextColor = AvaTextPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(top = 4.dp, bottom = 14.dp)
        )

        Text(text = "Website", color = AvaTextSecondary, fontSize = 12.sp)
        TextField(
            value = website,
            onValueChange = { website = it },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = AvaSurfaceSecondary,
                unfocusedContainerColor = AvaSurfaceSecondary,
                focusedTextColor = AvaTextPrimary,
                unfocusedTextColor = AvaTextPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )
    }
}
