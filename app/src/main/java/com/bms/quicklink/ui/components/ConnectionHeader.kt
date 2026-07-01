package com.bms.quicklink.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bms.quicklink.ble.BleFsmState
import com.bms.quicklink.ui.theme.LocalCardStyle
import com.bms.quicklink.ui.theme.LocalCornerStyle

@Composable
fun ConnectionHeader(
    fsmState: BleFsmState,
    modifier: Modifier = Modifier
) {
    val cardStyle = LocalCardStyle.current
    val cornerStyle = LocalCornerStyle.current
    val cardRadius = when (cornerStyle) {
        "SHARP" -> 4.dp
        "SOFT" -> 20.dp
        else -> 12.dp
    }

    val (statusText, deviceName, rssi, baseBgColor, contentColor, icon, badgeColor, badgeTextColor) = when (fsmState) {
        is BleFsmState.Disconnected -> {
            HeaderData(
                statusText = "Disconnected",
                deviceName = "No Active BMS",
                rssi = null,
                baseBgColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                icon = Icons.Default.Bluetooth,
                badgeColor = MaterialTheme.colorScheme.surfaceVariant,
                badgeTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        is BleFsmState.Scanning -> {
            HeaderData(
                statusText = "Scanning",
                deviceName = "Searching for BMS...",
                rssi = null,
                baseBgColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                icon = Icons.Default.BluetoothSearching,
                badgeColor = MaterialTheme.colorScheme.secondary,
                badgeTextColor = MaterialTheme.colorScheme.onSecondary
            )
        }
        is BleFsmState.Connecting -> {
            HeaderData(
                statusText = "Connecting",
                deviceName = fsmState.device.name,
                rssi = fsmState.device.rssi,
                baseBgColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                icon = Icons.Default.Bluetooth,
                badgeColor = MaterialTheme.colorScheme.tertiary,
                badgeTextColor = MaterialTheme.colorScheme.onTertiary
            )
        }
        is BleFsmState.Connected -> {
            HeaderData(
                statusText = "Connected",
                deviceName = fsmState.device.name,
                rssi = fsmState.device.rssi,
                baseBgColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                icon = Icons.Default.BluetoothConnected,
                badgeColor = MaterialTheme.colorScheme.primary,
                badgeTextColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    }

    val cardBg = when (cardStyle) {
        "GLASS" -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        "OUTLINED" -> Color.Transparent
        else -> baseBgColor
    }

    val cardBorder = when (cardStyle) {
        "FILLED" -> Color.Transparent
        else -> MaterialTheme.colorScheme.outline
    }

    Card(
        shape = RoundedCornerShape(cardRadius),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        modifier = modifier
            .fillMaxWidth()
            .border(if (cardStyle == "FILLED") 0.dp else 1.dp, cardBorder, RoundedCornerShape(cardRadius))
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Row: Status Icon & Status Badge Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Status Icon",
                    tint = if (fsmState is BleFsmState.Disconnected) badgeTextColor else badgeColor,
                    modifier = Modifier.size(24.dp)
                )
                
                // Status Badge Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeColor)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelLarge,
                        color = badgeTextColor
                    )
                }
            }

            // Bottom Row: Device Title & System Overview
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = deviceName,
                    style = MaterialTheme.typography.headlineMedium,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (fsmState is BleFsmState.Connected) fsmState.device.address else "System Overview",
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (rssi != null) {
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), modifier = Modifier.fillMaxWidth())
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Signal Strength (RSSI)", style = MaterialTheme.typography.bodyMedium, color = contentColor.copy(alpha = 0.7f))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("$rssi dBm", style = MaterialTheme.typography.labelLarge, color = badgeColor)
                    }
                    
                    // RSSI Signal Meter Bars
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier.height(20.dp)
                    ) {
                        val activeBars = when {
                            rssi > -60 -> 4
                            rssi > -70 -> 3
                            rssi > -85 -> 2
                            else -> 1
                        }
                        
                        for (i in 1..4) {
                            val barHeight = (i * 5).dp
                            val isActive = i <= activeBars
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(barHeight)
                                    .clip(CircleShape)
                                    .background(if (isActive) badgeColor else MaterialTheme.colorScheme.surfaceVariant)
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class HeaderData(
    val statusText: String,
    val deviceName: String,
    val rssi: Int?,
    val baseBgColor: Color,
    val contentColor: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val badgeColor: Color,
    val badgeTextColor: Color
)
