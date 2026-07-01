package com.bms.quicklink.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.random.Random

enum class BmsOperatingState { STANDBY, CHARGING, DISCHARGING, FAULT }

data class CellTelemetry(val cellNumber: Int, val voltage: Double)

class BmsStateModel {

    private val modelScope = CoroutineScope(Dispatchers.Default)

    private val _totalVoltage = MutableStateFlow(0.0)
    val totalVoltage: StateFlow<Double> = _totalVoltage

    private val _current = MutableStateFlow(0.0)
    val current: StateFlow<Double> = _current

    private val _power = MutableStateFlow(0.0)
    val power: StateFlow<Double> = _power

    private val _socPercentage = MutableStateFlow(0)
    val socPercentage: StateFlow<Int> = _socPercentage

    private val _operatingState = MutableStateFlow(BmsOperatingState.STANDBY)
    val operatingState: StateFlow<BmsOperatingState> = _operatingState

    private val _cells = MutableStateFlow<List<CellTelemetry>>(emptyList())
    val cells: StateFlow<List<CellTelemetry>> = _cells

    private val _maxCellVoltage = MutableStateFlow(0.0)
    val maxCellVoltage: StateFlow<Double> = _maxCellVoltage

    private val _minCellVoltage = MutableStateFlow(0.0)
    val minCellVoltage: StateFlow<Double> = _minCellVoltage

    private val _deltaVoltage = MutableStateFlow(0.0)
    val deltaVoltage: StateFlow<Double> = _deltaVoltage

    private val _mosfetTemp = MutableStateFlow(0.0)
    val mosfetTemp: StateFlow<Double> = _mosfetTemp

    private val _ambientTemp = MutableStateFlow(0.0)
    val ambientTemp: StateFlow<Double> = _ambientTemp

    private val _cycleCount = MutableStateFlow(0)
    val cycleCount: StateFlow<Int> = _cycleCount

    private val _batteryHealth = MutableStateFlow(100)
    val batteryHealth: StateFlow<Int> = _batteryHealth

    // --- FAULT & ALARM FLAGS ---
    private val _hasOverVoltageFault = MutableStateFlow(false)
    val hasOverVoltageFault: StateFlow<Boolean> = _hasOverVoltageFault

    private val _hasUnderVoltageFault = MutableStateFlow(false)
    val hasUnderVoltageFault: StateFlow<Boolean> = _hasUnderVoltageFault

    private val _hasOverCurrentFault = MutableStateFlow(false)
    val hasOverCurrentFault: StateFlow<Boolean> = _hasOverCurrentFault

    private val _hasShortCircuitFault = MutableStateFlow(false)
    val hasShortCircuitFault: StateFlow<Boolean> = _hasShortCircuitFault

    private val _hasActiveFault = MutableStateFlow(false)
    val hasActiveFault: StateFlow<Boolean> = _hasActiveFault

    private var simTimer: Timer? = null

    fun parseIncomingPacket(rawBytes: ByteArray) {
        if (rawBytes.size < 4) return
        val header1 = rawBytes[0].toInt() and 0xFF
        val header2 = rawBytes[1].toInt() and 0xFF

        if (header1 == 0xDD && header2 == 0x5A) {
            parseTelemetryFrame(rawBytes)
        }
    }

