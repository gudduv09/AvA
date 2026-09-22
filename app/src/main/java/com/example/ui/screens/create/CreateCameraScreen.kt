package com.example.ui.screens.create

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.theme.AvaBorder
import com.example.ui.theme.AvaPrimaryPink
import com.example.ui.theme.AvaSecondaryPink
import com.example.ui.theme.AvaSurfacePrimary
import com.example.ui.theme.AvaSurfaceSecondary
import com.example.ui.theme.AvaTextPrimary
import com.example.ui.theme.AvaTextSecondary

@Composable
fun CreateCameraScreen(
    viewModel: CreateViewModel,
    onClose: () -> Unit,
    onNavigateToEditor: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val isRecording by viewModel.isRecording.collectAsStateWithLifecycle()
    val recordProgress by viewModel.recordProgress.collectAsStateWithLifecycle()
    val selectedDuration by viewModel.selectedDuration.collectAsStateWithLifecycle()
    val isFlashOn by viewModel.isFlashOn.collectAsStateWithLifecycle()
    val isFrontCamera by viewModel.isFrontCamera.collectAsStateWithLifecycle()
    val speed by viewModel.speed.collectAsStateWithLifecycle()
    val selectedSound by viewModel.selectedSound.collectAsStateWithLifecycle()

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Camera Preview / Fallback simulation
        if (hasCameraPermission) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        try {
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }
                            val cameraSelector = if (isFrontCamera) {
                                CameraSelector.DEFAULT_FRONT_CAMERA
                            } else {
                                CameraSelector.DEFAULT_BACK_CAMERA
                            }
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
                        } catch (_: Exception) {
                            // Handled gracefully in emulator or restricted device
                        }
                    }, ContextCompat.getMainExecutor(ctx))
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // High fidelity simulated viewfinder background
            Image(
                painter = painterResource(id = R.drawable.ava_thumb_1),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Top Vignette gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent)
                    )
                )
        )

        // Bottom Vignette gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                    )
                )
        )

        // Recording Progress Bar at top
        if (recordProgress > 0f) {
            LinearProgressIndicator(
                progress = { recordProgress },
                color = AvaPrimaryPink,
                trackColor = Color.White.copy(alpha = 0.25f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .statusBarsPadding()
            )
        }

        // Top Bar: Close, Add Sound pill, Camera Tools
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Close button
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.testTag("camera_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Add Sound Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.55f))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.MusicNote,
                        contentDescription = null,
                        tint = AvaPrimaryPink,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = selectedSound ?: "Add Sound",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }

                // Flash toggle
                IconButton(onClick = { viewModel.toggleFlash() }) {
                    Icon(
                        imageVector = if (isFlashOn) Icons.Filled.FlashOn else Icons.Filled.FlashOff,
                        contentDescription = "Flash",
                        tint = if (isFlashOn) AvaPrimaryPink else Color.White
                    )
                }
            }
        }

        // Right Camera Toolbar: Flip, Speed
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CameraToolButton(
                icon = Icons.Filled.FlipCameraAndroid,
                label = "Flip",
                onClick = { viewModel.toggleCamera() }
            )

            CameraToolButton(
                icon = Icons.Filled.Speed,
                label = speed,
                onClick = {
                    val nextSpeed = when (speed) {
                        "0.5x" -> "1x"
                        "1x" -> "2x"
                        "2x" -> "3x"
                        else -> "0.5x"
                    }
                    viewModel.setSpeed(nextSpeed)
                }
            )
        }

        // Bottom Controls: Duration Selector, Record Button, Gallery, Next
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Duration selector pills (15s, 30s, 60s, 3m)
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                RecordDuration.values().forEach { dur ->
                    val isSelected = dur == selectedDuration
                    Text(
                        text = dur.label,
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f),
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier
                            .clickable { viewModel.selectDuration(dur) }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            // Action Row: Gallery, Big Record Button, Next Checkmark
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Upload from gallery icon
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        viewModel.stopRecording()
                        onNavigateToEditor()
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PhotoLibrary,
                            contentDescription = "Upload",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        text = "Upload",
                        color = Color.White,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Center Record Button
                val recordPulseScale = remember { Animatable(1f) }
                LaunchedEffect(isRecording) {
                    if (isRecording) {
                        recordPulseScale.animateTo(
                            targetValue = 1.15f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(500, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            )
                        )
                    } else {
                        recordPulseScale.snapTo(1f)
                    }
                }

                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .scale(recordPulseScale.value)
                        .testTag("button_record")
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    if (isRecording) {
                                        viewModel.stopRecording()
                                    } else {
                                        viewModel.startRecording()
                                    }
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Outer ring
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(
                                width = 4.dp,
                                color = if (isRecording) AvaSecondaryPink else Color.White,
                                shape = CircleShape
                            )
                    )

                    // Inner circle (transforms to rounded square when recording)
                    Box(
                        modifier = Modifier
                            .size(if (isRecording) 32.dp else 66.dp)
                            .clip(if (isRecording) RoundedCornerShape(8.dp) else CircleShape)
                            .background(AvaPrimaryPink)
                    )
                }

                // Next Step button (appears when progress > 0 or user recorded)
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (recordProgress > 0f) AvaPrimaryPink else Color.Transparent)
                        .clickable(enabled = recordProgress > 0f) {
                            viewModel.stopRecording()
                            onNavigateToEditor()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (recordProgress > 0f) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Next",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CameraToolButton(
    imageVector: androidx.compose.ui.graphics.vector.ImageVector = Icons.Filled.Speed,
    icon: androidx.compose.ui.graphics.vector.ImageVector = imageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
        Text(
            text = label,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
