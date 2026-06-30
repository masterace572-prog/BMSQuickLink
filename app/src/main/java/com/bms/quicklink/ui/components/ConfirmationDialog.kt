package com.bms.quicklink.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bms.quicklink.data.SwitchType

@Composable
fun ConfirmationDialog(
    switchType: SwitchType,
    targetState: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val actionText = if (targetState) "Enable" else "Disable"
    val bodyText = when (switchType) {
        SwitchType.CHARGE -> "$actionText Charging? This will physically ${if (targetState) "enable" else "disable"} the charging path MOSFET."
        SwitchType.DISCHARGE -> "$actionText Battery Output? This will physically ${if (targetState) "enable" else "disable"} the discharge power MOSFET."
        SwitchType.BALANCE -> "$actionText Auto Balance? This will ${if (targetState) "enable" else "disable"} passive cell balancing."
        SwitchType.HEATING -> "$actionText Heating? This will ${if (targetState) "activate" else "deactivate"} low temperature heating pads."
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        icon = {
            Icon(imageVector = Icons.Default.Warning, contentDescription = "Warning Icon", tint = MaterialTheme.colorScheme.tertiary)
        },
        title = {
            Text(text = "Confirm Hardware Change", style = MaterialTheme.typography.headlineMedium)
        },
        text = {
            Text(text = bodyText, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 8.dp))
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = MaterialTheme.shapes.medium,
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(text = "Confirm", style = MaterialTheme.typography.labelLarge)
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