    private fun parseTelemetryFrame(rawBytes: ByteArray) {
        if (rawBytes.size < 18) return

        try {
            // Byte 2-3: Total Voltage (0.01V units)
            val voltsRaw = ((rawBytes[2].toInt() and 0xFF) shl 8) or (rawBytes[3].toInt() and 0xFF)
            _totalVoltage.value = voltsRaw * 0.01

            // Byte 4-5: Current (0.01A units, two's complement for discharge)
            var currentRaw = ((rawBytes[4].toInt() and 0xFF) shl 8) or (rawBytes[5].toInt() and 0xFF)
            if ((currentRaw and 0x8000) != 0) {
                currentRaw -= 0x10000
            }
            _current.value = currentRaw * 0.01
            _power.value = abs(_totalVoltage.value * _current.value)

            // Byte 6: State of Charge (SOC %)
            _socPercentage.value = (rawBytes[6].toInt() and 0xFF).coerceIn(0, 100)

            // Byte 7-8: Hardware Fault Bitmask
            val faultMask = ((rawBytes[7].toInt() and 0xFF) shl 8) or (rawBytes[8].toInt() and 0xFF)
            _hasOverVoltageFault.value = (faultMask and 0x0001) != 0
            _hasUnderVoltageFault.value = (faultMask and 0x0002) != 0
            _hasOverCurrentFault.value = (faultMask and 0x0004) != 0
            _hasShortCircuitFault.value = (faultMask and 0x0008) != 0

            _hasActiveFault.value = _hasOverVoltageFault.value || _hasUnderVoltageFault.value || _hasOverCurrentFault.value || _hasShortCircuitFault.value

            // Update Operating State
            if (_hasActiveFault.value) {
                _operatingState.value = BmsOperatingState.FAULT
            } else if (_current.value > 0.1) {
                _operatingState.value = BmsOperatingState.CHARGING
            } else if (_current.value < -0.1) {
                _operatingState.value = BmsOperatingState.DISCHARGING
            } else {
                _operatingState.value = BmsOperatingState.STANDBY
            }

            // Byte 9: Cell Count (N)
            val cellCount = rawBytes[9].toInt() and 0xFF
            var currentIndex = 10
            val updatedCells = mutableListOf<CellTelemetry>()
            var tempMax = -100.0
            var tempMin = 100.0

            for (i in 0 until cellCount) {
                if (currentIndex + 1 >= rawBytes.size) break
                val cellVoltsRaw = ((rawBytes[currentIndex].toInt() and 0xFF) shl 8) or (rawBytes[currentIndex + 1].toInt() and 0xFF)
                val cellVolts = cellVoltsRaw * 0.001
                updatedCells.add(CellTelemetry(i + 1, cellVolts))
                if (cellVolts > tempMax) tempMax = cellVolts
                if (cellVolts < tempMin) tempMin = cellVolts
                currentIndex += 2
            }

            if (updatedCells.isNotEmpty()) {
                _cells.value = updatedCells
                _maxCellVoltage.value = tempMax
                _minCellVoltage.value = tempMin
                _deltaVoltage.value = ((tempMax - tempMin) * 1000.0).roundToInt() / 1000.0
            }

            // Read Temperatures
            if (currentIndex + 3 < rawBytes.size) {
                val mosfetTempRaw = ((rawBytes[currentIndex].toInt() and 0xFF) shl 8) or (rawBytes[currentIndex + 1].toInt() and 0xFF)
                val ambientTempRaw = ((rawBytes[currentIndex + 2].toInt() and 0xFF) shl 8) or (rawBytes[currentIndex + 3].toInt() and 0xFF)
                _mosfetTemp.value = (mosfetTempRaw - 2731) * 0.1
                _ambientTemp.value = (ambientTempRaw - 2731) * 0.1
                currentIndex += 4
            }

            // Read Cycles & Health
            if (currentIndex + 1 < rawBytes.size) {
                val cyclesRaw = ((rawBytes[currentIndex].toInt() and 0xFF) shl 8) or (rawBytes[currentIndex + 1].toInt() and 0xFF)
                _cycleCount.value = cyclesRaw
                _batteryHealth.value = (100 - (cyclesRaw / 30)).coerceIn(10, 100)
            }
        } catch (e: Exception) {
            // Safe parse failure
        }
    }

    fun startSimulationEngine() {
        simTimer?.cancel()
        simTimer = Timer().apply {
            scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    generateSimulatedTelemetryPacket()
                }
            }, 0, 1000)
        }
    }

    fun stopSimulationEngine() {
        simTimer?.cancel()
        simTimer = null
        resetTelemetry()
    }

    private fun resetTelemetry() {
        _totalVoltage.value = 0.0
        _current.value = 0.0
        _power.value = 0.0
        _socPercentage.value = 0
        _operatingState.value = BmsOperatingState.STANDBY
        _cells.value = emptyList()
        _maxCellVoltage.value = 0.0
        _minCellVoltage.value = 0.0
        _deltaVoltage.value = 0.0
        _mosfetTemp.value = 0.0
        _ambientTemp.value = 0.0
        _cycleCount.value = 0
        _batteryHealth.value = 100
        _hasActiveFault.value = false
        _hasOverVoltageFault.value = false
        _hasUnderVoltageFault.value = false
        _hasOverCurrentFault.value = false
        _hasShortCircuitFault.value = false
    }

    private fun generateSimulatedTelemetryPacket() {
        val rawBytes = ByteArray(60)
        rawBytes[0] = 0xDD.toByte()
        rawBytes[1] = 0x5A.toByte()

        // Simulated Total Voltage ~53.2V (5320)
        val volts = 5320 + Random.nextInt(-5, 6)
        rawBytes[2] = ((volts shr 8) and 0xFF).toByte()
        rawBytes[3] = (volts and 0xFF).toByte()

        // Simulated Current ~15.5A (1550)
        val curr = 1550 + Random.nextInt(-20, 21)
        rawBytes[4] = ((curr shr 8) and 0xFF).toByte()
        rawBytes[5] = (curr and 0xFF).toByte()

        // Simulated SOC ~84%
        rawBytes[6] = 84.toByte()

        // Fault mask (0 = normal)
        rawBytes[7] = 0.toByte()
        rawBytes[8] = 0.toByte()

        // 16 Cells
        rawBytes[9] = 16.toByte()
        var idx = 10
        for (i in 0 until 16) {
            val cVolts = 3325 + Random.nextInt(-10, 11)
            rawBytes[idx] = ((cVolts shr 8) and 0xFF).toByte()
            rawBytes[idx + 1] = (cVolts and 0xFF).toByte()
            idx += 2
        }

        // Temps (2731 + 325 = 3056 -> 32.5 C)
        val tMosfet = 3056 + Random.nextInt(-2, 3)
        val tAmbient = 2981 + Random.nextInt(-1, 2)
        rawBytes[idx] = ((tMosfet shr 8) and 0xFF).toByte()
        rawBytes[idx + 1] = (tMosfet and 0xFF).toByte()
        rawBytes[idx + 2] = ((tAmbient shr 8) and 0xFF).toByte()
        rawBytes[idx + 3] = (tAmbient and 0xFF).toByte()
        idx += 4

        // Cycles (45 cycles)
        rawBytes[idx] = 0.toByte()
        rawBytes[idx + 1] = 45.toByte()

        parseTelemetryFrame(rawBytes)
    }
}
