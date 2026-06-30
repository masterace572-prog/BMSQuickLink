package com.bms.quicklink.ble

import android.bluetooth.le.ScanResult
import com.bms.quicklink.data.BmsDevice

object ScanFilterHelper {

    private val SUPPORTED_PREFIXES = listOf(
        "BMS-", "LOSSIGY", "LSG-", "JK-", "DALY", "JBD", "LLT", "SMARTBMS"
    )

    fun isCompatibleDevice(result: ScanResult, developerMode: Boolean): Boolean {
        if (developerMode) return true

        val record = result.scanRecord ?: return false
        
        // 1. Check Local Name
        val deviceName = record.deviceName ?: result.device.name ?: ""
        if (deviceName.isNotBlank()) {
            if (SUPPORTED_PREFIXES.any { deviceName.startsWith(it, ignoreCase = true) }) {
                return true
            }
        }

        // 2. Check Service UUIDs (If any known BMS UUIDs are advertised, or if we filter by prefix)
        val serviceUuids = record.serviceUuids
        if (serviceUuids != null) {
            // Future specific BMS service UUID checking can go here
        }

        // 3. Check Manufacturer Specific Data
        val manufacturerData = record.manufacturerSpecificData
        if (manufacturerData != null && manufacturerData.size() > 0) {
            // Check known manufacturer IDs or payload if needed
        }

        return false
    }

    fun sortAndFilter(
        currentList: List<BmsDevice>,
        newResult: ScanResult,
        developerMode: Boolean
    ): List<BmsDevice> {
        val device = newResult.device ?: return currentList
        val address = device.address ?: return currentList
        val name = newResult.scanRecord?.deviceName ?: device.name ?: "Unknown BMS ($address)"

        val bmsDevice = BmsDevice(
            device = device,
            name = name,
            address = address,
            rssi = newResult.rssi,
            timestamp = System.currentTimeMillis()
        )

        val mutableMap = currentList.associateBy { it.address }.toMutableMap()
        mutableMap[address] = bmsDevice

        return mutableMap.values.sortedByDescending { it.rssi }
    }
}
