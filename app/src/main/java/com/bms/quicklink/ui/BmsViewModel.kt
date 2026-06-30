package com.bms.quicklink.ui

import androidx.lifecycle.ViewModel
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

class BmsViewModel(private val repository: BmsRepository) : ViewModel() {

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
    val developerMode: StateFlow<Boolean> = repository.developerMode
    val themeMode: StateFlow<String> = repository.themeMode
    val accentColor: StateFlow<String> = repository.accentColor
    val cardStyle: StateFlow<String> = repository.cardStyle

    fun onDeveloperModeToggled(enabled: Boolean) = repository.setDeveloperMode(enabled)
    fun onThemeModeSelected(mode: String) = repository.setThemeMode(mode)
    fun onAccentColorSelected(color: String) = repository.setAccentColor(color)
    fun onCardStyleSelected(style: String) = repository.setCardStyle(style)
}
