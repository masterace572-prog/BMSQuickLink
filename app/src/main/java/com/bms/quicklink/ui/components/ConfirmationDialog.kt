package com.bms.quicklink.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
        icon = {
            Icon(imageVector = Icons.Default.Warning, contentDescription = "Warning Icon")
        },
        title = {
            Text(text = "Confirm Hardware Change")
        },
        text = {
            Text(text = bodyText)
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(text = "Confirm")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        }
    )
}
