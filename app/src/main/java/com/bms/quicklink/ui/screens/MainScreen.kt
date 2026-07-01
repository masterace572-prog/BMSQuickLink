package com.bms.quicklink.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bms.quicklink.ui.BmsViewModel
import com.bms.quicklink.ui.components.ConfirmationDialog
import kotlinx.coroutines.flow.collectLatest

enum class NavigationTab(val title: String, val icon: ImageVector) {
    CONNECTION("Connection", Icons.Default.Bluetooth),
    CONTROLS("Controls", Icons.Default.Tune),
    SETTINGS("Settings", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: BmsViewModel,
    hasPermissions: Boolean,
    onRequestPermissions: () -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(NavigationTab.CONNECTION) }
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
        bottomBar = {
            // Spectacular Custom Floating Navigation Dock
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(36.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    shadowElevation = 16.dp,
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(36.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NavigationTab.values().forEach { tab ->
                            val isSelected = selectedTab == tab
                            val tabBg by animateColorAsState(targetValue = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent)
                            val iconColor by animateColorAsState(targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(28.dp))
                                    .background(tabBg)
                                    .clickable { selectedTab = tab },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                    Icon(imageVector = tab.icon, contentDescription = tab.title, tint = iconColor, modifier = Modifier.size(24.dp))
                                    if (isSelected) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(text = tab.title, style = MaterialTheme.typography.labelMedium, color = iconColor)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Crossfade(
            targetState = selectedTab,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) { tab ->
            when (tab) {
                NavigationTab.CONNECTION -> ConnectionTab(
                    viewModel = viewModel,
                    hasPermissions = hasPermissions,
                    onRequestPermissions = onRequestPermissions
                )
                NavigationTab.CONTROLS -> ControlsTab(
                    viewModel = viewModel,
                    hasPermissions = hasPermissions,
                    onRequestPermissions = onRequestPermissions
                )
                NavigationTab.SETTINGS -> SettingsTab(
                    viewModel = viewModel,
                    navController = navController
                )
            }
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
