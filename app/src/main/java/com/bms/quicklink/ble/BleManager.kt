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

    private val _developerMode = MutableStateFlow(false)
    val developerMode: StateFlow<Boolean> = _developerMode

    private val _errorEvents = MutableSharedFlow<String>(extraBufferCapacity = 10)
    val errorEvents: SharedFlow<String> = _errorEvents

    private var currentGatt: BluetoothGatt? = null
    private var activeDevice: BmsDevice? = null

    private var rxCharacteristic: BluetoothGattCharacteristic? = null
    private var txCharacteristic: BluetoothGattCharacteristic? = null

    val commandEngine = CommandEngine { payload ->
        writeRxCharacteristic(payload)
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            if (ScanFilterHelper.isCompatibleDevice(result, _developerMode.value)) {
                _scannedDevices.value = ScanFilterHelper.sortAndFilter(
                    _scannedDevices.value,
                    result,
                    _developerMode.value
                )
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e("BleManager", "Scan failed with error code: $errorCode")
            _fsmState.value = BleFsmState.Disconnected
            _errorEvents.tryEmit("BLE Scan failed (Code $errorCode)")
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            val device = activeDevice ?: return
            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d("BleManager", "Connected to GATT server. Requesting MTU 247...")
                _fsmState.value = BleFsmState.Connecting(device)
                
                // Delay slightly before requesting MTU for stability
                Handler(Looper.getMainLooper()).postDelayed({
                    val mtuRequested = gatt.requestMtu(247)
                    if (!mtuRequested) {
                        Log.w("BleManager", "MTU request failed, continuing to discoverServices")
                        gatt.discoverServices()
                    }
                }, 500)
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.w("BleManager", "Disconnected from GATT server. Status: $status")
                handleDisconnect()
            } else {
                Log.e("BleManager", "GATT connection error. Status: $status")
                handleDisconnect()
                _errorEvents.tryEmit("Connection failed (Status $status)")
            }
        }

        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
            Log.d("BleManager", "MTU changed to $mtu, status: $status. Discovering services...")
            gatt.discoverServices()
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.e("BleManager", "Service discovery failed. Status: $status")
                handleDisconnect()
                _errorEvents.tryEmit("Service discovery failed")
                return
            }

            Log.d("BleManager", "Services discovered. Discovering characteristics...")
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
                Log.d("BleManager", "Found RX and TX characteristics. Enabling Notification...")

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
                    handleDisconnect()
                    _errorEvents.tryEmit("Failed to enable notifications")
                }
            } else {
                Log.e("BleManager", "Required RX/TX characteristics not found. Unsupported device.")
                handleDisconnect()
                _errorEvents.tryEmit("Unsupported device (Missing RX/TX)")
            }
        }

        override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("BleManager", "CCCD descriptor written successfully. Connected Ready.")
                onConnectedReady()
            } else {
                Log.e("BleManager", "CCCD write failed. Status: $status")
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
        // Clear scanned devices when toggling to refresh list cleanly
        _scannedDevices.value = emptyList()
    }

    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    fun startScan() {
        val scanner = bluetoothAdapter?.bluetoothLeScanner
        if (scanner == null || !isBluetoothEnabled()) {
            _errorEvents.tryEmit("Bluetooth is disabled")
            return
        }

        _scannedDevices.value = emptyList()
        _fsmState.value = BleFsmState.Scanning

        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        try {
            scanner.startScan(null, scanSettings, scanCallback)
        } catch (e: Exception) {
            Log.e("BleManager", "Start scan failed: ${e.message}")
            _fsmState.value = BleFsmState.Disconnected
            _errorEvents.tryEmit("Failed to start scan")
        }
    }

    fun stopScan() {
        val scanner = bluetoothAdapter?.bluetoothLeScanner
        try {
            scanner?.stopScan(scanCallback)
        } catch (e: Exception) {
            Log.e("BleManager", "Stop scan failed: ${e.message}")
        }
        if (_fsmState.value == BleFsmState.Scanning) {
            _fsmState.value = BleFsmState.Disconnected
        }
    }

    fun connect(device: BmsDevice) {
        if (_fsmState.value == BleFsmState.Scanning) {
            stopScan()
        }

        activeDevice = device
        _fsmState.value = BleFsmState.Connecting(device)
        currentGatt = device.device.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
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
    }

    private fun handleDisconnect() {
        commandEngine.stop()
        currentGatt?.close()
        currentGatt = null
        rxCharacteristic = null
        txCharacteristic = null
        val dev = activeDevice
        _fsmState.value = BleFsmState.Disconnected
        _errorEvents.tryEmit("Unexpected disconnect")
    }

    private fun onConnectedReady() {
        activeDevice?.let { device ->
            _fsmState.value = BleFsmState.Connected(device)
            commandEngine.start()
        }
    }

    private fun writeRxCharacteristic(data: ByteArray): Boolean {
        val gatt = currentGatt ?: return false
        val char = rxCharacteristic ?: return false

        val writeType = if ((char.properties and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0) {
            BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
        } else {
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        }

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
            false
        }
    }
}
