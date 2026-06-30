package com.bms.quicklink.db

data class AuditLogEntity(
    val id: Long = 0,
    val timestamp: Long,
    val actionType: String, // e.g., "CHARGE_TOGGLE", "DISCHARGE_TOGGLE", "CONNECT"
    val deviceAddress: String,
    val status: String // e.g., "SUCCESS", "FAILED", "VERIFICATION_TIMEOUT"
)
