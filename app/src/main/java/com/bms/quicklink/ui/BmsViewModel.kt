package com.bms.quicklink.ui

import androidx.lifecycle.ViewModel
import com.bms.quicklink.ble.BleFsmState
import com.bms.quicklink.data.BmsDevice
import com.bms.quicklink.data.BmsRepository
import com.bms.quicklink.data.SwitchState
import com.bms.quicklink.data.SwitchType
import com.bms.quicklink.db.AuditLogEntity
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
    val terminalLogs: StateFlow<List<String>> = repository.terminalLogs
    val errorEvents: SharedFlow<String> = repository.errorEvents

    private val _confirmationDialogState = MutableStateFlow<ConfirmationDialogData?>(null)
    val confirmationDialogState: StateFlow<ConfirmationDialogData?> = _confirmationDialogState

    fun onScanTapped() = repository.startScan()
    fun onStopScanTapped() = repository.stopScan()
    fun onConnectTapped(device: BmsDevice) = repository.connect(device)
    fun onConnectToMacAddressTapped(address: String) = repository.connectToMacAddress(address)
    fun onDisconnectTapped() = repository.disconnect()
    fun onClearTerminalLogsTapped() = repository.clearTerminalLogs()

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
    val auditLogs: StateFlow<List<AuditLogEntity>> = repository.auditLogs

    fun clearAuditLogs() = repository.clearAuditLogs()

    // --- SETTINGS & PREFERENCES ---
    val developerMode: StateFlow<Boolean> = repository.developerMode
    val themeMode: StateFlow<String> = repository.themeMode
    val accentColor: StateFlow<String> = repository.accentColor
    val cardStyle: StateFlow<String> = repository.cardStyle
    val cornerStyle: StateFlow<String> = repository.cornerStyle
    val verifyTimeoutMs: StateFlow<Long> = repository.verifyTimeoutMs
    val isSimulationMode: StateFlow<Boolean> = repository.isSimulationMode
    val isOnboardingCompleted: StateFlow<Boolean> = repository.isOnboardingCompleted

    fun onDeveloperModeToggled(enabled: Boolean) = repository.setDeveloperMode(enabled)
    fun onThemeModeSelected(mode: String) = repository.setThemeMode(mode)
    fun onAccentColorSelected(color: String) = repository.setAccentColor(color)
    fun onCardStyleSelected(style: String) = repository.setCardStyle(style)
    fun onCornerStyleSelected(style: String) = repository.setCornerStyle(style)
    fun onVerifyTimeoutSelected(timeoutMs: Long) = repository.setVerifyTimeoutMs(timeoutMs)
    fun onSimulationModeToggled(enabled: Boolean) = repository.setSimulationMode(enabled)
    fun onOnboardingCompleted() = repository.setOnboardingCompleted(true)
}
