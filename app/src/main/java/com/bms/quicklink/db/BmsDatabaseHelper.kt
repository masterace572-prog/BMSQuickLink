package com.bms.quicklink.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BmsDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "bms_quicklink.db"
        private const val DATABASE_VERSION = 1

        // Tables
        private const val TABLE_AUTH = "user_auth"
        private const val TABLE_SAVED_DEVICES = "saved_devices"
        private const val TABLE_AUDIT_LOGS = "audit_logs"

        // Columns Auth
        private const val COL_AUTH_ID = "id"
        private const val COL_AUTH_PIN_HASH = "pin_hash"

        // Columns Saved Devices
        private const val COL_DEV_ID = "id"
        private const val COL_DEV_NICKNAME = "nickname"
        private const val COL_DEV_ADDRESS = "address"
        private const val COL_DEV_DATE = "date_added"

        // Columns Audit Logs
        private const val COL_LOG_ID = "id"
        private const val COL_LOG_TIMESTAMP = "timestamp"
        private const val COL_LOG_ACTION = "action_type"
        private const val COL_LOG_ADDRESS = "device_address"
        private const val COL_LOG_STATUS = "status"
    }

    private val dbScope = CoroutineScope(Dispatchers.Default)

    private val _savedDevicesFlow = MutableStateFlow<List<SavedDeviceEntity>>(emptyList())
    val savedDevicesFlow: StateFlow<List<SavedDeviceEntity>> = _savedDevicesFlow

    private val _auditLogsFlow = MutableStateFlow<List<AuditLogEntity>>(emptyList())
    val auditLogsFlow: StateFlow<List<AuditLogEntity>> = _auditLogsFlow

    init {
        refreshSavedDevices()
        refreshAuditLogs()
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLE_AUTH (" +
                    "$COL_AUTH_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COL_AUTH_PIN_HASH TEXT NOT NULL)"
        )
        db.execSQL(
            "CREATE TABLE $TABLE_SAVED_DEVICES (" +
                    "$COL_DEV_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COL_DEV_NICKNAME TEXT NOT NULL, " +
                    "$COL_DEV_ADDRESS TEXT NOT NULL UNIQUE, " +
                    "$COL_DEV_DATE INTEGER NOT NULL)"
        )
        db.execSQL(
            "CREATE TABLE $TABLE_AUDIT_LOGS (" +
                    "$COL_LOG_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COL_LOG_TIMESTAMP INTEGER NOT NULL, " +
                    "$COL_LOG_ACTION TEXT NOT NULL, " +
                    "$COL_LOG_ADDRESS TEXT NOT NULL, " +
                    "$COL_LOG_STATUS TEXT NOT NULL)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_AUTH")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SAVED_DEVICES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_AUDIT_LOGS")
        onCreate(db)
    }

    // --- AUTH CRUD ---
    suspend fun getPinHash(): String? = withContext(Dispatchers.IO) {
        val db = readableDatabase
        val cursor = db.query(TABLE_AUTH, arrayOf(COL_AUTH_PIN_HASH), null, null, null, null, "$COL_AUTH_ID DESC", "1")
        var hash: String? = null
        if (cursor.moveToFirst()) {
            hash = cursor.getString(0)
        }
        cursor.close()
        hash
    }

    suspend fun savePinHash(pinHash: String): Boolean = withContext(Dispatchers.IO) {
        val db = writableDatabase
        db.delete(TABLE_AUTH, null, null)
        val values = ContentValues().apply {
            put(COL_AUTH_PIN_HASH, pinHash)
        }
        val id = db.insert(TABLE_AUTH, null, values)
        id != -1L
    }

    // --- SAVED DEVICES CRUD ---
    fun addSavedDevice(nickname: String, address: String) {
        dbScope.launch(Dispatchers.IO) {
            val db = writableDatabase
            val values = ContentValues().apply {
                put(COL_DEV_NICKNAME, nickname)
                put(COL_DEV_ADDRESS, address)
                put(COL_DEV_DATE, System.currentTimeMillis())
            }
            db.insertWithOnConflict(TABLE_SAVED_DEVICES, null, values, SQLiteDatabase.CONFLICT_REPLACE)
            refreshSavedDevices()
        }
    }

    fun deleteSavedDevice(address: String) {
        dbScope.launch(Dispatchers.IO) {
            val db = writableDatabase
            db.delete(TABLE_SAVED_DEVICES, "$COL_DEV_ADDRESS = ?", arrayOf(address))
            refreshSavedDevices()
        }
    }

    private fun refreshSavedDevices() {
        dbScope.launch(Dispatchers.IO) {
            val db = readableDatabase
            val cursor = db.query(TABLE_SAVED_DEVICES, null, null, null, null, null, "$COL_DEV_DATE DESC")
            val list = mutableListOf<SavedDeviceEntity>()
            while (cursor.moveToNext()) {
                list.add(
                    SavedDeviceEntity(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_DEV_ID)),
                        nickname = cursor.getString(cursor.getColumnIndexOrThrow(COL_DEV_NICKNAME)),
                        address = cursor.getString(cursor.getColumnIndexOrThrow(COL_DEV_ADDRESS)),
                        dateAdded = cursor.getLong(cursor.getColumnIndexOrThrow(COL_DEV_DATE))
                    )
                )
            }
            cursor.close()
            _savedDevicesFlow.value = list
        }
    }

    // --- AUDIT LOGS CRUD ---
    fun addAuditLog(actionType: String, deviceAddress: String, status: String) {
        dbScope.launch(Dispatchers.IO) {
            val db = writableDatabase
            val values = ContentValues().apply {
                put(COL_LOG_TIMESTAMP, System.currentTimeMillis())
                put(COL_LOG_ACTION, actionType)
                put(COL_LOG_ADDRESS, deviceAddress)
                put(COL_LOG_STATUS, status)
            }
            db.insert(TABLE_AUDIT_LOGS, null, values)
            
            // Keep only last 100 logs
            db.execSQL("DELETE FROM $TABLE_AUDIT_LOGS WHERE $COL_LOG_ID NOT IN (SELECT $COL_LOG_ID FROM $TABLE_AUDIT_LOGS ORDER BY $COL_LOG_TIMESTAMP DESC LIMIT 100)")
            refreshAuditLogs()
        }
    }

    fun clearAuditLogs() {
        dbScope.launch(Dispatchers.IO) {
            val db = writableDatabase
            db.delete(TABLE_AUDIT_LOGS, null, null)
            refreshAuditLogs()
        }
    }

    private fun refreshAuditLogs() {
        dbScope.launch(Dispatchers.IO) {
            val db = readableDatabase
            val cursor = db.query(TABLE_AUDIT_LOGS, null, null, null, null, null, "$COL_LOG_TIMESTAMP DESC")
            val list = mutableListOf<AuditLogEntity>()
            while (cursor.moveToNext()) {
                list.add(
                    AuditLogEntity(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_LOG_ID)),
                        timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COL_LOG_TIMESTAMP)),
                        actionType = cursor.getString(cursor.getColumnIndexOrThrow(COL_LOG_ACTION)),
                        deviceAddress = cursor.getString(cursor.getColumnIndexOrThrow(COL_LOG_ADDRESS)),
                        status = cursor.getString(cursor.getColumnIndexOrThrow(COL_LOG_STATUS))
                    )
                )
            }
            cursor.close()
            _auditLogsFlow.value = list
        }
    }
}
