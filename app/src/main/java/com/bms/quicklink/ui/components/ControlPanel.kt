package com.bms.quicklink.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Hardware Control Panel",
            style = MaterialTheme.typography.titleLarge,
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
            activeTint = MaterialTheme.colorScheme.primary
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
            activeTint = MaterialTheme.colorScheme.primary
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
            activeTint = MaterialTheme.colorScheme.secondary
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
            activeTint = MaterialTheme.colorScheme.tertiary
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
    activeTint: Color
) {
    val cardBg = MaterialTheme.colorScheme.surface
    val iconBg = if (isChecked && isEnabled) activeTint.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
    val iconColor = if (isChecked && isEnabled) activeTint else MaterialTheme.colorScheme.onSurfaceVariant
    val titleColor = if (isEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
    val subtitleColor = if (isEnabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
    val borderColor = if (isChecked && isEnabled) activeTint.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(24.dp))
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = titleColor
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = subtitleColor
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            if (isPending) {
                CircularProgressIndicator(
                    modifier = Modifier.size(26.dp),
                    strokeWidth = 2.5.dp,
                    color = activeTint
                )
            } else {
                Switch(
                    checked = isChecked,
                    onCheckedChange = onCheckedChange,
                    enabled = isEnabled && !isPending,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = activeTint,
                        checkedTrackColor = activeTint.copy(alpha = 0.2f)
                    )
                )
            }
        }
    }
}
