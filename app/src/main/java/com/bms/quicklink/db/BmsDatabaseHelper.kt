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

class BmsDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "bms_quicklink.db"
        private const val DATABASE_VERSION = 2

        // Tables
        private const val TABLE_AUDIT_LOGS = "audit_logs"

        // Columns Audit Logs
        private const val COL_LOG_ID = "id"
        private const val COL_LOG_TIMESTAMP = "timestamp"
        private const val COL_LOG_ACTION = "action_type"
        private const val COL_LOG_ADDRESS = "device_address"
        private const val COL_LOG_STATUS = "status"
    }

    private val dbScope = CoroutineScope(Dispatchers.Default)

    private val _auditLogsFlow = MutableStateFlow<List<AuditLogEntity>>(emptyList())
    val auditLogsFlow: StateFlow<List<AuditLogEntity>> = _auditLogsFlow

    init {
        refreshAuditLogs()
    }

    override fun onCreate(db: SQLiteDatabase) {
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
        db.execSQL("DROP TABLE IF EXISTS user_auth")
        db.execSQL("DROP TABLE IF EXISTS saved_devices")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_AUDIT_LOGS")
        onCreate(db)
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
