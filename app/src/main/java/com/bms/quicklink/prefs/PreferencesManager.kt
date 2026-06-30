package com.bms.quicklink.prefs

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PreferencesManager(context: Context) {

    companion object {
        private const val PREFS_NAME = "bms_preferences"
        private const val KEY_DARK_MODE = "key_dark_mode"
        private const val KEY_DEVELOPER_MODE = "key_developer_mode"
        private const val KEY_VERIFY_TIMEOUT = "key_verify_timeout"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _isDarkMode = MutableStateFlow(prefs.getBoolean(KEY_DARK_MODE, true))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    private val _isDeveloperMode = MutableStateFlow(prefs.getBoolean(KEY_DEVELOPER_MODE, false))
    val isDeveloperMode: StateFlow<Boolean> = _isDeveloperMode

    private val _verifyTimeoutMs = MutableStateFlow(prefs.getLong(KEY_VERIFY_TIMEOUT, 2000L))
    val verifyTimeoutMs: StateFlow<Long> = _verifyTimeoutMs

    fun setDarkMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
        _isDarkMode.value = enabled
    }

    fun setDeveloperMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DEVELOPER_MODE, enabled).apply()
        _isDeveloperMode.value = enabled
    }

    fun setVerifyTimeoutMs(timeoutMs: Long) {
        prefs.edit().putLong(KEY_VERIFY_TIMEOUT, timeoutMs).apply()
        _verifyTimeoutMs.value = timeoutMs
    }
}
