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
        "BLUE" -> if (isDark) ClassicBlueDark else ClassicBlueLight
        "EMERALD" -> if (isDark) ClassicGreenDark else ClassicGreenLight
        "ORANGE" -> if (isDark) ClassicOrangeDark else ClassicOrangeLight
        "ROSE" -> if (isDark) ClassicCrimsonDark else ClassicCrimsonLight
        "CYAN" -> if (isDark) ClassicTealDark else ClassicTealLight
        "PURPLE" -> if (isDark) ClassicPurpleDark else ClassicPurpleLight
        else -> if (isDark) ClassicBlueDark else ClassicBlueLight
    }

    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = primaryAccent,
            onPrimary = SolidBackgroundDark,
            primaryContainer = primaryAccent.copy(alpha = 0.25f),
            onPrimaryContainer = primaryAccent,
            secondary = primaryAccent,
            onSecondary = SolidBackgroundDark,
            secondaryContainer = primaryAccent.copy(alpha = 0.25f),
            onSecondaryContainer = primaryAccent,
            tertiary = primaryAccent,
            onTertiary = SolidBackgroundDark,
            tertiaryContainer = primaryAccent.copy(alpha = 0.25f),
            onTertiaryContainer = primaryAccent,
            surface = SolidSurfaceDark,
            onSurface = TextPrimaryDark,
            surfaceVariant = SolidSurfaceVariantDark,
            onSurfaceVariant = TextSecondaryDark,
            background = SolidBackgroundDark,
            onBackground = TextPrimaryDark,
            error = ClassicCrimsonDark,
            errorContainer = ClassicCrimsonDark.copy(alpha = 0.25f),
            onErrorContainer = ClassicCrimsonDark,
            outline = SolidBorderDark
        )
    } else {
        lightColorScheme(
            primary = primaryAccent,
            onPrimary = SolidSurfaceLight,
            primaryContainer = primaryAccent.copy(alpha = 0.18f),
            onPrimaryContainer = primaryAccent,
            secondary = primaryAccent,
            onSecondary = SolidSurfaceLight,
            secondaryContainer = primaryAccent.copy(alpha = 0.18f),
            onSecondaryContainer = primaryAccent,
            tertiary = primaryAccent,
            onTertiary = SolidSurfaceLight,
            tertiaryContainer = primaryAccent.copy(alpha = 0.18f),
            onTertiaryContainer = primaryAccent,
            surface = SolidSurfaceLight,
            onSurface = TextPrimaryLight,
            surfaceVariant = SolidSurfaceVariantLight,
            onSurfaceVariant = TextSecondaryLight,
            background = SolidBackgroundLight,
            onBackground = TextPrimaryLight,
            error = ClassicCrimsonLight,
            errorContainer = ClassicCrimsonLight.copy(alpha = 0.18f),
            onErrorContainer = ClassicCrimsonLight,
            outline = SolidBorderLight
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
