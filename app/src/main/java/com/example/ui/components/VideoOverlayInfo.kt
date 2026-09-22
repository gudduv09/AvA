package com.example.ui.components

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.Video
import com.example.ui.theme.AvaPrimaryPink
import com.example.ui.theme.AvaSecondaryPink
import com.example.ui.theme.AvaTextPrimary

@Composable
fun VideoOverlayInfo(
    video: Video,
    onCreatorClick: () -> Unit,
    onHashtagClick: (String) -> Unit,
    onSoundClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 76.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        // Creator Display Name & Username
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onCreatorClick() }
        ) {
            Text(
                text = "@${video.creator.username}",
                color = AvaTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            if (video.creator.isVerified) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Verified",
                    tint = AvaPrimaryPink,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Caption
        Text(
            text = video.caption,
            color = Color.White.copy(alpha = 0.95f),
            fontSize = 14.sp,
            lineHeight = 19.sp,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Sound marquee bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { onSoundClick() }
                .fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Filled.MusicNote,
                contentDescription = "Sound",
                tint = AvaSecondaryPink,
                modifier = Modifier.size(15.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "${video.soundTitle} • ${video.soundArtist}",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                modifier = Modifier.basicMarquee()
            )
        }
    }
}
