package com.bms.quicklink.data

import com.bms.quicklink.ble.BleFsmState
import com.bms.quicklink.ble.BleManager
import com.bms.quicklink.ble.CommandTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BmsRepository(private val bleManager: BleManager) {

    private val repositoryScope = CoroutineScope(Dispatchers.Default)

    val fsmState: StateFlow<BleFsmState> = bleManager.fsmState
    val scannedDevices: StateFlow<List<BmsDevice>> = bleManager.scannedDevices
    val developerMode: StateFlow<Boolean> = bleManager.developerMode

    private val _errorEvents = MutableSharedFlow<String>(extraBufferCapacity = 10)
    val errorEvents: SharedFlow<String> = merge(bleManager.errorEvents, _errorEvents).shareIn(
        repositoryScope,
        SharingStarted.WhileSubscribed(5000),
        replay = 0
    )

    private val _switchState = MutableStateFlow(SwitchState())
    val switchState: StateFlow<SwitchState> = _switchState

    init {
        repositoryScope.launch {
            fsmState.collect { state ->
                if (state == BleFsmState.Disconnected) {
                    // Reset switches on disconnect
                    _switchState.value = SwitchState()
                }
            }
        }
    }

    fun startScan() = bleManager.startScan()
    fun stopScan() = bleManager.stopScan()
    fun connect(device: BmsDevice) = bleManager.connect(device)
    fun disconnect() = bleManager.disconnect()
    fun setDeveloperMode(enabled: Boolean) = bleManager.setDeveloperMode(enabled)

    fun executeSwitchCommand(switchType: SwitchType, targetState: Boolean) {
        // Optimistic pending state
        _switchState.update { current ->
            when (switchType) {
                SwitchType.CHARGE -> current.copy(chargePending = true)
                SwitchType.DISCHARGE -> current.copy(dischargePending = true)
                SwitchType.BALANCE -> current.copy(balancePending = true)
                SwitchType.HEATING -> current.copy(heatingPending = true)
            }
        }

        val payload = when (switchType) {
            SwitchType.CHARGE -> byteArrayOf(0x01, if (targetState) 0x01 else 0x00)
            SwitchType.DISCHARGE -> byteArrayOf(0x02, if (targetState) 0x01 else 0x00)
            SwitchType.BALANCE -> byteArrayOf(0x03, if (targetState) 0x01 else 0x00)
            SwitchType.HEATING -> byteArrayOf(0x04, if (targetState) 0x01 else 0x00)
        }

        val verifyPredicate: (ByteArray) -> Boolean = { incoming ->
            incoming.isNotEmpty() && incoming[0] == payload[0]
        }

        val task = CommandTask(
            switchType = switchType,
            targetState = targetState,
            payload = payload,
            verifyPredicate = verifyPredicate,
            onSuccess = {
                _switchState.update { current ->
                    when (switchType) {
                        SwitchType.CHARGE -> current.copy(chargeOn = targetState, chargePending = false)
                        SwitchType.DISCHARGE -> current.copy(dischargeOn = targetState, dischargePending = false)
                        SwitchType.BALANCE -> current.copy(balanceOn = targetState, balancePending = false)
                        SwitchType.HEATING -> current.copy(heatingOn = targetState, heatingPending = false)
                    }
                }
            },
            onFailure = { errorMessage ->
                // Rollback pending state
                _switchState.update { current ->
                    when (switchType) {
                        SwitchType.CHARGE -> current.copy(chargePending = false)
                        SwitchType.DISCHARGE -> current.copy(dischargePending = false)
                        SwitchType.BALANCE -> current.copy(balancePending = false)
                        SwitchType.HEATING -> current.copy(heatingPending = false)
                    }
                }
                repositoryScope.launch {
                    _errorEvents.emit("Command failed: ${switchType.title}")
                }
            }
        )

        bleManager.commandEngine.enqueue(task)
    }
}
