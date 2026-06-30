package com.bms.quicklink.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bms.quicklink.ble.BleFsmState
import com.bms.quicklink.data.BmsDevice
import com.bms.quicklink.data.BmsRepository
import com.bms.quicklink.data.SwitchState
import com.bms.quicklink.data.SwitchType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ConfirmationDialogData(
    val switchType: SwitchType,
    val targetState: Boolean
)

class BmsViewModel(private val repository: BmsRepository) : ViewModel() {

    val fsmState: StateFlow<BleFsmState> = repository.fsmState
    val scannedDevices: StateFlow<List<BmsDevice>> = repository.scannedDevices
    val switchState: StateFlow<SwitchState> = repository.switchState
    val developerMode: StateFlow<Boolean> = repository.developerMode
    val errorEvents: SharedFlow<String> = repository.errorEvents

    private val _confirmationDialogState = MutableStateFlow<ConfirmationDialogData?>(null)
    val confirmationDialogState: StateFlow<ConfirmationDialogData?> = _confirmationDialogState

    fun onScanTapped() {
        repository.startScan()
    }

    fun onStopScanTapped() {
        repository.stopScan()
    }

    fun onConnectTapped(device: BmsDevice) {
        repository.connect(device)
    }

    fun onDisconnectTapped() {
        repository.disconnect()
    }

    fun onDeveloperModeToggled(enabled: Boolean) {
        repository.setDeveloperMode(enabled)
    }

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
}
