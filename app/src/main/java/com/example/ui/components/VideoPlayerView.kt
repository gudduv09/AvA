package com.example.ui.components

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.domain.model.Video
import com.example.ui.theme.AvaBackground
import com.example.ui.theme.AvaPrimaryPink
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerView(
    video: Video,
    isActive: Boolean,
    onDoubleTapLike: () -> Unit,
    onLongPressOptions: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(true) }
    var isMuted by remember { mutableStateOf(false) }
    var isBuffering by remember { mutableStateOf(true) }
    var currentProgress by remember { mutableFloatStateOf(0f) }
    var showHeartAnimation by remember { mutableStateOf(false) }
    var heartOffset by remember { mutableStateOf(Offset.Zero) }

    val coroutineScope = rememberCoroutineScope()

    // ExoPlayer lifecycle management
    val exoPlayer = remember(video.videoUrl) {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
            val mediaItem = MediaItem.fromUri(Uri.parse(video.videoUrl))
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = isActive
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    isBuffering = playbackState == Player.STATE_BUFFERING
                }

                override fun onIsPlayingChanged(playing: Boolean) {
                    isPlaying = playing
                }
            })
        }
    }

    // React to pager active status
    LaunchedEffect(isActive) {
        if (isActive) {
            exoPlayer.playWhenReady = true
        } else {
            exoPlayer.playWhenReady = false
            exoPlayer.seekTo(0)
        }
    }

    // Playback progress tracking
    LaunchedEffect(isActive, isPlaying) {
        while (isActive && isPlaying) {
            val dur = exoPlayer.duration
            if (dur > 0) {
                currentProgress = exoPlayer.currentPosition.toFloat() / dur.toFloat()
            }
            delay(100)
        }
    }

    DisposableEffect(video.id) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AvaBackground)
            .pointerInput(video.id) {
                detectTapGestures(
                    onTap = {
                        if (exoPlayer.isPlaying) {
                            exoPlayer.pause()
                        } else {
                            exoPlayer.play()
                        }
                    },
                    onDoubleTap = { offset ->
                        heartOffset = offset
                        showHeartAnimation = true
                        onDoubleTapLike()
                        coroutineScope.launch {
                            delay(700)
                            showHeartAnimation = false
                        }
                    },
                    onLongPress = {
                        onLongPressOptions()
                    }
                )
            }
    ) {
        // Fallback thumbnail / background
        if (video.thumbnailResId != 0) {
            Image(
                painter = painterResource(id = video.thumbnailResId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // ExoPlayer Surface
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Gradient vignettes for readable text and controls
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.35f),
                            Color.Transparent,
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.8f)
                        )
                    )
                )
        )

        // Buffering indicator
        if (isBuffering && isActive) {
            CircularProgressIndicator(
                color = AvaPrimaryPink,
                strokeWidth = 3.dp,
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.Center)
            )
        }

        // Play/Pause icon indicator when paused by user
        AnimatedVisibility(
            visible = !isPlaying && !isBuffering,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .background(Color.Black.copy(alpha = 0.55f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Paused",
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(44.dp)
                )
            }
        }

        // Double-tap heart explosion animation
        if (showHeartAnimation) {
            val scaleAnim = remember { Animatable(0.2f) }
            val alphaAnim = remember { Animatable(1f) }

            LaunchedEffect(showHeartAnimation) {
                launch {
                    scaleAnim.animateTo(
                        targetValue = 1.6f,
                        animationSpec = tween(400, easing = FastOutSlowInEasing)
                    )
                }
                launch {
                    delay(300)
                    alphaAnim.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(350, easing = LinearEasing)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .offset(
                        x = (heartOffset.x / 3f - 30).dp,
                        y = (heartOffset.y / 3f - 30).dp
                    )
                    .scale(scaleAnim.value)
                    .alpha(alphaAnim.value)
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Liked",
                    tint = AvaPrimaryPink,
                    modifier = Modifier.size(80.dp)
                )
            }
        }

        // Video scrub progress bar at the very bottom
        LinearProgressIndicator(
            progress = { currentProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .align(Alignment.BottomCenter),
            color = AvaPrimaryPink,
            trackColor = Color.White.copy(alpha = 0.2f)
        )
    }
}
