package com.bms.quicklink.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import com.bms.quicklink.ble.BleFsmState

@Composable
fun ConnectionHeader(
    fsmState: BleFsmState,
    modifier: Modifier = Modifier
) {
    val (statusText, deviceName, rssi, backgroundColor, contentColor, icon, badgeColor, badgeTextColor) = when (fsmState) {
        is BleFsmState.Disconnected -> {
            HeaderData(
                statusText = "Disconnected",
                deviceName = "No device connected",
                rssi = null,
                backgroundColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                icon = Icons.Default.Bluetooth,
                badgeColor = MaterialTheme.colorScheme.surfaceVariant,
                badgeTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        is BleFsmState.Scanning -> {
            HeaderData(
                statusText = "Scanning...",
                deviceName = "Searching for compatible BMS...",
                rssi = null,
                backgroundColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                icon = Icons.Default.BluetoothSearching,
                badgeColor = MaterialTheme.colorScheme.secondary,
                badgeTextColor = MaterialTheme.colorScheme.onSecondary
            )
        }
        is BleFsmState.Connecting -> {
            HeaderData(
                statusText = "Connecting...",
                deviceName = fsmState.device.name,
                rssi = fsmState.device.rssi,
                backgroundColor = MaterialTheme.colorScheme.surface,
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
                backgroundColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                icon = Icons.Default.BluetoothConnected,
                badgeColor = MaterialTheme.colorScheme.primary,
                badgeTextColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    }

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(28.dp))
    ) {
        Column(modifier = Modifier.padding(28.dp).fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(badgeColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = "Status Icon",
                            tint = if (fsmState is BleFsmState.Disconnected) badgeTextColor else badgeColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Column {
                        Text(
                            text = deviceName,
                            style = MaterialTheme.typography.titleLarge,
                            color = contentColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (fsmState is BleFsmState.Connected) fsmState.device.address else "Hardware Status",
                            style = MaterialTheme.typography.bodyMedium,
                            color = contentColor.copy(alpha = 0.6f)
                        )
                    }
                }
                
                // Status Badge Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(badgeColor)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelLarge,
                        color = badgeTextColor
                    )
                }
            }

            if (rssi != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Signal Strength (RSSI)", style = MaterialTheme.typography.bodyMedium, color = contentColor.copy(alpha = 0.7f))
                    Text("$rssi dBm", style = MaterialTheme.typography.labelLarge, color = badgeColor)
                }
            }
        }
    }
}

private data class HeaderData(
    val statusText: String,
    val deviceName: String,
    val rssi: Int?,
    val backgroundColor: Color,
    val contentColor: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val badgeColor: Color,
    val badgeTextColor: Color
)
