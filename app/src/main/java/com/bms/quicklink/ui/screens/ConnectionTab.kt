package com.bms.quicklink.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bms.quicklink.ble.BleFsmState
import com.bms.quicklink.ui.BmsViewModel
import com.bms.quicklink.ui.components.ConnectionHeader
import com.bms.quicklink.ui.components.DeviceListCard
import com.bms.quicklink.ui.theme.LocalCardStyle
import com.bms.quicklink.ui.theme.LocalCornerStyle

@Composable
fun ConnectionTab(
    viewModel: BmsViewModel,
    hasPermissions: Boolean,
    onRequestPermissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fsmState by viewModel.fsmState.collectAsState()
    val scannedDevices by viewModel.scannedDevices.collectAsState()
    val terminalLogs by viewModel.terminalLogs.collectAsState()
    val isSimulationMode by viewModel.isSimulationMode.collectAsState()

    val cardStyle = LocalCardStyle.current
    val cornerStyle = LocalCornerStyle.current
    val cardRadius = when (cornerStyle) {
        "SHARP" -> 8.dp
        "SOFT" -> 28.dp
        else -> 20.dp
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // App Title & Simulation Mode Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "BMS Quick Link",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            if (isSimulationMode) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "DEMO MODE",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Connection Header Card
        ConnectionHeader(fsmState = fsmState)

        // Primary Action Button Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (fsmState) {
                is BleFsmState.Disconnected -> {
                    Button(
                        onClick = {
                            if (hasPermissions || isSimulationMode) {
                                viewModel.onScanTapped()
                            } else {
                                onRequestPermissions()
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = if (isSimulationMode) "Scan Virtual BMS" else "Start Scan", style = MaterialTheme.typography.titleMedium)
                    }
                }
                is BleFsmState.Scanning -> {
                    Button(
                        onClick = { viewModel.onStopScanTapped() },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Stop Scan", style = MaterialTheme.typography.titleMedium)
                    }
                }
                is BleFsmState.Connecting -> {
                    Button(
                        onClick = { viewModel.onDisconnectTapped() },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.5.dp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Cancel Connection", style = MaterialTheme.typography.titleMedium)
                    }
                }
                is BleFsmState.Connected -> {
                    Button(
                        onClick = { viewModel.onDisconnectTapped() },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Disconnect", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }

        // Scanned Devices List (Hidden when connected)
        AnimatedVisibility(
            visible = fsmState is BleFsmState.Scanning || (fsmState is BleFsmState.Disconnected && scannedDevices.isNotEmpty()),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            DeviceListCard(
                devices = scannedDevices,
                onConnectTapped = { device ->
                    if (hasPermissions || isSimulationMode) {
                        viewModel.onConnectTapped(device)
                    } else {
                        onRequestPermissions()
                    }
                }
            )
        }

        Divider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(vertical = 2.dp))

        // Spectacular Real Terminal Console Card
        val terminalBorder = if (cardStyle == "FILLED") Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)

        Card(
            shape = RoundedCornerShape(cardRadius),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0C0C0E)), // Real deep hacker/developer terminal background
            modifier = Modifier
                .fillMaxWidth()
                .border(if (cardStyle == "FILLED") 0.dp else 1.dp, terminalBorder, RoundedCornerShape(cardRadius))
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Classic Window Title Bar (Terminal Header)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF252525))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Classic macOS/Ubuntu style colored window control dots
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(Color(0xFFFF5F56))) // Red
                        Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(Color(0xFFFFBD2E))) // Yellow
                        Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(Color(0xFF27C93F))) // Green
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "bash - bms-quicklink-core ~ 80x24",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFFB0B0B0),
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Merged Clear Logs Button
                    if (terminalLogs.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.onClearTerminalLogsTapped() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Clear Logs",
                                tint = Color(0xFFFF5F56),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Terminal Body with Syntax Color Coding
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                        .height(240.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    terminalLogs.forEach { logLine ->
                        val lineColor = when {
                            logLine.contains("[ERROR]") || logLine.contains("failed") -> Color(0xFFFF5555) // Bright Red
                            logLine.contains("[SUCCESS]") || logLine.contains("established") || logLine.contains("ready") -> Color(0xFF50FA7B) // Bright Green
                            logLine.contains("[WARN]") || logLine.contains("timeout") -> Color(0xFFF1FA8C) // Yellow
                            else -> Color(0xFFF8F8F2) // Crisp Terminal White
                        }

                        Text(
                            text = logLine,
                            style = MaterialTheme.typography.labelMedium,
                            color = lineColor,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(110.dp))
    }
}
