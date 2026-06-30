package com.bms.quicklink.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun PermissionRationaleDialog(
    isPermanentDenial: Boolean,
    onRetry: () -> Unit,
    onOpenSettings: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(imageVector = Icons.Default.Info, contentDescription = "Permission Info")
        },
        title = {
            Text(text = "Bluetooth Permissions Required")
        },
        text = {
            Text(
                text = if (isPermanentDenial) {
                    "Bluetooth Low Energy permissions were permanently denied. Please enable them in App Settings to scan for and connect to your BMS."
                } else {
                    "BMS Quick Link & Control requires Bluetooth Low Energy permissions to discover and establish a stable connection with your battery management system."
                }
            )
        },
        confirmButton = {
            if (isPermanentDenial) {
                Button(onClick = onOpenSettings) {
                    Text(text = "Open Settings")
                }
            } else {
                Button(onClick = onRetry) {
                    Text(text = "Retry")
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        }
    )
}
