package com.bms.quicklink.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bms.quicklink.ble.BleFsmState
import com.bms.quicklink.ui.BmsViewModel
import com.bms.quicklink.ui.components.ConnectionHeader
import com.bms.quicklink.ui.components.ControlPanel
import com.bms.quicklink.ui.components.DeviceListCard

@Composable
fun ControlsTab(
    viewModel: BmsViewModel,
    hasPermissions: Boolean,
    onRequestPermissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fsmState by viewModel.fsmState.collectAsState()
    val scannedDevices by viewModel.scannedDevices.collectAsState()
    val switchState by viewModel.switchState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "BMS Control Center",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Connection Header
        ConnectionHeader(fsmState = fsmState)

        // Scan / Connect Action Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (fsmState) {
                is BleFsmState.Disconnected -> {
                    Button(
                        onClick = {
                            if (hasPermissions) {
                                viewModel.onScanTapped()
                            } else {
                                onRequestPermissions()
                            }
                        },
                        shape = MaterialTheme.shapes.large,
                        contentPadding = PaddingValues(horizontal = 28.dp, vertical = 16.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Start Scan", style = MaterialTheme.typography.labelLarge)
                    }
                }
                is BleFsmState.Scanning -> {
                    Button(
                        onClick = { viewModel.onStopScanTapped() },
                        shape = MaterialTheme.shapes.large,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        contentPadding = PaddingValues(horizontal = 28.dp, vertical = 16.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Stop Scan", style = MaterialTheme.typography.labelLarge)
                    }
                }
                is BleFsmState.Connecting -> {
                    Button(
                        onClick = { viewModel.onDisconnectTapped() },
                        shape = MaterialTheme.shapes.large,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        contentPadding = PaddingValues(horizontal = 28.dp, vertical = 16.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Cancel Connection", style = MaterialTheme.typography.labelLarge)
                    }
                }
                is BleFsmState.Connected -> {
                    Button(
                        onClick = { viewModel.onDisconnectTapped() },
                        shape = MaterialTheme.shapes.large,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        contentPadding = PaddingValues(horizontal = 28.dp, vertical = 16.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Disconnect", style = MaterialTheme.typography.labelLarge)
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
                    if (hasPermissions) {
                        viewModel.onConnectTapped(device)
                        // Automatically save profile on connect attempt
                        viewModel.addSavedDevice(device.name, device.address)
                    } else {
                        onRequestPermissions()
                    }
                }
            )
        }

        Divider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(vertical = 4.dp))

        // Control Panel
        ControlPanel(
            isConnected = fsmState is BleFsmState.Connected,
            switchState = switchState,
            onSwitchToggled = viewModel::onSwitchToggled
        )
    }
}
