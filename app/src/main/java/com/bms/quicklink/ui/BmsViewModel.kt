package com.bms.quicklink.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bms.quicklink.auth.AuthManager
import com.bms.quicklink.auth.AuthState
import com.bms.quicklink.ble.BleFsmState
import com.bms.quicklink.data.BmsDevice
import com.bms.quicklink.data.BmsRepository
import com.bms.quicklink.data.SwitchState
import com.bms.quicklink.data.SwitchType
import com.bms.quicklink.db.AuditLogEntity
import com.bms.quicklink.db.SavedDeviceEntity
import kotlinx.coroutines.flow.*

data class ConfirmationDialogData(
    val switchType: SwitchType,
    val targetState: Boolean
)

class BmsViewModel(
    private val repository: BmsRepository,
    private val authManager: AuthManager
) : ViewModel() {

    // --- AUTH ---
    val authState: StateFlow<AuthState> = authManager.authState
    val authErrorMessage: StateFlow<String?> = authManager.errorMessage
    val authIsLoading: StateFlow<Boolean> = authManager.isLoading

    fun verifyPin(pin: String) = authManager.verifyPin(pin)
    fun setupPin(pin: String) = authManager.setupPin(pin)
    fun logout() = authManager.logout()
    fun clearAuthErrorMessage() = authManager.clearErrorMessage()

    // --- BLE & SWITCHES ---
    val fsmState: StateFlow<BleFsmState> = repository.fsmState
    val scannedDevices: StateFlow<List<BmsDevice>> = repository.scannedDevices
    val switchState: StateFlow<SwitchState> = repository.switchState
    val errorEvents: SharedFlow<String> = repository.errorEvents

    private val _confirmationDialogState = MutableStateFlow<ConfirmationDialogData?>(null)
    val confirmationDialogState: StateFlow<ConfirmationDialogData?> = _confirmationDialogState

    fun onScanTapped() = repository.startScan()
    fun onStopScanTapped() = repository.stopScan()
    fun onConnectTapped(device: BmsDevice) = repository.connect(device)
    fun onDisconnectTapped() = repository.disconnect()

    fun onSwitchToggled(switchType: SwitchType, targetState: Boolean) {
        if (fsmState.value !is BleFsmState.Connected) return
        _confirmationDialogState.value = ConfirmationDialogData(switchType, targetState)
    }

    fun onDialogConfirmed() {
        val data = _confirmationDialogState.value ?: return
        _confirmationDialogState.value = null
        repository.executeSwitchCommand(data.switchType, data.targetState)
    }

    fun onDialogDismissed() {
        _confirmationDialogState.value = null
    }

    // --- DATABASE OPERATIONS ---
    val savedDevices: StateFlow<List<SavedDeviceEntity>> = repository.savedDevices
    val auditLogs: StateFlow<List<AuditLogEntity>> = repository.auditLogs

    fun addSavedDevice(nickname: String, address: String) = repository.addSavedDevice(nickname, address)
    fun deleteSavedDevice(address: String) = repository.deleteSavedDevice(address)
    fun clearAuditLogs() = repository.clearAuditLogs()

    // --- SETTINGS & PREFERENCES ---
    val darkMode: StateFlow<Boolean> = repository.darkMode
    val developerMode: StateFlow<Boolean> = repository.developerMode

    fun onDarkModeToggled(enabled: Boolean) = repository.setDarkMode(enabled)
    fun onDeveloperModeToggled(enabled: Boolean) = repository.setDeveloperMode(enabled)
}
