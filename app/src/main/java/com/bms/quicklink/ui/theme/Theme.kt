package com.bms.quicklink.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = AccentPrimaryDark,
    onPrimary = SlateBackgroundDark,
    primaryContainer = AccentPrimaryDark.copy(alpha = 0.2f),
    onPrimaryContainer = AccentPrimaryDark,
    secondary = AccentPrimaryDark,
    onSecondary = SlateBackgroundDark,
    secondaryContainer = SlateSurfaceVariantDark,
    onSecondaryContainer = TextPrimaryDark,
    surface = SlateSurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SlateSurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
    background = SlateBackgroundDark,
    onBackground = TextPrimaryDark,
    error = ErrorRed,
    errorContainer = ErrorRed.copy(alpha = 0.2f),
    onErrorContainer = TextPrimaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = AccentPrimaryLight,
    onPrimary = PureSurfaceLight,
    primaryContainer = AccentPrimaryLight.copy(alpha = 0.15f),
    onPrimaryContainer = AccentPrimaryLight,
    secondary = AccentPrimaryLight,
    onSecondary = PureSurfaceLight,
    secondaryContainer = PureSurfaceVariantLight,
    onSecondaryContainer = TextPrimaryLight,
    surface = PureSurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = PureSurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,
    background = PureBackgroundLight,
    onBackground = TextPrimaryLight,
    error = ErrorRed,
    errorContainer = ErrorRed.copy(alpha = 0.15f),
    onErrorContainer = TextPrimaryLight
)

@Composable
fun BMSQuickLinkTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
