package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.Video
import com.example.ui.theme.AvaBorder
import com.example.ui.theme.AvaPrimaryPink
import com.example.ui.theme.AvaSecondaryPink
import com.example.ui.theme.AvaSurfacePrimary
import com.example.ui.theme.AvaSurfaceSecondary
import com.example.ui.theme.AvaTextPrimary
import com.example.ui.theme.AvaTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareBottomSheet(
    video: Video,
    onDismiss: () -> Unit,
    onSaveToDevice: () -> Unit,
    onReportVideo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Share to",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = AvaTextPrimary,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )

            // Direct share actions row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ShareAppItem(
                    title = "Copy Link",
                    icon = Icons.Filled.Link,
                    iconBg = AvaSurfaceSecondary,
                    onClick = {
                        clipboardManager.setText(AnnotatedString("https://ava.social/v/${video.id}"))
                        Toast.makeText(context, "Link copied to clipboard!", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                )

                ShareAppItem(
                    title = "System Share",
                    icon = Icons.Filled.Share,
                    iconBg = AvaPrimaryPink,
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Watch on AVA: ${video.caption}")
                            putExtra(Intent.EXTRA_TEXT, "Check out this video by @${video.creator.username} on AVA! https://ava.social/v/${video.id}")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share via"))
                        onDismiss()
                    }
                )

                ShareAppItem(
                    title = "Message",
                    icon = Icons.Filled.Send,
                    iconBg = AvaSurfaceSecondary,
                    onClick = {
                        Toast.makeText(context, "Shared to Direct Message", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                )

                ShareAppItem(
                    title = "Download",
                    icon = Icons.Filled.Download,
                    iconBg = AvaSurfaceSecondary,
                    onClick = {
                        onSaveToDevice()
                        Toast.makeText(context, "Video saved to gallery", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .height(0.5.dp)
                    .background(AvaBorder)
            )

            // Moderation actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                ShareAppItem(
                    title = "Not Interested",
                    icon = Icons.Filled.Block,
                    iconBg = AvaSurfaceSecondary,
                    onClick = {
                        Toast.makeText(context, "We will show fewer videos like this", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                )

                ShareAppItem(
                    title = "Report",
                    icon = Icons.Filled.Flag,
                    iconBg = Color(0xFF3B151E),
                    iconTint = Color(0xFFEF4444),
                    onClick = {
                        onReportVideo()
                        onDismiss()
                    }
                )
            }
        }
    }
}

@Composable
private fun ShareAppItem(
    title: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color = Color.White,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            color = AvaTextSecondary,
            fontWeight = FontWeight.Medium
        )
    }
}
