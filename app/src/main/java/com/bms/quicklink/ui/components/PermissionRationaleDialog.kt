package com.bms.quicklink.ui.components

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
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        icon = {
            Icon(imageVector = Icons.Default.Info, contentDescription = "Permission Info", tint = MaterialTheme.colorScheme.primary)
        },
        title = {
            Text(text = "Bluetooth Permissions Required", style = MaterialTheme.typography.headlineMedium)
        },
        text = {
            Text(
                text = if (isPermanentDenial) {
                    "Bluetooth Low Energy permissions were permanently denied. Please enable them in App Settings to scan for and connect to your BMS."
                } else {
                    "BMS Quick Link & Control requires Bluetooth Low Energy permissions to discover and establish a stable connection with your battery management system."
                },
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
        },
        confirmButton = {
            if (isPermanentDenial) {
                Button(
                    onClick = onOpenSettings,
                    shape = MaterialTheme.shapes.medium,
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(text = "Open Settings", style = MaterialTheme.typography.labelLarge)
                }
            } else {
                Button(
                    onClick = onRetry,
                    shape = MaterialTheme.shapes.medium,
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(text = "Retry", style = MaterialTheme.typography.labelLarge)
                }
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = MaterialTheme.shapes.medium,
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(text = "Cancel", style = MaterialTheme.typography.labelLarge)
            }
        }
    )
}
