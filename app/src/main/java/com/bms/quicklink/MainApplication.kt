package com.bms.quicklink

import android.app.Application
import com.bms.quicklink.ble.BleManager
import com.bms.quicklink.data.BmsRepository
import com.bms.quicklink.data.BmsStateModel
import com.bms.quicklink.db.BmsDatabaseHelper
import com.bms.quicklink.prefs.PreferencesManager

class MainApplication : Application() {

    lateinit var dbHelper: BmsDatabaseHelper
        private set

    lateinit var prefsManager: PreferencesManager
        private set

    lateinit var stateModel: BmsStateModel
        private set

    lateinit var bleManager: BleManager
        private set

    lateinit var repository: BmsRepository
        private set

    override fun onCreate() {
        super.onCreate()
        dbHelper = BmsDatabaseHelper(this)
        prefsManager = PreferencesManager(this)
        stateModel = BmsStateModel()
        bleManager = BleManager(this, stateModel)
        repository = BmsRepository(bleManager, dbHelper, prefsManager, stateModel)
    }
}
