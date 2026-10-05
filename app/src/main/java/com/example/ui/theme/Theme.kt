package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CinematicDarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = TextOnCyan,
    primaryContainer = DarkCardSurface,
    onPrimaryContainer = NeonCyan,
    secondary = CyanAccent,
    onSecondary = TextOnCyan,
    secondaryContainer = DarkCardBorder,
    onSecondaryContainer = CyanAccent,
    tertiary = ConfidenceGreen,
    onTertiary = TextOnCyan,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkCardSurface,
    onSurfaceVariant = TextSecondary,
    outline = DarkCardBorder,
    outlineVariant = DarkSubtleBorder,
    error = WarningRed
)

@Composable
fun PhonoSubTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = CinematicDarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = DarkBackground.toArgb()
                window.navigationBarColor = DarkBackground.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Alias for template compatibility
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    PhonoSubTheme(content = content)
}
