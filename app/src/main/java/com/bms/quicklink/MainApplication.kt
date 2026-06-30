package com.bms.quicklink

import android.app.Application
import com.bms.quicklink.ble.BleManager
import com.bms.quicklink.data.BmsRepository

class MainApplication : Application() {

    lateinit var bleManager: BleManager
        private set

    lateinit var repository: BmsRepository
        private set

    override fun onCreate() {
        super.onCreate()
        bleManager = BleManager(this)
        repository = BmsRepository(bleManager)
    }
}
