package com.bms.quicklink.data

import android.bluetooth.BluetoothDevice

data class BmsDevice(
    val device: BluetoothDevice,
    val name: String,
    val address: String,
    val rssi: Int,
    val timestamp: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BmsDevice) return false
        return address == other.address
    }

    override fun hashCode(): Int {
        return address.hashCode()
    }
}
