package com.example.ui.screens.create

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.AvaBackground
import com.example.ui.theme.AvaBorder
import com.example.ui.theme.AvaPrimaryPink
import com.example.ui.theme.AvaSecondaryPink
import com.example.ui.theme.AvaSurfacePrimary
import com.example.ui.theme.AvaSurfaceSecondary
import com.example.ui.theme.AvaTextPrimary
import com.example.ui.theme.AvaTextSecondary

enum class EditorTab {
    FILTERS,
    VOICE,
    TRIM,
    TEXT
}

@Composable
fun VideoEditorScreen(
    viewModel: CreateViewModel,
    onBack: () -> Unit,
    onNavigateToPublish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val recordedThumb by viewModel.recordedThumbnailRes.collectAsStateWithLifecycle()
    val activeFilter by viewModel.activeFilter.collectAsStateWithLifecycle()
    val activeVoice by viewModel.activeVoiceEffect.collectAsStateWithLifecycle()
    val textOverlay by viewModel.textOverlay.collectAsStateWithLifecycle()
    val trimRange by viewModel.trimRange.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf(EditorTab.FILTERS) }

    // Color matrix calculation for filters
    val colorFilter = remember(activeFilter) {
        when (activeFilter) {
            VideoFilter.NORMAL -> null
            VideoFilter.CINEMATIC -> ColorFilter.colorMatrix(
                ColorMatrix().apply {
                    setToSaturation(1.3f)
                }
            )
            VideoFilter.PINK_GLOW -> ColorFilter.lighting(
                multiply = Color(0xFFFFD6E8),
                add = Color(0x33FF2D8D)
            )
            VideoFilter.CYBERPUNK -> ColorFilter.lighting(
                multiply = Color(0xFFC0EBFF),
                add = Color(0x33FF007F)
            )
            VideoFilter.NOIR -> ColorFilter.colorMatrix(
                ColorMatrix().apply {
                    setToSaturation(0f)
                }
            )
            VideoFilter.RETRO -> ColorFilter.lighting(
                multiply = Color(0xFFFFE8D6),
                add = Color(0x22FFAA33)
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AvaBackground)
    ) {
        // Video Preview
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp, bottom = 220.dp, start = 16.dp, end = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black)
        ) {
            Image(
                painter = painterResource(id = recordedThumb),
                contentDescription = null,
                colorFilter = colorFilter,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Dynamic Text Overlay
            if (textOverlay.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.65f))
                        .border(1.dp, AvaPrimaryPink, RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = textOverlay,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        // Top Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
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
                text = "Edit Video",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = AvaTextPrimary
            )

            Button(
                onClick = onNavigateToPublish,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AvaPrimaryPink,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("button_editor_next")
            ) {
                Text(text = "Next", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }

        // Bottom Controls Container
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(AvaSurfacePrimary)
                .navigationBarsPadding()
                .padding(vertical = 12.dp)
        ) {
            // Tab Buttons: Filters, Voice, Trim, Text
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                EditorTabItem(
                    label = "Filters",
                    icon = Icons.Filled.AutoAwesome,
                    isSelected = activeTab == EditorTab.FILTERS,
                    onClick = { activeTab = EditorTab.FILTERS }
                )
                EditorTabItem(
                    label = "Voice",
                    icon = Icons.Filled.Mic,
                    isSelected = activeTab == EditorTab.VOICE,
                    onClick = { activeTab = EditorTab.VOICE }
                )
                EditorTabItem(
                    label = "Trim",
                    icon = Icons.Filled.ContentCut,
                    isSelected = activeTab == EditorTab.TRIM,
                    onClick = { activeTab = EditorTab.TRIM }
                )
                EditorTabItem(
                    label = "Text",
                    icon = Icons.Filled.TextFields,
                    isSelected = activeTab == EditorTab.TEXT,
                    onClick = { activeTab = EditorTab.TEXT }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sub-options based on active tab
            when (activeTab) {
                EditorTab.FILTERS -> {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(VideoFilter.values()) { filter ->
                            val isSelected = filter == activeFilter
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) AvaPrimaryPink else AvaSurfaceSecondary)
                                    .border(
                                        1.dp,
                                        if (isSelected) AvaSecondaryPink else AvaBorder,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { viewModel.setFilter(filter) }
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = filter.displayName,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else AvaTextSecondary
                                )
                            }
                        }
                    }
                }
                EditorTab.VOICE -> {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(VoiceEffect.values()) { effect ->
                            val isSelected = effect == activeVoice
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) AvaPrimaryPink else AvaSurfaceSecondary)
                                    .border(
                                        1.dp,
                                        if (isSelected) AvaSecondaryPink else AvaBorder,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { viewModel.setVoiceEffect(effect) }
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = effect.displayName,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else AvaTextSecondary
                                )
                            }
                        }
                    }
                }
                EditorTab.TRIM -> {
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        Text(
                            text = "Trim Range: ${(trimRange.start * 15).toInt()}s - ${(trimRange.endInclusive * 15).toInt()}s",
                            color = AvaTextSecondary,
                            fontSize = 12.sp
                        )
                        RangeSlider(
                            value = trimRange,
                            onValueChange = { viewModel.setTrimRange(it) },
                            colors = SliderDefaults.colors(
                                thumbColor = AvaPrimaryPink,
                                activeTrackColor = AvaPrimaryPink,
                                inactiveTrackColor = AvaBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                EditorTab.TEXT -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = textOverlay,
                            onValueChange = { viewModel.setTextOverlay(it) },
                            placeholder = { Text("Add text to video...", color = AvaTextSecondary, fontSize = 13.sp) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = AvaSurfaceSecondary,
                                unfocusedContainerColor = AvaSurfaceSecondary,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = AvaTextPrimary,
                                unfocusedTextColor = AvaTextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )
                        if (textOverlay.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { viewModel.setTextOverlay("") },
                                colors = ButtonDefaults.buttonColors(containerColor = AvaSurfaceSecondary)
                            ) {
                                Text("Clear", color = AvaTextSecondary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EditorTabItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) AvaPrimaryPink else AvaTextSecondary,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) AvaPrimaryPink else AvaTextSecondary,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
