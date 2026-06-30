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
    primary = AccentElectricBlueDark,
    onPrimary = ObsidianBackground,
    primaryContainer = AccentElectricBlueDark.copy(alpha = 0.2f),
    onPrimaryContainer = AccentElectricBlueDark,
    secondary = AccentEmeraldDark,
    onSecondary = ObsidianBackground,
    secondaryContainer = AccentEmeraldDark.copy(alpha = 0.2f),
    onSecondaryContainer = AccentEmeraldDark,
    tertiary = AccentAmberDark,
    onTertiary = ObsidianBackground,
    tertiaryContainer = AccentAmberDark.copy(alpha = 0.2f),
    onTertiaryContainer = AccentAmberDark,
    surface = ObsidianSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = ObsidianSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    background = ObsidianBackground,
    onBackground = TextPrimaryDark,
    error = AccentRoseDark,
    errorContainer = AccentRoseDark.copy(alpha = 0.2f),
    onErrorContainer = AccentRoseDark,
    outline = ObsidianBorder
)

private val LightColorScheme = lightColorScheme(
    primary = AccentElectricBlueLight,
    onPrimary = ArcticSurface,
    primaryContainer = AccentElectricBlueLight.copy(alpha = 0.15f),
    onPrimaryContainer = AccentElectricBlueLight,
    secondary = AccentEmeraldLight,
    onSecondary = ArcticSurface,
    secondaryContainer = AccentEmeraldLight.copy(alpha = 0.15f),
    onSecondaryContainer = AccentEmeraldLight,
    tertiary = AccentAmberLight,
    onTertiary = ArcticSurface,
    tertiaryContainer = AccentAmberLight.copy(alpha = 0.15f),
    onTertiaryContainer = AccentAmberLight,
    surface = ArcticSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = ArcticSurfaceVariant,
    onSurfaceVariant = TextSecondaryLight,
    background = ArcticBackground,
    onBackground = TextPrimaryLight,
    error = AccentRoseLight,
    errorContainer = AccentRoseLight.copy(alpha = 0.15f),
    onErrorContainer = AccentRoseLight,
    outline = ArcticBorder
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
