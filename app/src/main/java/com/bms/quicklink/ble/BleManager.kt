package com.bms.quicklink.ble

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.bms.quicklink.data.BmsDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@SuppressLint("MissingPermission")
class BleManager(private val context: Context) {

    private val bluetoothManager: BluetoothManager? =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter

    private val _fsmState = MutableStateFlow<BleFsmState>(BleFsmState.Disconnected)
    val fsmState: StateFlow<BleFsmState> = _fsmState

    private val _scannedDevices = MutableStateFlow<List<BmsDevice>>(emptyList())
    val scannedDevices: StateFlow<List<BmsDevice>> = _scannedDevices

    private val _terminalLogs = MutableStateFlow<List<String>>(emptyList())
    val terminalLogs: StateFlow<List<String>> = _terminalLogs

    private val _developerMode = MutableStateFlow(false)
    val developerMode: StateFlow<Boolean> = _developerMode

    private val _isSimulationMode = MutableStateFlow(false)
    private val _verifyTimeoutMs = MutableStateFlow(2000L)

    private val _errorEvents = MutableSharedFlow<String>(extraBufferCapacity = 10)
    val errorEvents: SharedFlow<String> = _errorEvents

    private var currentGatt: BluetoothGatt? = null
    private var activeDevice: BmsDevice? = null

    private var rxCharacteristic: BluetoothGattCharacteristic? = null
    private var txCharacteristic: BluetoothGattCharacteristic? = null

    val commandEngine = CommandEngine(
        isSimulationMode = { _isSimulationMode.value },
        getTimeoutMs = { _verifyTimeoutMs.value },
        bleWriter = { payload -> writeRxCharacteristic(payload) }
    )

    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    init {
        appendLog("System initialized. BLE Manager ready.")
    }

    private fun appendLog(msg: String) {
        val timestamp = timeFormat.format(Date())
        val logLine = "[$timestamp] $msg"
        _terminalLogs.value = (_terminalLogs.value + logLine).takeLast(100)
    }

