package com.example.ui.screens.discover

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.SoundTrack
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
fun DiscoverScreen(
    viewModel: DiscoverViewModel,
    onNavigateToVideo: (String) -> Unit,
    onNavigateToProfile: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val recentSearches by viewModel.recentSearches.collectAsStateWithLifecycle()
    val filteredVideos by viewModel.filteredVideos.collectAsStateWithLifecycle()
    val filteredUsers by viewModel.filteredUsers.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AvaBackground)
            .statusBarsPadding()
    ) {
        // Search Input Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = {
                    Text(
                        text = "Search videos, creators, sounds...",
                        color = AvaTextSecondary,
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = AvaTextSecondary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = "Clear",
                                tint = AvaTextSecondary
                            )
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        viewModel.submitSearch(searchQuery)
                        focusManager.clearFocus()
                    }
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = AvaSurfaceSecondary,
                    unfocusedContainerColor = AvaSurfaceSecondary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = AvaTextPrimary,
                    unfocusedTextColor = AvaTextPrimary
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_input_field")
            )
        }

        // Filter chips row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(SearchFilter.values()) { filter ->
                val isSelected = filter == selectedFilter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) AvaPrimaryPink else AvaSurfaceSecondary)
                        .border(
                            1.dp,
                            if (isSelected) AvaSecondaryPink else AvaBorder,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { viewModel.selectFilter(filter) }
                        .padding(horizontal = 16.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = filter.name.lowercase().replaceFirstChar { it.uppercase() },
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else AvaTextSecondary
                    )
                }
            }
        }

        // Search Content
        if (searchQuery.isEmpty()) {
            // Default Discover Landing: Recent searches, Trending hashtags, Popular sounds, Featured Creators
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // Recent Searches
                if (recentSearches.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Recent Searches",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = AvaTextPrimary
                            )
                            Text(
                                text = "Clear",
                                fontSize = 12.sp,
                                color = AvaTextSecondary,
                                modifier = Modifier.clickable { viewModel.clearRecentSearches() }
                            )
                        }
                    }

                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            items(recentSearches) { search ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(AvaSurfaceSecondary)
                                        .clickable { viewModel.updateSearchQuery(search) }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.History,
                                        contentDescription = null,
                                        tint = AvaTextSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = search,
                                        color = AvaTextPrimary,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Trending Hashtags
                item {
                    Text(
                        text = "Trending Vibes",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AvaTextPrimary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        items(viewModel.trendingHashtags) { (tag, views) ->
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AvaSurfaceSecondary)
                                    .border(0.5.dp, AvaBorder, RoundedCornerShape(12.dp))
                                    .clickable { viewModel.updateSearchQuery(tag) }
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(AvaPrimaryPink.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.TrendingUp,
                                        contentDescription = null,
                                        tint = AvaPrimaryPink,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = tag,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AvaTextPrimary
                                    )
                                    Text(
                                        text = views,
                                        fontSize = 11.sp,
                                        color = AvaTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                // Trending Sounds
                item {
                    Text(
                        text = "Trending Sounds",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AvaTextPrimary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                items(viewModel.popularSounds) { sound ->
                    SoundRow(sound = sound, onUseSound = { viewModel.updateSearchQuery(sound.title) })
                }

                // Featured Videos Header
                item {
                    Text(
                        text = "Explore Popular",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AvaTextPrimary,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
                    )
                }

                // Grid of Videos in Discover
                item {
                    Box(modifier = Modifier.height(400.dp)) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredVideos) { video ->
                                DiscoverVideoCard(
                                    video = video,
                                    onClick = { onNavigateToVideo(video.id) }
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Search Results
            when (selectedFilter) {
                SearchFilter.USERS -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredUsers) { user ->
                            CreatorSearchRow(
                                user = user,
                                onFollowToggle = { viewModel.toggleFollow(user) },
                                onClick = { onNavigateToProfile(user.id) }
                            )
                        }
                    }
                }
                else -> {
                    // Video grid results
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredVideos) { video ->
                            DiscoverVideoCard(
                                video = video,
                                onClick = { onNavigateToVideo(video.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DiscoverVideoCard(
    video: Video,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(9f / 14f)
            .clip(RoundedCornerShape(12.dp))
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

        // Dark gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        // Play icon + count at bottom left
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = formatCount(video.likesCount),
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Creator name at top left
        Text(
            text = "@${video.creator.username}",
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        )
    }
}

@Composable
private fun SoundRow(
    sound: SoundTrack,
    onUseSound: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onUseSound() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AvaSurfaceSecondary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.MusicNote,
                    contentDescription = null,
                    tint = AvaPrimaryPink,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = sound.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AvaTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${sound.artist} • ${formatCount(sound.usageCount)} videos",
                    fontSize = 12.sp,
                    color = AvaTextSecondary
                )
            }
        }

        Text(
            text = sound.duration,
            fontSize = 12.sp,
            color = AvaTextSecondary
        )
    }
}

@Composable
private fun CreatorSearchRow(
    user: User,
    onFollowToggle: () -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AvaSurfaceSecondary)
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Image(
                painter = painterResource(id = user.avatarDrawableRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user.displayName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AvaTextPrimary
                    )
                    if (user.isVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Verified",
                            tint = AvaPrimaryPink,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Text(
                    text = "@${user.username} • ${formatCount(user.followersCount)} followers",
                    fontSize = 12.sp,
                    color = AvaTextSecondary
                )
            }
        }

        Button(
            onClick = onFollowToggle,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (user.isFollowing) AvaSurfacePrimary else AvaPrimaryPink,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.height(34.dp)
        ) {
            Text(
                text = if (user.isFollowing) "Following" else "Follow",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
