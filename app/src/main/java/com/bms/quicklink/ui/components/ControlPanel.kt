package com.bms.quicklink.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.bms.quicklink.data.SwitchState
import com.bms.quicklink.data.SwitchType

@Composable
fun ControlPanel(
    isConnected: Boolean,
    switchState: SwitchState,
    onSwitchToggled: (SwitchType, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Hardware Control Panel",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        // Switch 1: Charge MOSFET
        ControlCard(
            title = SwitchType.CHARGE.title,
            subtitle = "Enable battery charging path",
            icon = Icons.Default.BatteryChargingFull,
            isChecked = switchState.chargeOn,
            isPending = switchState.chargePending,
            isEnabled = isConnected,
            onCheckedChange = { onSwitchToggled(SwitchType.CHARGE, it) },
            activeColor = MaterialTheme.colorScheme.primaryContainer
        )

        // Switch 2: Discharge MOSFET
        ControlCard(
            title = SwitchType.DISCHARGE.title,
            subtitle = "Enable battery output power",
            icon = Icons.Default.Power,
            isChecked = switchState.dischargeOn,
            isPending = switchState.dischargePending,
            isEnabled = isConnected,
            onCheckedChange = { onSwitchToggled(SwitchType.DISCHARGE, it) },
            activeColor = MaterialTheme.colorScheme.primaryContainer
        )

        // Switch 3: Auto Balance
        ControlCard(
            title = SwitchType.BALANCE.title,
            subtitle = "Enable passive cell balancing",
            icon = Icons.Default.Balance,
            isChecked = switchState.balanceOn,
            isPending = switchState.balancePending,
            isEnabled = isConnected,
            onCheckedChange = { onSwitchToggled(SwitchType.BALANCE, it) },
            activeColor = MaterialTheme.colorScheme.secondaryContainer
        )

        // Switch 4: Heating
        ControlCard(
            title = SwitchType.HEATING.title,
            subtitle = if (switchState.heatingWritable) "Low temperature charging heater" else "Heating Indicator (Notify-only)",
            icon = Icons.Default.Thermostat,
            isChecked = switchState.heatingOn,
            isPending = switchState.heatingPending,
            isEnabled = isConnected && switchState.heatingWritable,
            onCheckedChange = { onSwitchToggled(SwitchType.HEATING, it) },
            activeColor = MaterialTheme.colorScheme.errorContainer
        )
    }
}

@Composable
private fun ControlCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isChecked: Boolean,
    isPending: Boolean,
    isEnabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    activeColor: Color
) {
    val cardBg = if (isChecked && isEnabled) activeColor else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (isEnabled) 1.0f else 0.4f)
    val contentColor = if (isChecked && isEnabled) MaterialTheme.colorScheme.contentColorFor(activeColor) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = if (isEnabled) 1.0f else 0.4f)

    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = contentColor,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = contentColor
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor.copy(alpha = 0.8f)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            if (isPending) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = contentColor
                )
            } else {
                Switch(
                    checked = isChecked,
                    onCheckedChange = onCheckedChange,
                    enabled = isEnabled && !isPending
                )
            }
        }
    }
}