    fun clearTerminalLogs() {
        _terminalLogs.value = emptyList()
        appendLog("Terminal logs cleared.")
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            if (ScanFilterHelper.isCompatibleDevice(result, _developerMode.value)) {
                val devName = result.scanRecord?.deviceName ?: result.device.name ?: "Unknown BMS"
                val devAddr = result.device.address ?: "00:00:00:00:00:00"
                
                val exists = _scannedDevices.value.any { it.address == devAddr }
                if (!exists) {
                    appendLog("Discovered compatible BMS: $devName ($devAddr) at ${result.rssi} dBm")
                }

                _scannedDevices.value = ScanFilterHelper.sortAndFilter(
                    _scannedDevices.value,
                    result,
                    _developerMode.value
                )
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e("BleManager", "Scan failed with error code: $errorCode")
            appendLog("BLE Scan failed with OS code: $errorCode")
            _fsmState.value = BleFsmState.Disconnected
            _errorEvents.tryEmit("BLE Scan failed (Code $errorCode)")
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            val device = activeDevice ?: return
            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d("BleManager", "Connected to GATT server. Requesting MTU 247...")
                appendLog("GATT connection established to ${device.address}. Requesting MTU 247...")
                _fsmState.value = BleFsmState.Connecting(device)
                
                Handler(Looper.getMainLooper()).postDelayed({
                    val mtuRequested = gatt.requestMtu(247)
                    if (!mtuRequested) {
                        appendLog("MTU 247 request failed, continuing to discoverServices...")
                        gatt.discoverServices()
                    }
                }, 500)
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                appendLog("Disconnected from GATT server. Status: $status")
                handleDisconnect()
            } else {
                appendLog("GATT connection error. Status: $status")
                handleDisconnect()
                _errorEvents.tryEmit("Connection failed (Status $status)")
            }
        }

        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
            appendLog("MTU changed to $mtu (Status: $status). Discovering GATT services...")
            gatt.discoverServices()
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                appendLog("Service discovery failed. Status: $status")
                handleDisconnect()
                _errorEvents.tryEmit("Service discovery failed")
                return
            }

            appendLog("GATT Services discovered. Scanning for RX/TX characteristics...")
            var foundRx: BluetoothGattCharacteristic? = null
            var foundTx: BluetoothGattCharacteristic? = null

            for (service in gatt.services) {
                for (char in service.characteristics) {
                    val properties = char.properties
                    if ((properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
                        foundTx = char
                    }
                    if ((properties and BluetoothGattCharacteristic.PROPERTY_WRITE) != 0 ||
                        (properties and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0) {
                        foundRx = char
                    }
                }
                if (foundRx != null && foundTx != null) break
            }

            if (foundRx != null && foundTx != null) {
                rxCharacteristic = foundRx
                txCharacteristic = foundTx
                appendLog("Found RX (Write) and TX (Notify) characteristics. Enabling Notifications...")

                val enabled = gatt.setCharacteristicNotification(foundTx, true)
                if (enabled) {
                    val cccdUuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
                    val descriptor = foundTx.getDescriptor(cccdUuid)
                    if (descriptor != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            gatt.writeDescriptor(descriptor, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
                        } else {
                            @Suppress("DEPRECATION")
                            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                            @Suppress("DEPRECATION")
                            gatt.writeDescriptor(descriptor)
                        }
                    } else {
                        onConnectedReady()
                    }
                } else {
                    appendLog("Failed to enable notification descriptor.")
                    handleDisconnect()
                    _errorEvents.tryEmit("Failed to enable notifications")
                }
            } else {
                appendLog("Required RX/TX characteristics not found. Unsupported device.")
                handleDisconnect()
                _errorEvents.tryEmit("Unsupported device (Missing RX/TX)")
            }
        }

        override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                appendLog("CCCD descriptor written successfully. Connected Ready.")
                onConnectedReady()
            } else {
                appendLog("CCCD write failed. Status: $status")
                handleDisconnect()
                _errorEvents.tryEmit("Failed to subscribe to notifications")
            }
        }

        @Suppress("DEPRECATION")
        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            if (characteristic == txCharacteristic) {
                val data = characteristic.value
                if (data != null) {
                    commandEngine.onNotifyReceived(data)
                }
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            if (characteristic == txCharacteristic) {
                commandEngine.onNotifyReceived(value)
            }
        }
    }

    fun setDeveloperMode(enabled: Boolean) {
        _developerMode.value = enabled
        _scannedDevices.value = emptyList()
        appendLog("Developer Mode toggled: $enabled")
    }

    fun setSimulationMode(enabled: Boolean) {
        _isSimulationMode.value = enabled
        _scannedDevices.value = emptyList()
        appendLog("Simulation Mode toggled: $enabled")
        if (_fsmState.value !is BleFsmState.Disconnected) {
            disconnect()
        }
    }

    fun setVerifyTimeoutMs(timeoutMs: Long) {
        _verifyTimeoutMs.value = timeoutMs
        appendLog("GATT Notify Verification Timeout set to $timeoutMs ms")
    }

    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    fun startScan() {
        _scannedDevices.value = emptyList()
        _fsmState.value = BleFsmState.Scanning

        if (_isSimulationMode.value) {
            appendLog("Starting virtual offline simulation scan...")
            Handler(Looper.getMainLooper()).postDelayed({
                val virtualBms1 = BmsDevice(
                    device = null,
                    name = "Virtual LiFePO4 BMS",
                    address = "00:11:22:AA:BB:CC",
                    rssi = -52,
                    timestamp = System.currentTimeMillis()
                )
                val virtualBms2 = BmsDevice(
                    device = null,
                    name = "Virtual Smart Pack",
                    address = "00:11:22:DD:EE:FF",
                    rssi = -68,
                    timestamp = System.currentTimeMillis()
                )
                _scannedDevices.value = listOf(virtualBms1, virtualBms2)
                appendLog("Discovered virtual simulated hardware: Virtual LiFePO4 BMS (-52 dBm)")
                appendLog("Discovered virtual simulated hardware: Virtual Smart Pack (-68 dBm)")
            }, 600)
            return
        }

        val scanner = bluetoothAdapter?.bluetoothLeScanner
        if (scanner == null || !isBluetoothEnabled()) {
            _fsmState.value = BleFsmState.Disconnected
            appendLog("Scan aborted: Bluetooth is disabled or unsupported on this device.")
            _errorEvents.tryEmit("Bluetooth is disabled or unsupported")
            return
        }

        appendLog("Initializing Resolvable Private Address (RPA) scanner...")
        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setReportDelay(0)
            .build()

        try {
            scanner.startScan(null, scanSettings, scanCallback)
            appendLog("BLE low-latency scan started successfully.")
        } catch (e: Exception) {
            Log.e("BleManager", "Start scan failed: ${e.message}")
            appendLog("Start scan failed with exception: ${e.message}")
            _fsmState.value = BleFsmState.Disconnected
            _errorEvents.tryEmit("Failed to start scan")
        }
    }

    fun stopScan() {
        if (!_isSimulationMode.value) {
            val scanner = bluetoothAdapter?.bluetoothLeScanner
            try {
                scanner?.stopScan(scanCallback)
            } catch (e: Exception) {
                Log.e("BleManager", "Stop scan failed: ${e.message}")
            }
        }
        if (_fsmState.value == BleFsmState.Scanning) {
            _fsmState.value = BleFsmState.Disconnected
            appendLog("BLE scan stopped by user.")
        }
    }

    fun connect(device: BmsDevice) {
        if (_fsmState.value == BleFsmState.Scanning) {
            stopScan()
        }

        activeDevice = device
        _fsmState.value = BleFsmState.Connecting(device)
        appendLog("Initializing connection to ${device.name} (${device.address})...")

        if (_isSimulationMode.value || device.device == null) {
            Handler(Looper.getMainLooper()).postDelayed({
                appendLog("Simulated GATT connection established. MTU 247 negotiated.")
                appendLog("Simulated RX/TX characteristics mapped. Subscribed to notifications.")
                onConnectedReady()
            }, 800)
            return
        }

        currentGatt = device.device.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
    }

    fun connectToMacAddress(address: String) {
        if (_fsmState.value == BleFsmState.Scanning) {
            stopScan()
        }

        appendLog("Initializing direct GATT connect to MAC: $address...")

        if (_isSimulationMode.value || bluetoothAdapter == null) {
            val directDevice = BmsDevice(
                device = null,
                name = "Direct Virtual BMS",
                address = address,
                rssi = -55,
                timestamp = System.currentTimeMillis()
            )
            connect(directDevice)
            return
        }

        try {
            val remoteDev = bluetoothAdapter.getRemoteDevice(address)
            val directDevice = BmsDevice(
                device = remoteDev,
                name = "Direct Quick Link",
                address = address,
                rssi = -65,
                timestamp = System.currentTimeMillis()
            )
            connect(directDevice)
        } catch (e: Exception) {
            appendLog("Direct connect failed: Invalid MAC Address format.")
            _errorEvents.tryEmit("Invalid Bluetooth MAC Address format")
        }
    }

    fun disconnect() {
        commandEngine.stop()
        currentGatt?.disconnect()
        currentGatt?.close()
        currentGatt = null
        rxCharacteristic = null
        txCharacteristic = null
        activeDevice = null
        _fsmState.value = BleFsmState.Disconnected
        appendLog("BMS connection closed by user. Command queue reset.")
    }

    private fun handleDisconnect() {
        commandEngine.stop()
        currentGatt?.close()
        currentGatt = null
        rxCharacteristic = null
        txCharacteristic = null
        _fsmState.value = BleFsmState.Disconnected
        appendLog("Unexpected disconnect handled safely. Command queue cleared.")
        _errorEvents.tryEmit("Unexpected disconnect")
    }

    private fun onConnectedReady() {
        activeDevice?.let { device ->
            _fsmState.value = BleFsmState.Connected(device)
            appendLog("BMS Quick Link established successfully. Ready for commands.")
            commandEngine.start()
        }
    }

    private fun writeRxCharacteristic(data: ByteArray): Boolean {
        if (_isSimulationMode.value) {
            appendLog("Simulated write command transmitted: [${data.joinToString(" ") { "%02X".format(it) }}]")
            return true
        }

        val gatt = currentGatt ?: return false
        val char = rxCharacteristic ?: return false

        val writeType = if ((char.properties and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0) {
            BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
        } else {
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        }

        appendLog("Writing command payload to RX characteristic: [${data.joinToString(" ") { "%02X".format(it) }}]")

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                gatt.writeCharacteristic(char, data, writeType) == BluetoothGatt.GATT_SUCCESS
            } else {
                @Suppress("DEPRECATION")
                char.writeType = writeType
                @Suppress("DEPRECATION")
                char.value = data
                @Suppress("DEPRECATION")
                gatt.writeCharacteristic(char)
            }
        } catch (e: Exception) {
            Log.e("BleManager", "Write characteristic failed: ${e.message}")
            appendLog("Write characteristic failed with exception: ${e.message}")
            false
        }
    }
}
