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
val LocalCornerStyle = compositionLocalOf { "CLASSIC" }

@Composable
fun BMSQuickLinkTheme(
    themeMode: String,
    accentColorName: String,
    cardStyle: String,
    cornerStyle: String,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        "DARK" -> true
        "LIGHT" -> false
        else -> isSystemInDarkTheme()
    }

    val primaryAccent = when (accentColorName) {
        "BLUE" -> if (isDark) CorporateBlueDark else CorporateBlueLight
        "EMERALD" -> if (isDark) CorporateGreenDark else CorporateGreenLight
        "ORANGE" -> if (isDark) CorporateOrangeDark else CorporateOrangeLight
        "ROSE" -> if (isDark) CorporateRedDark else CorporateRedLight
        "CYAN" -> if (isDark) CorporateTealDark else CorporateTealLight
        "PURPLE" -> if (isDark) CorporatePurpleDark else CorporatePurpleLight
        else -> if (isDark) CorporateBlueDark else CorporateBlueLight
    }

    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = primaryAccent,
            onPrimary = CorporateBackgroundDark,
            primaryContainer = CorporateSurfaceVariantDark,
            onPrimaryContainer = primaryAccent,
            secondary = primaryAccent,
            onSecondary = CorporateBackgroundDark,
            secondaryContainer = CorporateSurfaceVariantDark,
            onSecondaryContainer = primaryAccent,
            tertiary = primaryAccent,
            onTertiary = CorporateBackgroundDark,
            tertiaryContainer = CorporateSurfaceVariantDark,
            onTertiaryContainer = primaryAccent,
            surface = CorporateSurfaceDark,
            onSurface = TextPrimaryDark,
            surfaceVariant = CorporateSurfaceVariantDark,
            onSurfaceVariant = TextSecondaryDark,
            background = CorporateBackgroundDark,
            onBackground = TextPrimaryDark,
            error = CorporateRedDark,
            errorContainer = CorporateSurfaceVariantDark,
            onErrorContainer = CorporateRedDark,
            outline = CorporateBorderDark
        )
    } else {
        lightColorScheme(
            primary = primaryAccent,
            onPrimary = CorporateSurfaceLight,
            primaryContainer = CorporateSurfaceVariantLight,
            onPrimaryContainer = primaryAccent,
            secondary = primaryAccent,
            onSecondary = CorporateSurfaceLight,
            secondaryContainer = CorporateSurfaceVariantLight,
            onSecondaryContainer = primaryAccent,
            tertiary = primaryAccent,
            onTertiary = CorporateSurfaceLight,
            tertiaryContainer = CorporateSurfaceVariantLight,
            onTertiaryContainer = primaryAccent,
            surface = CorporateSurfaceLight,
            onSurface = TextPrimaryLight,
            surfaceVariant = CorporateSurfaceVariantLight,
            onSurfaceVariant = TextSecondaryLight,
            background = CorporateBackgroundLight,
            onBackground = TextPrimaryLight,
            error = CorporateRedLight,
            errorContainer = CorporateSurfaceVariantLight,
            onErrorContainer = CorporateRedLight,
            outline = CorporateBorderLight
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

    CompositionLocalProvider(
        LocalCardStyle provides cardStyle,
        LocalCornerStyle provides cornerStyle
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
