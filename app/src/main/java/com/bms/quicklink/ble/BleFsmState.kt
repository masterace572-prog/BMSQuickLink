package com.bms.quicklink.ble

import com.bms.quicklink.data.BmsDevice

sealed class BleFsmState {
    object Disconnected : BleFsmState()
    object Scanning : BleFsmState()
    data class Connecting(val device: BmsDevice) : BleFsmState()
    data class Connected(val device: BmsDevice) : BleFsmState()
}
