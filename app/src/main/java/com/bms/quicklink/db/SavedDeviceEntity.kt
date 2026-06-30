package com.bms.quicklink.db

data class SavedDeviceEntity(
    val id: Long = 0,
    val nickname: String,
    val address: String,
    val dateAdded: Long
)
