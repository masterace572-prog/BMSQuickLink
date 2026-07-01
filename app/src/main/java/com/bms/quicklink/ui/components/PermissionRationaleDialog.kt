package com.bms.quicklink.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PermissionRationaleDialog(
    isPermanentDenial: Boolean,
    onRetry: () -> Unit,
    onOpenSettings: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(32.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        icon = {
            Icon(imageVector = Icons.Default.Info, contentDescription = "Permission Info", tint = MaterialTheme.colorScheme.primary)
        },
        title = {
            Text(text = "Permissions Required", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
        },
        text = {
            Text(
                text = if (isPermanentDenial) {
                    "Bluetooth and Location permissions were permanently denied. Both are required by the Android BLE stack for finding and connecting to your BMS. Please enable them in App Settings."
                } else {
                    "BMS Quick Link & Control requires both Bluetooth and Location permissions for successfully finding, scanning, and establishing a stable connection with your battery management system."
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        },
        confirmButton = {
            if (isPermanentDenial) {
                Button(
                    onClick = onOpenSettings,
                    shape = MaterialTheme.shapes.large,
                    contentPadding = PaddingValues(horizontal = 28.dp, vertical = 14.dp)
                ) {
                    Text(text = "Open Settings", style = MaterialTheme.typography.titleMedium)
                }
            } else {
                Button(
                    onClick = onRetry,
                    shape = MaterialTheme.shapes.large,
                    contentPadding = PaddingValues(horizontal = 28.dp, vertical = 14.dp)
                ) {
                    Text(text = "Retry", style = MaterialTheme.typography.titleMedium)
                }
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = MaterialTheme.shapes.large,
                contentPadding = PaddingValues(horizontal = 28.dp, vertical = 14.dp)
            ) {
                Text(text = "Cancel", style = MaterialTheme.typography.titleMedium)
            }
        }
    )
}
