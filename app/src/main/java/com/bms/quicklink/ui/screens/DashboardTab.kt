package com.bms.quicklink.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.OfflineBolt
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.bms.quicklink.ble.BleFsmState
import com.bms.quicklink.data.BmsOperatingState
import com.bms.quicklink.ui.BmsViewModel
import com.bms.quicklink.ui.theme.LocalCardStyle
import com.bms.quicklink.ui.theme.LocalCornerStyle

@Composable
fun DashboardTab(
    viewModel: BmsViewModel,
    modifier: Modifier = Modifier
) {
    val fsmState by viewModel.fsmState.collectAsState()
    val totalVoltage by viewModel.totalVoltage.collectAsState()
    val current by viewModel.current.collectAsState()
    val power by viewModel.power.collectAsState()
    val socPercentage by viewModel.socPercentage.collectAsState()
    val operatingState by viewModel.operatingState.collectAsState()

    val cardStyle = LocalCardStyle.current
    val cornerStyle = LocalCornerStyle.current
    val cardRadius = when (cornerStyle) {
        "SHARP" -> 8.dp
        "SOFT" -> 28.dp
        else -> 20.dp
    }

    val cardBg = when (cardStyle) {
        "GLASS" -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        "OUTLINED" -> Color.Transparent
        else -> MaterialTheme.colorScheme.surface
    }
    val cardBorder = if (cardStyle == "FILLED") Color.Transparent else MaterialTheme.colorScheme.outline

    val isConnected = fsmState is BleFsmState.Connected

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            Text(
                text = "Real-Time Dashboard",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Large Circular State of Charge (SOC) Progress Indicator
        Box(
            modifier = Modifier.size(260.dp),
            contentAlignment = Alignment.Center
        ) {
            val progressTarget = if (isConnected) socPercentage / 100f else 0f
            val animatedProgress by animateFloatAsState(targetValue = progressTarget, label = "soc_progress")
            val socColor = when {
                !isConnected -> MaterialTheme.colorScheme.surfaceVariant
                socPercentage > 50 -> Color(0xFF4CAF50)
                socPercentage > 20 -> Color(0xFFFF9800)
                else -> Color(0xFFF44336)
            }

            CircularProgressIndicator(
                progress = animatedProgress,
                strokeWidth = 16.dp,
                color = socColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                strokeCap = StrokeCap.Round,
                modifier = Modifier.size(240.dp)
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isConnected) "$socPercentage%" else "--%",
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 52.sp),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "STATE OF CHARGE",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Live Readout Row (V, A, W)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                TelemetryCard(
                    title = "VOLTAGE",
                    value = if (isConnected) String.format("%.2f V", totalVoltage) else "-- V",
                    icon = Icons.Default.Bolt,
                    iconTint = MaterialTheme.colorScheme.primary,
                    cardBg = cardBg,
                    cardBorder = cardBorder,
                    cardStyle = cardStyle,
                    cardRadius = cardRadius
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                TelemetryCard(
                    title = "CURRENT",
                    value = if (isConnected) String.format("%.2f A", current) else "-- A",
                    icon = Icons.Default.SwapVert,
                    iconTint = if (current < 0) Color(0xFFFF9800) else MaterialTheme.colorScheme.primary,
                    cardBg = cardBg,
                    cardBorder = cardBorder,
                    cardStyle = cardStyle,
                    cardRadius = cardRadius
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                TelemetryCard(
                    title = "POWER",
                    value = if (isConnected) String.format("%.1f W", power) else "-- W",
                    icon = Icons.Default.OfflineBolt,
                    iconTint = MaterialTheme.colorScheme.primary,
                    cardBg = cardBg,
                    cardBorder = cardBorder,
                    cardStyle = cardStyle,
                    cardRadius = cardRadius
                )
            }
        }

        // Hardware Operating State Banner
        val (stateLabel, stateIcon, stateBg) = when {
            !isConnected -> Triple("SYSTEM STANDBY", Icons.Default.CheckCircleOutline, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            operatingState == BmsOperatingState.CHARGING -> Triple("CHARGING ACTIVE", Icons.Default.BatteryChargingFull, Color(0xFF2E7D32))
            operatingState == BmsOperatingState.DISCHARGING -> Triple("DISCHARGING (LOAD ACTIVE)", Icons.Default.Power, Color(0xFFE65100))
            operatingState == BmsOperatingState.FAULT -> Triple("SYSTEM FAULT LOCK", Icons.Default.ErrorOutline, Color(0xFFC62828))
            else -> Triple("SYSTEM STANDBY", Icons.Default.CheckCircleOutline, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
        }

        Card(
            shape = RoundedCornerShape(cardRadius),
            colors = CardDefaults.cardColors(containerColor = stateBg),
            modifier = Modifier
                .fillMaxWidth()
                .border(if (cardStyle == "FILLED") 0.dp else 1.dp, cardBorder, RoundedCornerShape(cardRadius))
        ) {
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = stateIcon, contentDescription = stateLabel, tint = Color.White, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = stateLabel, style = MaterialTheme.typography.titleLarge, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(110.dp))
    }
}

@Composable
private fun TelemetryCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    cardBg: Color,
    cardBorder: Color,
    cardStyle: String,
    cardRadius: androidx.compose.ui.unit.Dp
) {
    Card(
        shape = RoundedCornerShape(cardRadius),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        modifier = Modifier
            .fillMaxWidth()
            .border(if (cardStyle == "FILLED") 0.dp else 1.dp, cardBorder, RoundedCornerShape(cardRadius))
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Icon(imageVector = icon, contentDescription = title, tint = iconTint, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = value, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
