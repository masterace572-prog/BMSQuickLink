package com.bms.quicklink.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.bms.quicklink.ui.BmsViewModel
import com.bms.quicklink.ui.components.ConfirmationDialog
import kotlinx.coroutines.flow.collectLatest

enum class NavigationTab(val title: String, val icon: ImageVector) {
    CONTROLS("Controls", Icons.Default.Tune),
    SAVED("Saved Devices", Icons.Default.Bookmark),
    SETTINGS("Settings", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: BmsViewModel,
    hasPermissions: Boolean,
    onRequestPermissions: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(NavigationTab.CONTROLS) }
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
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                NavigationTab.values().forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        label = { Text(text = tab.title, style = MaterialTheme.typography.labelMedium) },
                        icon = { Icon(imageVector = tab.icon, contentDescription = tab.title) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
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
                NavigationTab.CONTROLS -> ControlsTab(
                    viewModel = viewModel,
                    hasPermissions = hasPermissions,
                    onRequestPermissions = onRequestPermissions
                )
                NavigationTab.SAVED -> SavedDevicesTab(
                    viewModel = viewModel,
                    hasPermissions = hasPermissions,
                    onRequestPermissions = onRequestPermissions
                )
                NavigationTab.SETTINGS -> SettingsTab(
                    viewModel = viewModel,
                    onLogout = onLogout
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
