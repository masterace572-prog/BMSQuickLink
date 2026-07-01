package com.bms.quicklink.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bms.quicklink.ui.BmsViewModel
import com.bms.quicklink.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceScreen(
    viewModel: BmsViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val accentColor by viewModel.accentColor.collectAsState()
    val cardStyle by viewModel.cardStyle.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Appearance Customization", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // 1. Theme Mode Toggle Pill Bar
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(text = "Theme Mode", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
                Text(text = "Adjust the active background and surface appearance palette.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val modes = listOf("DARK" to "Solid Dark", "LIGHT" to "Solid Light", "SYSTEM" to "System")
                    modes.forEach { (modeKey, modeLabel) ->
                        val isSelected = themeMode == modeKey
                        val bgColor by animateColorAsState(targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                        val textColor by animateColorAsState(targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(20.dp))
                                .background(bgColor)
                                .clickable { viewModel.onThemeModeSelected(modeKey) }
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = modeLabel, style = MaterialTheme.typography.titleMedium, color = textColor)
                        }
                    }
                }
            }

            Divider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(vertical = 4.dp))

            // 2. Dynamic Accent Color Swatch Palette
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(text = "Accent Palette", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
                Text(text = "Customize the solid accent color for active switches, buttons, and navigation indicators.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val swatches = listOf(
                        "BLUE" to ClassicBlueDark,
                        "EMERALD" to ClassicGreenDark,
                        "ORANGE" to ClassicOrangeDark,
                        "ROSE" to ClassicCrimsonDark,
                        "CYAN" to ClassicTealDark,
                        "PURPLE" to ClassicPurpleDark
                    )

                    swatches.forEach { (colorKey, colorValue) ->
                        val isSelected = accentColor == colorKey
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(colorValue)
                                .border(
                                    width = if (isSelected) 3.dp else 0.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onBackground else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { viewModel.onAccentColorSelected(colorKey) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = "Selected", tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                }
            }

            Divider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(vertical = 4.dp))

            // 3. Card Style Selector Pill Bar
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(text = "Card Style", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
                Text(text = "Alter the architectural container style across the entire dashboard.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val styles = listOf("FILLED" to "Solid Clean", "OUTLINED" to "Outlined", "GLASS" to "Translucent")
                    styles.forEach { (styleKey, styleLabel) ->
                        val isSelected = cardStyle == styleKey
                        val bgColor by animateColorAsState(targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                        val textColor by animateColorAsState(targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(20.dp))
                                .background(bgColor)
                                .clickable { viewModel.onCardStyleSelected(styleKey) }
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = styleLabel, style = MaterialTheme.typography.titleMedium, color = textColor)
                        }
                    }
                }
            }
        }
    }
}
