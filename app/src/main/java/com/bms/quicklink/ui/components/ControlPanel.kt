package com.bms.quicklink.ui.components

import androidx.compose.animation.animateContentSize
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bms.quicklink.data.SwitchState
import com.bms.quicklink.data.SwitchType
import com.bms.quicklink.ui.theme.LocalCardStyle

@Composable
fun ControlPanel(
    isConnected: Boolean,
    switchState: SwitchState,
    onSwitchToggled: (SwitchType, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Hardware Switch Panel",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 2.dp)
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
    val cardStyle = LocalCardStyle.current
    val cardBg = when (cardStyle) {
        "GLASS" -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        "OUTLINED" -> Color.Transparent
        else -> MaterialTheme.colorScheme.surface
    }

    val iconColor = if (isChecked && isEnabled) activeTint else MaterialTheme.colorScheme.onSurfaceVariant
    val titleColor = if (isEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
    val subtitleColor = if (isEnabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
    val borderColor = if (isChecked && isEnabled) activeTint.copy(alpha = 0.4f) else if (cardStyle == "FILLED") Color.Transparent else MaterialTheme.colorScheme.outline
    val accentBarColor = if (isChecked && isEnabled) activeTint else Color.Transparent

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        modifier = Modifier
            .fillMaxWidth()
            .border(if (cardStyle == "FILLED" && !isChecked) 0.dp else 1.dp, borderColor, RoundedCornerShape(12.dp))
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Accent Bar Indicator
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(accentBarColor)
            )

            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Clean icon placement, no neon boxes
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            color = titleColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (isEnabled && !isPending) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isChecked) activeTint.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isChecked) "ACTIVE" else "OFF",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isChecked) activeTint else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = subtitleColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                if (isPending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = activeTint
                    )
                } else {
                    Switch(
                        checked = isChecked,
                        onCheckedChange = onCheckedChange,
                        enabled = isEnabled && !isPending,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = activeTint,
                            checkedTrackColor = activeTint.copy(alpha = 0.2f),
                            uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }
        }
    }
}
