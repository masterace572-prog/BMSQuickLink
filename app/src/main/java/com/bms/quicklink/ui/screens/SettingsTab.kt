package com.bms.quicklink.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.bms.quicklink.db.AuditLogEntity
import com.bms.quicklink.ui.BmsViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SettingsTab(
    viewModel: BmsViewModel,
    modifier: Modifier = Modifier
) {
    val darkMode by viewModel.darkMode.collectAsState()
    val developerMode by viewModel.developerMode.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        Text(
            text = "Settings & Preferences",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Preferences Section
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Dark / Light Mode Toggle with Icon Support
            PreferenceCard(
                title = "Appearance",
                subtitle = if (darkMode) "Dark Mode active" else "Light Mode active",
                icon = if (darkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                action = {
                    Switch(
                        checked = darkMode,
                        onCheckedChange = viewModel::onDarkModeToggled
                    )
                }
            )

            // Developer Mode Toggle
            PreferenceCard(
                title = "Developer Mode",
                subtitle = if (developerMode) "BMS scan filters bypassed" else "Scan filtered to known BMS prefixes",
                icon = Icons.Default.DeveloperMode,
                action = {
                    Switch(
                        checked = developerMode,
                        onCheckedChange = viewModel::onDeveloperModeToggled
                    )
                }
            )
        }

        Divider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(vertical = 4.dp))

        // Audit Logs Section
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hardware Audit Logs",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (auditLogs.isNotEmpty()) {
                    TextButton(onClick = { viewModel.clearAuditLogs() }) {
                        Text(text = "Clear Logs", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            if (auditLogs.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No active hardware operations recorded yet.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(auditLogs, key = { it.id }) { log ->
                        AuditLogItem(log = log)
                    }
                }
            }
        }
    }
}

@Composable
private fun PreferenceCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    action: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            action()
        }
    }
}

@Composable
private fun AuditLogItem(log: AuditLogEntity) {
    val timeFormat = remember { SimpleDateFormat("HH:mm:ss - MMM dd", Locale.getDefault()) }
    val timeString = remember(log.timestamp) { timeFormat.format(Date(log.timestamp)) }
    val isSuccess = log.status == "SUCCESS"

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = log.actionType,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "BMS: ${log.deviceAddress}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = timeString,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSuccess) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer)
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = log.status,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSuccess) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}
