package com.bms.quicklink.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bms.quicklink.db.AuditLogEntity
import com.bms.quicklink.ui.BmsViewModel
import com.bms.quicklink.ui.theme.LocalCardStyle
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SettingsTab(
    viewModel: BmsViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val developerMode by viewModel.developerMode.collectAsState()
    val verifyTimeoutMs by viewModel.verifyTimeoutMs.collectAsState()
    val isSimulationMode by viewModel.isSimulationMode.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()

    val cardStyle = LocalCardStyle.current
    val cardBg = when (cardStyle) {
        "GLASS" -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        "OUTLINED" -> Color.Transparent
        else -> MaterialTheme.colorScheme.surface
    }
    val cardBorder = if (cardStyle == "FILLED") Color.Transparent else MaterialTheme.colorScheme.outline

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        Text(
            text = "Settings & Console",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Navigation Sections
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Appearance Nav Card
            PreferenceNavCard(
                title = "Appearance",
                subtitle = "Customize active themes, accent palette, and card styles",
                icon = Icons.Default.Palette,
                cardBg = cardBg,
                cardBorder = cardBorder,
                cardStyle = cardStyle,
                onClick = { navController.navigate("appearance") }
            )

            // Developer Profile Nav Card
            PreferenceNavCard(
                title = "Developer Profile",
                subtitle = "About Anoy (Arjun) & social connect handles",
                icon = Icons.Default.Person,
                cardBg = cardBg,
                cardBorder = cardBorder,
                cardStyle = cardStyle,
                onClick = { navController.navigate("developer") }
            )

            // Terms & Conditions Nav Card
            PreferenceNavCard(
                title = "Terms & Conditions",
                subtitle = "Read usage guidelines & hardware safety parameters",
                icon = Icons.Default.Gavel,
                cardBg = cardBg,
                cardBorder = cardBorder,
                cardStyle = cardStyle,
                onClick = { navController.navigate("terms") }
            )

            // Privacy Policy Nav Card
            PreferenceNavCard(
                title = "Privacy Policy",
                subtitle = "View absolute offline security & zero tracking mandate",
                icon = Icons.Default.PrivacyTip,
                cardBg = cardBg,
                cardBorder = cardBorder,
                cardStyle = cardStyle,
                onClick = { navController.navigate("privacy") }
            )
        }

        Divider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(vertical = 4.dp))

        // Hardware Configuration Section
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Text(
                text = "Hardware Configuration",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Verification Timeout Selector Pill Bar
            Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Text(text = "BLE Notify Verification Timeout", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val timeouts = listOf(1000L to "1.0s", 2000L to "2.0s", 3000L to "3.0s", 5000L to "5.0s")
                    timeouts.forEach { (timeoutKey, timeoutLabel) ->
                        val isSelected = verifyTimeoutMs == timeoutKey
                        val bgColor by animateColorAsState(targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                        val textColor by animateColorAsState(targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(20.dp))
                                .background(bgColor)
                                .clickable { viewModel.onVerifyTimeoutSelected(timeoutKey) }
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = timeoutLabel, style = MaterialTheme.typography.titleMedium, color = textColor)
                        }
                    }
                }
            }

            // Developer Mode Card
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(if (cardStyle == "FILLED") 0.dp else 1.dp, cardBorder, RoundedCornerShape(28.dp))
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
                            imageVector = Icons.Default.DeveloperMode,
                            contentDescription = "Developer Mode",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Developer Mode",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = if (developerMode) "BMS scan filters bypassed" else "Scan filtered to known BMS prefixes",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Switch(
                        checked = developerMode,
                        onCheckedChange = viewModel::onDeveloperModeToggled
                    )
                }
            }

            // Simulation Mode Card
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(if (cardStyle == "FILLED") 0.dp else 1.dp, cardBorder, RoundedCornerShape(28.dp))
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
                            .background(MaterialTheme.colorScheme.tertiaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.BugReport,
                            contentDescription = "Simulation Mode",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Simulation Mode",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = if (isSimulationMode) "Offline demo environment active" else "Connects to physical BLE hardware",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Switch(
                        checked = isSimulationMode,
                        onCheckedChange = viewModel::onSimulationModeToggled
                    )
                }
            }
        }

        Divider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(vertical = 4.dp))

        // Audit Logs Section
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
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
                        Text(text = "Clear Logs", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            if (auditLogs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(28.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No active hardware operations recorded yet.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(auditLogs, key = { it.id }) { log ->
                        AuditLogItem(log = log)
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(110.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun PreferenceNavCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    cardBg: Color,
    cardBorder: Color,
    cardStyle: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        modifier = Modifier
            .fillMaxWidth()
            .border(if (cardStyle == "FILLED") 0.dp else 1.dp, cardBorder, RoundedCornerShape(28.dp))
            .clickable(onClick = onClick)
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
                Icon(imageVector = icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(3.dp))
                Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Open", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(22.dp))
        }
    }
}

@Composable
private fun AuditLogItem(log: AuditLogEntity) {
    val timeFormat = remember { SimpleDateFormat("HH:mm:ss - MMM dd", Locale.getDefault()) }
    val timeString = remember(log.timestamp) { timeFormat.format(Date(log.timestamp)) }
    val isSuccess = log.status == "SUCCESS"

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = log.actionType,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "BMS: ${log.deviceAddress}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = timeString,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSuccess) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
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
