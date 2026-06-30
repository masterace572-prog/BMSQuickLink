package com.bms.quicklink.ui.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bms.quicklink.ble.BleFsmState

@Composable
fun ConnectionHeader(
    fsmState: BleFsmState,
    modifier: Modifier = Modifier
) {
    val (statusText, deviceName, rssi, backgroundColor, contentColor, icon) = when (fsmState) {
        is BleFsmState.Disconnected -> {
            HeaderData(
                statusText = "Disconnected",
                deviceName = "No device connected",
                rssi = null,
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                icon = Icons.Default.Bluetooth
            )
        }
        is BleFsmState.Scanning -> {
            HeaderData(
                statusText = "Scanning...",
                deviceName = "Searching for compatible BMS...",
                rssi = null,
                backgroundColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                icon = Icons.Default.BluetoothSearching
            )
        }
        is BleFsmState.Connecting -> {
            HeaderData(
                statusText = "Connecting...",
                deviceName = fsmState.device.name,
                rssi = fsmState.device.rssi,
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                icon = Icons.Default.Bluetooth
            )
        }
        is BleFsmState.Connected -> {
            HeaderData(
                statusText = "Connected",
                deviceName = fsmState.device.name,
                rssi = fsmState.device.rssi,
                backgroundColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                icon = Icons.Default.BluetoothConnected
            )
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Bluetooth Status Icon",
                tint = contentColor,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.titleLarge,
                    color = contentColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = deviceName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor.copy(alpha = 0.8f)
                )
            }
            if (rssi != null) {
                Text(
                    text = "$rssi dBm",
                    style = MaterialTheme.typography.labelLarge,
                    color = contentColor
                )
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
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
