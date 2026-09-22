package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AvaBackground
import com.example.ui.theme.AvaPrimaryPink
import com.example.ui.theme.AvaSecondaryPink
import com.example.ui.theme.AvaTextPrimary
import com.example.ui.theme.AvaTextSecondary
import kotlinx.coroutines.delay

@Composable
fun AvaGeometricMark(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    tint: Color = AvaPrimaryPink,
    glow: Boolean = false
) {
    Canvas(modifier = modifier.size(size)) {
        drawAvaSymbol(tint = tint, glow = glow)
    }
}

fun DrawScope.drawAvaSymbol(tint: Color, glow: Boolean = false) {
    val w = size.width
    val h = size.height

    if (glow) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(tint.copy(alpha = 0.4f), Color.Transparent),
                center = Offset(w / 2f, h / 2f),
                radius = w * 0.7f
            )
        )
    }

    // Outer geometric triangular chevron
    val pathLeft = Path().apply {
        moveTo(w * 0.5f, h * 0.12f)
        lineTo(w * 0.15f, h * 0.88f)
        lineTo(w * 0.32f, h * 0.88f)
        lineTo(w * 0.5f, h * 0.45f)
        lineTo(w * 0.68f, h * 0.88f)
        lineTo(w * 0.85f, h * 0.88f)
        close()
    }
    drawPath(pathLeft, color = tint, style = Fill)

    // Center sharp futuristic diamond accent
    val pathDiamond = Path().apply {
        moveTo(w * 0.5f, h * 0.58f)
        lineTo(w * 0.42f, h * 0.74f)
        lineTo(w * 0.5f, h * 0.78f)
        lineTo(w * 0.58f, h * 0.74f)
        close()
    }
    drawPath(pathDiamond, color = Color.White, style = Fill)
}

@Composable
fun AvaSplashScreen(
    onAnimationFinished: () -> Unit
) {
    val dotScale = remember { Animatable(0.1f) }
    val dotAlpha = remember { Animatable(0f) }
    val logoScale = remember { Animatable(0.8f) }
    val logoAlpha = remember { Animatable(0f) }
    val letterA1Alpha = remember { Animatable(0f) }
    val letterVAlpha = remember { Animatable(0f) }
    val letterA2Alpha = remember { Animatable(0f) }
    val pulseScale = remember { Animatable(1f) }
    val screenAlpha = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        // Step 1: Black screen briefly
        delay(150)

        // Step 2 & 3: Tiny pink dot appears at center and expands smoothly
        dotAlpha.animateTo(1f, tween(250, easing = LinearEasing))
        dotScale.animateTo(1.5f, tween(350, easing = FastOutSlowInEasing))

        // Step 4: Dot transforms into minimal AVA symbol
        dotAlpha.animateTo(0f, tween(200))
        logoAlpha.animateTo(1f, tween(300, easing = FastOutSlowInEasing))
        logoScale.animateTo(1f, tween(300, easing = FastOutSlowInEasing))

        // Step 5: "AVA" appears using letter-by-letter animation
        delay(100)
        letterA1Alpha.animateTo(1f, tween(150))
        delay(80)
        letterVAlpha.animateTo(1f, tween(150))
        delay(80)
        letterA2Alpha.animateTo(1f, tween(150))

        // Step 6 & 7: Very subtle pink glow/pulse & logo slight scale up
        pulseScale.animateTo(
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        delay(350)

        // Step 8: Smooth fade/scale transition into home
        screenAlpha.animateTo(0f, tween(300, easing = FastOutSlowInEasing))
        onAnimationFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AvaBackground)
            .alpha(screenAlpha.value),
        contentAlignment = Alignment.Center
    ) {
        // Dot element during opening
        if (dotAlpha.value > 0f) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .scale(dotScale.value)
                    .alpha(dotAlpha.value)
                    .background(AvaPrimaryPink, shape = androidx.compose.foundation.shape.CircleShape)
            )
        }

        // Transformed AVA symbol and letters
        if (logoAlpha.value > 0f) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .scale(logoScale.value * pulseScale.value)
                    .alpha(logoAlpha.value)
            ) {
                AvaGeometricMark(
                    size = 72.dp,
                    tint = AvaPrimaryPink,
                    glow = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "A",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AvaTextPrimary,
                        letterSpacing = 6.sp,
                        modifier = Modifier.alpha(letterA1Alpha.value)
                    )
                    Text(
                        text = "V",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AvaPrimaryPink,
                        letterSpacing = 6.sp,
                        modifier = Modifier.alpha(letterVAlpha.value)
                    )
                    Text(
                        text = "A",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AvaTextPrimary,
                        letterSpacing = 6.sp,
                        modifier = Modifier.alpha(letterA2Alpha.value)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Watch. Create. Vibe.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = AvaTextSecondary,
                    letterSpacing = 2.sp,
                    modifier = Modifier.alpha(letterA2Alpha.value)
                )
            }
        }
    }
}
