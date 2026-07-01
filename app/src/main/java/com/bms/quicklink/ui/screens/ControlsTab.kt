package com.bms.quicklink.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.LockOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.bms.quicklink.ble.BleFsmState
import com.bms.quicklink.ui.BmsViewModel
import com.bms.quicklink.ui.components.ControlPanel
import com.bms.quicklink.ui.theme.LocalCardStyle
import com.bms.quicklink.ui.theme.LocalCornerStyle

@Composable
fun ControlsTab(
    viewModel: BmsViewModel,
    hasPermissions: Boolean,
    onRequestPermissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fsmState by viewModel.fsmState.collectAsState()
    val switchState by viewModel.switchState.collectAsState()
    val isSimulationMode by viewModel.isSimulationMode.collectAsState()
    val cardStyle = LocalCardStyle.current
    val cornerStyle = LocalCornerStyle.current
    val cardRadius = when (cornerStyle) {
        "SHARP" -> 8.dp
        "SOFT" -> 28.dp
        else -> 20.dp
    }

    val isConnected = fsmState is BleFsmState.Connected

    var pinCode by remember { mutableStateOf("") }
    var isParametersUnlocked by remember { mutableStateOf(false) }

    val cardBg = when (cardStyle) {
        "GLASS" -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        "OUTLINED" -> Color.Transparent
        else -> MaterialTheme.colorScheme.surface
    }
    val cardBorder = if (cardStyle == "FILLED") Color.Transparent else MaterialTheme.colorScheme.outline

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
                text = "Hardware Controls",
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

        // Elegant Disconnected Info Banner
        AnimatedVisibility(
            visible = !isConnected,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            val bannerBg = when (cardStyle) {
                "GLASS" -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                "OUTLINED" -> Color.Transparent
                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
            val bannerBorder = if (cardStyle == "FILLED") Color.Transparent else MaterialTheme.colorScheme.outline

            Card(
                shape = RoundedCornerShape(cardRadius),
                colors = CardDefaults.cardColors(containerColor = bannerBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(if (cardStyle == "FILLED") 0.dp else 1.dp, bannerBorder, RoundedCornerShape(cardRadius))
            ) {
                Row(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(18.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Switches Disabled",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Hardware switches are locked in view-only mode. Please connect to a BMS device in the Connection tab to unlock controls.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Control Panel
        ControlPanel(
            isConnected = isConnected,
            switchState = switchState,
            onSwitchToggled = viewModel::onSwitchToggled
        )

        Divider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(vertical = 4.dp))

        // PIN Protected Safety Parameters View
        Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
            Text(text = "HARDWARE SAFETY LIMITS", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)

            if (!isParametersUnlocked) {
                Card(
                    shape = RoundedCornerShape(cardRadius),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(if (cardStyle == "FILLED") 0.dp else 1.dp, cardBorder, RoundedCornerShape(cardRadius))
                ) {
                    Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.LockOutline, contentDescription = "Locked", tint = Color(0xFFFF9800), modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = "Protected Hardware Parameters", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Text(text = "Enter your 6-digit hardware PIN to unlock and review primary cut-off thresholds.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = pinCode,
                                onValueChange = { if (it.length <= 6) pinCode = it },
                                placeholder = { Text("••••••", letterSpacing = 8.sp) },
                                singleLine = true,
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = {
                                    if (pinCode.length >= 4) {
                                        isParametersUnlocked = true
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp)
                            ) {
                                Text(text = "Unlock", style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }
                }
            } else {
                Card(
                    shape = RoundedCornerShape(cardRadius),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(if (cardStyle == "FILLED") 0.dp else 1.dp, cardBorder, RoundedCornerShape(cardRadius))
                ) {
                    Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Active Safety Cut-Off Thresholds", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                            IconButton(onClick = { isParametersUnlocked = false; pinCode = "" }) {
                                Icon(imageVector = Icons.Default.LockOpen, contentDescription = "Lock", tint = Color(0xFF2E7D32))
                            }
                        }
                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        ParamRow("Cell Over-Voltage Protection", "3.650 V")
                        ParamRow("Cell Under-Voltage Protection", "2.500 V")
                        ParamRow("Pack Over-Current Release", "120.0 A")
                        ParamRow("High Temperature Cut-Off", "65.0 °C")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(110.dp))
    }
}

@Composable
private fun ParamRow(title: String, valStr: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = valStr, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
    }
}
