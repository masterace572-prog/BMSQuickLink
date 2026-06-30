package com.bms.quicklink

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.bms.quicklink.ui.BmsViewModel
import com.bms.quicklink.ui.BmsViewModelFactory
import com.bms.quicklink.ui.components.PermissionRationaleDialog
import com.bms.quicklink.ui.navigation.AppNavigation
import com.bms.quicklink.ui.theme.BMSQuickLinkTheme

class MainActivity : ComponentActivity() {

    private val viewModel: BmsViewModel by viewModels {
        val app = application as MainApplication
        BmsViewModelFactory(app.repository, app.authManager)
    }

    private val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        )
    } else {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val hasBleSupport = packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)

        setContent {
            val isDarkMode by viewModel.darkMode.collectAsState()

            BMSQuickLinkTheme(darkTheme = isDarkMode) {
                if (!hasBleSupport) {
                    UnsupportedDeviceScreen()
                } else {
                    MainContent(viewModel = viewModel)
                }
            }
        }
    }

    @Composable
    private fun MainContent(viewModel: BmsViewModel) {
        var hasPermissions by remember { mutableStateOf(checkPermissionsGranted()) }
        var showRationaleDialog by remember { mutableStateOf(false) }
        var isPermanentDenial by remember { mutableStateOf(false) }

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        val bluetoothAdapter = bluetoothManager?.adapter
        var isBluetoothEnabled by remember { mutableStateOf(bluetoothAdapter?.isEnabled == true) }

        val enableBluetoothLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { _ ->
            isBluetoothEnabled = bluetoothAdapter?.isEnabled == true
        }

        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val allGranted = permissions.values.all { it }
            hasPermissions = allGranted
            if (!allGranted) {
                val shouldShowRationale = requiredPermissions.any { permission ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        shouldShowRequestPermissionRationale(permission)
                    } else false
                }
                isPermanentDenial = !shouldShowRationale
                showRationaleDialog = true
            }
        }

        // Initial permission check on launch
        LaunchedEffect(Unit) {
            if (!hasPermissions) {
                permissionLauncher.launch(requiredPermissions)
            } else if (!isBluetoothEnabled && bluetoothAdapter != null) {
                enableBluetoothLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            }
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AppNavigation(
                viewModel = viewModel,
                hasPermissions = hasPermissions && isBluetoothEnabled,
                onRequestPermissions = {
                    if (!hasPermissions) {
                        permissionLauncher.launch(requiredPermissions)
                    } else if (!isBluetoothEnabled && bluetoothAdapter != null) {
                        enableBluetoothLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
                    }
                }
            )

            if (showRationaleDialog) {
                PermissionRationaleDialog(
                    isPermanentDenial = isPermanentDenial,
                    onRetry = {
                        showRationaleDialog = false
                        permissionLauncher.launch(requiredPermissions)
                    },
                    onOpenSettings = {
                        showRationaleDialog = false
                        openAppSettings()
                    },
                    onDismiss = { showRationaleDialog = false }
                )
            }
        }
    }

    private fun checkPermissionsGranted(): Boolean {
        return requiredPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }
}

@Composable
fun UnsupportedDeviceScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = "Unsupported Device",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Unsupported Device",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "BMS Quick Link & Control requires Bluetooth Low Energy (BLE) support, which is not available on this device.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}
