package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AvaDarkColorScheme = darkColorScheme(
    primary = AvaPrimaryPink,
    onPrimary = AvaTextPrimary,
    primaryContainer = AvaSurfaceSecondary,
    onPrimaryContainer = AvaSecondaryPink,
    secondary = AvaSecondaryPink,
    onSecondary = AvaTextPrimary,
    tertiary = AvaPrimaryPink,
    background = AvaBackground,
    onBackground = AvaTextPrimary,
    surface = AvaSurfacePrimary,
    onSurface = AvaTextPrimary,
    surfaceVariant = AvaSurfaceSecondary,
    onSurfaceVariant = AvaTextSecondary,
    outline = AvaDivider,
    outlineVariant = AvaBorder,
    error = AvaError,
    onError = AvaTextPrimary
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    AvaTheme(content = content)
}

@Composable
fun AvaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AvaDarkColorScheme,
        typography = Typography,
        content = content
    )
}
