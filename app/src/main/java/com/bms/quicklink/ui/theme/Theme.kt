package com.bms.quicklink.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val LocalCardStyle = compositionLocalOf { "FILLED" }

@Composable
fun BMSQuickLinkTheme(
    themeMode: String,
    accentColorName: String,
    cardStyle: String,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        "DARK" -> true
        "LIGHT" -> false
        else -> isSystemInDarkTheme()
    }

    val primaryAccent = when (accentColorName) {
        "BLUE" -> if (isDark) AccentBlueDark else AccentBlueLight
        "EMERALD" -> if (isDark) AccentEmeraldDark else AccentEmeraldLight
        "ORANGE" -> if (isDark) AccentOrangeDark else AccentOrangeLight
        "ROSE" -> if (isDark) AccentRoseDark else AccentRoseLight
        "CYAN" -> if (isDark) AccentCyanDark else AccentCyanLight
        "PURPLE" -> if (isDark) AccentPurpleDark else AccentPurpleLight
        else -> if (isDark) AccentBlueDark else AccentBlueLight
    }

    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = primaryAccent,
            onPrimary = ObsidianBackground,
            primaryContainer = primaryAccent.copy(alpha = 0.2f),
            onPrimaryContainer = primaryAccent,
            secondary = primaryAccent,
            onSecondary = ObsidianBackground,
            secondaryContainer = primaryAccent.copy(alpha = 0.2f),
            onSecondaryContainer = primaryAccent,
            tertiary = primaryAccent,
            onTertiary = ObsidianBackground,
            tertiaryContainer = primaryAccent.copy(alpha = 0.2f),
            onTertiaryContainer = primaryAccent,
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
    } else {
        lightColorScheme(
            primary = primaryAccent,
            onPrimary = ArcticSurface,
            primaryContainer = primaryAccent.copy(alpha = 0.15f),
            onPrimaryContainer = primaryAccent,
            secondary = primaryAccent,
            onSecondary = ArcticSurface,
            secondaryContainer = primaryAccent.copy(alpha = 0.15f),
            onSecondaryContainer = primaryAccent,
            tertiary = primaryAccent,
            onTertiary = ArcticSurface,
            tertiaryContainer = primaryAccent.copy(alpha = 0.15f),
            onTertiaryContainer = primaryAccent,
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
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
        }
    }

    CompositionLocalProvider(LocalCardStyle provides cardStyle) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
