package com.bms.quicklink.data

import com.bms.quicklink.ble.BleFsmState
import com.bms.quicklink.ble.BleManager
import com.bms.quicklink.ble.CommandTask
import com.bms.quicklink.db.AuditLogEntity
import com.bms.quicklink.db.BmsDatabaseHelper
import com.bms.quicklink.prefs.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BmsRepository(
    private val bleManager: BleManager,
    private val dbHelper: BmsDatabaseHelper,
    private val prefsManager: PreferencesManager,
    private val stateModel: BmsStateModel
) {

    private val repositoryScope = CoroutineScope(Dispatchers.Default)

    val fsmState: StateFlow<BleFsmState> = bleManager.fsmState
    val scannedDevices: StateFlow<List<BmsDevice>> = bleManager.scannedDevices
    val terminalLogs: StateFlow<List<String>> = bleManager.terminalLogs
    val developerMode: StateFlow<Boolean> = prefsManager.isDeveloperMode

    // Appearance & Hardware Configuration Flows
    val themeMode: StateFlow<String> = prefsManager.themeMode
    val accentColor: StateFlow<String> = prefsManager.accentColor
    val cardStyle: StateFlow<String> = prefsManager.cardStyle
    val cornerStyle: StateFlow<String> = prefsManager.cornerStyle
    val verifyTimeoutMs: StateFlow<Long> = prefsManager.verifyTimeoutMs
    val isSimulationMode: StateFlow<Boolean> = prefsManager.isSimulationMode
    val isOnboardingCompleted: StateFlow<Boolean> = prefsManager.isOnboardingCompleted

    val auditLogs: StateFlow<List<AuditLogEntity>> = dbHelper.auditLogsFlow

    // --- TELEMETRY FLOWS ---
    val totalVoltage: StateFlow<Double> = stateModel.totalVoltage
    val current: StateFlow<Double> = stateModel.current
    val power: StateFlow<Double> = stateModel.power
    val socPercentage: StateFlow<Int> = stateModel.socPercentage
    val operatingState: StateFlow<BmsOperatingState> = stateModel.operatingState
    val cells: StateFlow<List<CellTelemetry>> = stateModel.cells
    val maxCellVoltage: StateFlow<Double> = stateModel.maxCellVoltage
    val minCellVoltage: StateFlow<Double> = stateModel.minCellVoltage
    val deltaVoltage: StateFlow<Double> = stateModel.deltaVoltage
    val mosfetTemp: StateFlow<Double> = stateModel.mosfetTemp
    val ambientTemp: StateFlow<Double> = stateModel.ambientTemp
    val cycleCount: StateFlow<Int> = stateModel.cycleCount
    val batteryHealth: StateFlow<Int> = stateModel.batteryHealth

    val hasActiveFault: StateFlow<Boolean> = stateModel.hasActiveFault
    val hasOverVoltageFault: StateFlow<Boolean> = stateModel.hasOverVoltageFault
    val hasUnderVoltageFault: StateFlow<Boolean> = stateModel.hasUnderVoltageFault
    val hasOverCurrentFault: StateFlow<Boolean> = stateModel.hasOverCurrentFault
    val hasShortCircuitFault: StateFlow<Boolean> = stateModel.hasShortCircuitFault

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
            developerMode.collect { enabled -> bleManager.setDeveloperMode(enabled) }
        }
        repositoryScope.launch {
            isSimulationMode.collect { enabled -> bleManager.setSimulationMode(enabled) }
        }
        repositoryScope.launch {
            verifyTimeoutMs.collect { timeout -> bleManager.setVerifyTimeoutMs(timeout) }
        }

        repositoryScope.launch {
            fsmState.collect { state ->
                when (state) {
                    is BleFsmState.Disconnected -> {
                        _switchState.value = SwitchState()
                    }
                    is BleFsmState.Connected -> {
                        dbHelper.addAuditLog("CONNECT", state.device.address, "SUCCESS")
                    }
                    else -> {}
                }
            }
        }
    }

    fun startScan() = bleManager.startScan()
    fun stopScan() = bleManager.stopScan()
    fun connect(device: BmsDevice) = bleManager.connect(device)
    fun connectToMacAddress(address: String) = bleManager.connectToMacAddress(address)
    fun disconnect() = bleManager.disconnect()
    fun clearTerminalLogs() = bleManager.clearTerminalLogs()

    fun setDeveloperMode(enabled: Boolean) = prefsManager.setDeveloperMode(enabled)
    fun setThemeMode(mode: String) = prefsManager.setThemeMode(mode)
    fun setAccentColor(color: String) = prefsManager.setAccentColor(color)
    fun setCardStyle(style: String) = prefsManager.setCardStyle(style)
    fun setCornerStyle(style: String) = prefsManager.setCornerStyle(style)
    fun setVerifyTimeoutMs(timeoutMs: Long) = prefsManager.setVerifyTimeoutMs(timeoutMs)
    fun setSimulationMode(enabled: Boolean) = prefsManager.setSimulationMode(enabled)
    fun setOnboardingCompleted(completed: Boolean) = prefsManager.setOnboardingCompleted(completed)

    fun clearAuditLogs() = dbHelper.clearAuditLogs()

    fun executeSwitchCommand(switchType: SwitchType, targetState: Boolean) {
        val currentState = fsmState.value
        if (currentState !is BleFsmState.Connected) {
            repositoryScope.launch { _errorEvents.emit("Cannot execute command: BMS not connected") }
            return
        }
        val deviceAddress = currentState.device.address

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

        bleManager.appendLog("[INFO] Transmitting hex command for ${switchType.title} (Target: ${if (targetState) "ON" else "OFF"})...")

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
                bleManager.appendLog("[SUCCESS] Command verified: ${switchType.title} successfully switched to ${if (targetState) "ON" else "OFF"}")
                dbHelper.addAuditLog("${switchType.name}_TOGGLE_${if (targetState) "ON" else "OFF"}", deviceAddress, "SUCCESS")
            },
            onFailure = { errorMessage ->
                _switchState.update { current ->
                    when (switchType) {
                        SwitchType.CHARGE -> current.copy(chargePending = false)
                        SwitchType.DISCHARGE -> current.copy(dischargePending = false)
                        SwitchType.BALANCE -> current.copy(balancePending = false)
                        SwitchType.HEATING -> current.copy(heatingPending = false)
                    }
                }
                val timeoutVal = verifyTimeoutMs.value
                bleManager.appendLog("[ERROR] Command failed: ${switchType.title} verification timed out after ${timeoutVal}ms. Rolling back UI.")
                dbHelper.addAuditLog("${switchType.name}_TOGGLE_${if (targetState) "ON" else "OFF"}", deviceAddress, "FAILED")
                repositoryScope.launch {
                    _errorEvents.emit("Command failed: ${switchType.title}")
                }
            }
        )

        bleManager.commandEngine.enqueue(task)
    }
}
