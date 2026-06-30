package com.bms.quicklink.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bms.quicklink.ble.BleFsmState
import com.bms.quicklink.ui.components.ConfirmationDialog
import com.bms.quicklink.ui.components.ConnectionHeader
import com.bms.quicklink.ui.components.ControlPanel
import com.bms.quicklink.ui.components.DeviceListCard
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BmsScreen(
    viewModel: BmsViewModel,
    hasPermissions: Boolean,
    onRequestPermissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fsmState by viewModel.fsmState.collectAsState()
    val scannedDevices by viewModel.scannedDevices.collectAsState()
    val switchState by viewModel.switchState.collectAsState()
    val developerMode by viewModel.developerMode.collectAsState()
    val confirmationDialogState by viewModel.confirmationDialogState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.errorEvents) {
        viewModel.errorEvents.collectLatest { errorMessage ->
            snackbarHostState.showSnackbar(
                message = errorMessage,
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BMS Quick Link & Control") },
                actions = {
                    IconButton(
                        onClick = { viewModel.onDeveloperModeToggled(!developerMode) },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = if (developerMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Icon(imageVector = Icons.Default.DeveloperMode, contentDescription = "Toggle Developer Mode")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Connection Header
            ConnectionHeader(fsmState = fsmState)

            // Primary Scan / Disconnect Button Action Bar
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
                            enabled = true,
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Start Scan", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                    is BleFsmState.Scanning -> {
                        Button(
                            onClick = { viewModel.onStopScanTapped() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Stop, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Stop Scan", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                    is BleFsmState.Connecting -> {
                        Button(
                            onClick = { viewModel.onDisconnectTapped() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onTertiary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Cancel Connection", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                    is BleFsmState.Connected -> {
                        Button(
                            onClick = { viewModel.onDisconnectTapped() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Stop, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
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
                        } else {
                            onRequestPermissions()
                        }
                    }
                )
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Control Panel
            ControlPanel(
                isConnected = fsmState is BleFsmState.Connected,
                switchState = switchState,
                onSwitchToggled = viewModel::onSwitchToggled
            )
        }
    }

    // Confirmation Dialog
    confirmationDialogState?.let { dialogData ->
        ConfirmationDialog(
            switchType = dialogData.switchType,
            targetState = dialogData.targetState,
            onConfirm = viewModel::onDialogConfirmed,
            onDismiss = viewModel::onDialogDismissed
        )
    }
}
