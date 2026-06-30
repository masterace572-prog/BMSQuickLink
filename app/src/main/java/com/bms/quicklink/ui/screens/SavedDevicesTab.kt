package com.bms.quicklink.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bms.quicklink.db.SavedDeviceEntity
import com.bms.quicklink.ui.BmsViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SavedDevicesTab(
    viewModel: BmsViewModel,
    hasPermissions: Boolean,
    onRequestPermissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    val savedDevices by viewModel.savedDevices.collectAsState()
    val scannedDevices by viewModel.scannedDevices.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Saved Profiles",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Button(
                onClick = { showAddDialog = true },
                shape = MaterialTheme.shapes.large,
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Device", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Add Profile", style = MaterialTheme.typography.labelLarge)
            }
        }

        if (savedDevices.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .border(2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(32.dp))
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Bluetooth, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(32.dp))
                    }
                    Text(
                        text = "No saved profiles yet",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Quickly link and connect to your frequently used BMS hardware by adding a profile.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Button(
                        onClick = { showAddDialog = true },
                        shape = MaterialTheme.shapes.medium,
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Text(text = "Create Profile", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(savedDevices, key = { it.address }) { entity ->
                    SavedDeviceCard(
                        entity = entity,
                        onConnect = {
                            if (hasPermissions) {
                                val matchedDevice = scannedDevices.find { it.address == entity.address }
                                if (matchedDevice != null) {
                                    viewModel.onConnectTapped(matchedDevice)
                                } else {
                                    // Start scan if not already found in active list
                                    viewModel.onScanTapped()
                                }
                            } else {
                                onRequestPermissions()
                            }
                        },
                        onDelete = { viewModel.deleteSavedDevice(entity.address) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddDeviceDialog(
            onDismiss = { showAddDialog = false },
            onSave = { nickname, address ->
                viewModel.addSavedDevice(nickname, address)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun SavedDeviceCard(
    entity: SavedDeviceEntity,
    onConnect: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    val dateString = remember(entity.dateAdded) { dateFormat.format(Date(entity.dateAdded)) }

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(28.dp))
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Bluetooth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entity.nickname,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = entity.address,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Added: $dateString",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = onConnect,
                shape = MaterialTheme.shapes.large,
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(text = "Connect", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            IconButton(
                onClick = onDelete,
                colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.size(44.dp)
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun AddDeviceDialog(
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var nickname by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(32.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text(text = "Add Saved BMS", style = MaterialTheme.typography.headlineMedium) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp), modifier = Modifier.padding(top = 16.dp)) {
                OutlinedTextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    label = { Text("Device Nickname") },
                    placeholder = { Text("My LiFePO4 Battery") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Bluetooth MAC Address") },
                    placeholder = { Text("00:11:22:33:44:55") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(nickname, address) },
                enabled = nickname.isNotBlank() && address.isNotBlank(),
                shape = MaterialTheme.shapes.large,
                contentPadding = PaddingValues(horizontal = 28.dp, vertical = 14.dp)
            ) {
                Text("Save Profile", style = MaterialTheme.typography.labelLarge)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = MaterialTheme.shapes.large,
                contentPadding = PaddingValues(horizontal = 28.dp, vertical = 14.dp)
            ) {
                Text("Cancel", style = MaterialTheme.typography.labelLarge)
            }
        }
    )
}
