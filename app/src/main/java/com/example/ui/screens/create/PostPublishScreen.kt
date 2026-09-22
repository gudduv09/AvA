package com.example.ui.screens.create

import android.widget.Toast
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.VideoVisibility
import com.example.ui.theme.AvaBackground
import com.example.ui.theme.AvaBorder
import com.example.ui.theme.AvaPrimaryPink
import com.example.ui.theme.AvaSecondaryPink
import com.example.ui.theme.AvaSurfacePrimary
import com.example.ui.theme.AvaSurfaceSecondary
import com.example.ui.theme.AvaTextPrimary
import com.example.ui.theme.AvaTextSecondary

@Composable
fun PostPublishScreen(
    viewModel: CreateViewModel,
    onBack: () -> Unit,
    onPublished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val recordedThumb by viewModel.recordedThumbnailRes.collectAsStateWithLifecycle()
    val caption by viewModel.caption.collectAsStateWithLifecycle()
    val visibility by viewModel.visibility.collectAsStateWithLifecycle()
    val allowComments by viewModel.allowComments.collectAsStateWithLifecycle()
    val allowDuet by viewModel.allowDuet.collectAsStateWithLifecycle()
    val allowDownload by viewModel.allowDownload.collectAsStateWithLifecycle()

    val quickHashtags = listOf("#AVA", "#vibe", "#dance", "#aesthetic", "#cyberpunk", "#music")
    val quickMentions = listOf("@pinkwave", "@urbanframe", "@dailyvibe")

    var isPublishing by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AvaBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Text(
                text = "New Post",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = AvaTextPrimary
            )

            Spacer(modifier = Modifier.width(48.dp))
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Video Preview & Caption Input Block
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(AvaSurfaceSecondary)
                        .padding(12.dp)
                ) {
                    // Caption Field
                    TextField(
                        value = caption,
                        onValueChange = { viewModel.setCaption(it) },
                        placeholder = {
                            Text(
                                text = "Describe your video, add #hashtags, or mention creators...",
                                color = AvaTextSecondary,
                                fontSize = 13.sp
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
                            .height(110.dp)
                            .testTag("publish_caption_input")
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    // Thumbnail Preview
                    Box(
                        modifier = Modifier
                            .width(75.dp)
                            .aspectRatio(9f / 14f)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, AvaBorder, RoundedCornerShape(8.dp))
                    ) {
                        Image(
                            painter = painterResource(id = recordedThumb),
                            contentDescription = "Cover",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // Quick Hashtags Chips
            item {
                Column {
                    Text(
                        text = "Hashtags",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AvaTextPrimary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(quickHashtags) { tag ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(AvaSurfaceSecondary)
                                    .border(1.dp, AvaBorder, RoundedCornerShape(16.dp))
                                    .clickable { viewModel.addHashtag(tag) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = tag,
                                    fontSize = 12.sp,
                                    color = AvaPrimaryPink,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // Quick Mentions Chips
            item {
                Column {
                    Text(
                        text = "Mentions",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AvaTextPrimary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(quickMentions) { mention ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(AvaSurfaceSecondary)
                                    .border(1.dp, AvaBorder, RoundedCornerShape(16.dp))
                                    .clickable { viewModel.addHashtag(mention) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = mention,
                                    fontSize = 12.sp,
                                    color = AvaTextPrimary
                                )
                            }
                        }
                    }
                }
            }

            // Visibility / Audience
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(AvaSurfaceSecondary)
                        .padding(14.dp)
                ) {
                    Text(
                        text = "Who can watch this video",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AvaTextPrimary,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        VideoVisibility.values().forEach { vis ->
                            val isSelected = vis == visibility
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) AvaPrimaryPink else AvaSurfacePrimary)
                                    .clickable { viewModel.setVisibility(vis) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = vis.name.lowercase().replaceFirstChar { it.uppercase() },
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else AvaTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Interaction Settings Switches
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(AvaSurfaceSecondary)
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SwitchRow(
                        title = "Allow Comments",
                        checked = allowComments,
                        onCheckedChange = { viewModel.toggleComments() }
                    )
                    SwitchRow(
                        title = "Allow Duet & Remix",
                        checked = allowDuet,
                        onCheckedChange = { viewModel.toggleDuet() }
                    )
                    SwitchRow(
                        title = "Allow Downloads",
                        checked = allowDownload,
                        onCheckedChange = { viewModel.toggleDownload() }
                    )
                }
            }
        }

        val uploadStatus by viewModel.uploadStatus.collectAsStateWithLifecycle()
        val uploadProgress by viewModel.uploadProgress.collectAsStateWithLifecycle()

        // Uploading indicator card
        if (isPublishing) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AvaSurfaceSecondary)
                    .border(1.dp, AvaBorder, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = uploadStatus ?: "Processing video...",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AvaPrimaryPink
                    )
                    Text(
                        text = "${(uploadProgress * 100).toInt()}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AvaTextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                androidx.compose.material3.LinearProgressIndicator(
                    progress = { uploadProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = AvaPrimaryPink,
                    trackColor = AvaSurfacePrimary
                )
            }
        }

        // Bottom Action Buttons: Save Draft, Post to AVA
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = {
                    viewModel.saveDraft {
                        Toast.makeText(context, "Saved to Drafts", Toast.LENGTH_SHORT).show()
                        onPublished()
                    }
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AvaTextPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(imageVector = Icons.Filled.Bookmark, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Draft")
            }

            Button(
                onClick = {
                    isPublishing = true
                    viewModel.publishVideo {
                        isPublishing = false
                        Toast.makeText(context, "Video Published to AVA! ✨", Toast.LENGTH_LONG).show()
                        onPublished()
                    }
                },
                enabled = !isPublishing,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AvaPrimaryPink,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(2f)
                    .testTag("button_publish_post")
            ) {
                Icon(imageVector = Icons.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = if (isPublishing) "Publishing..." else "Post to AVA", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = AvaTextPrimary,
            fontSize = 14.sp
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AvaPrimaryPink,
                uncheckedThumbColor = AvaTextSecondary,
                uncheckedTrackColor = AvaSurfacePrimary
            )
        )
    }
}
