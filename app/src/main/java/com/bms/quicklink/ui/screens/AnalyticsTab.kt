package com.bms.quicklink.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bms.quicklink.ble.BleFsmState
import com.bms.quicklink.data.CellTelemetry
import com.bms.quicklink.ui.BmsViewModel
import com.bms.quicklink.ui.theme.LocalCardStyle
import com.bms.quicklink.ui.theme.LocalCornerStyle

@Composable
fun AnalyticsTab(
    viewModel: BmsViewModel,
    modifier: Modifier = Modifier
) {
    val fsmState by viewModel.fsmState.collectAsState()
    val cells by viewModel.cells.collectAsState()
    val maxCellVoltage by viewModel.maxCellVoltage.collectAsState()
    val minCellVoltage by viewModel.minCellVoltage.collectAsState()
    val deltaVoltage by viewModel.deltaVoltage.collectAsState()
    val mosfetTemp by viewModel.mosfetTemp.collectAsState()
    val ambientTemp by viewModel.ambientTemp.collectAsState()
    val cycleCount by viewModel.cycleCount.collectAsState()
    val batteryHealth by viewModel.batteryHealth.collectAsState()

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
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        Text(
            text = "Cell Analytics & Health",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Delta Highlights Banner
        Card(
            shape = RoundedCornerShape(cardRadius),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            modifier = Modifier
                .fillMaxWidth()
                .border(if (cardStyle == "FILLED") 0.dp else 1.dp, cardBorder, RoundedCornerShape(cardRadius))
        ) {
            Row(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CompactStat("MAX CELL", if (isConnected) String.format("%.3f V", maxCellVoltage) else "-- V", MaterialTheme.colorScheme.primary)
                CompactStat("MIN CELL", if (isConnected) String.format("%.3f V", minCellVoltage) else "-- V", Color(0xFFFF9800))
                CompactStat("DELTA GAP", if (isConnected) String.format("%.3f V", deltaVoltage) else "-- V", if (deltaVoltage > 0.1) Color(0xFFC62828) else Color(0xFF2E7D32))
            }
        }

        // Individual Cell Voltages Grid
        Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            Text(text = "INDIVIDUAL CELL VOLTAGES", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            
            val activeCells = if (isConnected && cells.isNotEmpty()) cells else (1..16).map { CellTelemetry(it, 0.0) }

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.heightIn(max = 400.dp)
            ) {
                items(activeCells, key = { it.cellNumber }) { cell ->
                    val isMax = isConnected && cell.voltage == maxCellVoltage && cell.voltage > 0
                    val isMin = isConnected && cell.voltage == minCellVoltage && cell.voltage > 0
                    
                    val borderCol = when {
                        isMax -> MaterialTheme.colorScheme.primary
                        isMin -> Color(0xFFFF9800)
                        else -> MaterialTheme.colorScheme.outline
                    }

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(if (isMax || isMin) 2.dp else 1.dp, borderCol, RoundedCornerShape(12.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "CELL ${cell.cellNumber}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isConnected) String.format("%.3f", cell.voltage) else "-.---",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        Divider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(vertical = 4.dp))

        // Diagnostic Sensors Row
        Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            Text(text = "HARDWARE HEALTH & TEMPERATURES", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    SensorBox("MOSFET TEMP", if (isConnected) String.format("%.1f °C", mosfetTemp) else "-- °C", Icons.Default.Thermostat, MaterialTheme.colorScheme.primary, cardBg, cardBorder, cardStyle, cardRadius)
                }
                Box(modifier = Modifier.weight(1f)) {
                    SensorBox("AMBIENT TEMP", if (isConnected) String.format("%.1f °C", ambientTemp) else "-- °C", Icons.Default.AcUnit, MaterialTheme.colorScheme.primary, cardBg, cardBorder, cardStyle, cardRadius)
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    SensorBox("CYCLE COUNT", if (isConnected) "$cycleCount Cycles" else "-- Cycles", Icons.Default.BatteryChargingFull, MaterialTheme.colorScheme.primary, cardBg, cardBorder, cardStyle, cardRadius)
                }
                Box(modifier = Modifier.weight(1f)) {
                    SensorBox("BATTERY HEALTH", if (isConnected) "$batteryHealth%" else "--%", Icons.Default.HealthAndSafety, Color(0xFF2E7D32), cardBg, cardBorder, cardStyle, cardRadius)
                }
            }
        }

        Spacer(modifier = Modifier.height(110.dp))
    }
}

@Composable
private fun CompactStat(title: String, valStr: String, col: Color) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(text = title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = valStr, style = MaterialTheme.typography.titleLarge, color = col)
    }
}

@Composable
private fun SensorBox(title: String, valStr: String, icon: androidx.compose.ui.graphics.vector.ImageVector, iconTint: Color, cardBg: Color, cardBorder: Color, cardStyle: String, cardRadius: androidx.compose.ui.unit.Dp) {
    Card(
        shape = RoundedCornerShape(cardRadius),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        modifier = Modifier
            .fillMaxWidth()
            .border(if (cardStyle == "FILLED") 0.dp else 1.dp, cardBorder, RoundedCornerShape(cardRadius))
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = title, tint = iconTint, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = valStr, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}
