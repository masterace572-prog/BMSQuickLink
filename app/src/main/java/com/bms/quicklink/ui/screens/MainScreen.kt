package com.bms.quicklink.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bms.quicklink.ui.BmsViewModel
import com.bms.quicklink.ui.components.ConfirmationDialog
import kotlinx.coroutines.flow.collectLatest

enum class NavigationTab(val title: String, val icon: ImageVector) {
    CONTROLS("Controls", Icons.Default.Tune),
    SAVED("Saved Profiles", Icons.Default.Bookmark),
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(32.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    shadowElevation = 12.dp,
                    modifier = Modifier.fillMaxWidth(0.95f)
                ) {
                    NavigationBar(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        tonalElevation = 0.dp,
                        windowInsets = NavigationBarDefaults.windowInsets,
                        modifier = Modifier.height(72.dp)
                    ) {
                        NavigationTab.values().forEach { tab ->
                            NavigationBarItem(
                                selected = selectedTab == tab,
                                onClick = { selectedTab = tab },
                                label = { Text(text = tab.title, style = MaterialTheme.typography.labelMedium) },
                                icon = { Icon(imageVector = tab.icon, contentDescription = tab.title, modifier = Modifier.size(24.dp)) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
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
