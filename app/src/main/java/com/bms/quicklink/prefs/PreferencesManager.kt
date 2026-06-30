package com.bms.quicklink.prefs

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PreferencesManager(context: Context) {

    companion object {
        private const val PREFS_NAME = "bms_preferences"
        private const val KEY_THEME_MODE = "key_theme_mode" // "DARK", "LIGHT", "SYSTEM"
        private const val KEY_ACCENT_COLOR = "key_accent_color" // "BLUE", "EMERALD", "ORANGE", "ROSE", "CYAN", "PURPLE"
        private const val KEY_CARD_STYLE = "key_card_style" // "FILLED", "OUTLINED", "GLASS"
        private const val KEY_DEVELOPER_MODE = "key_developer_mode"
        private const val KEY_VERIFY_TIMEOUT = "key_verify_timeout"
        private const val KEY_SIMULATION_MODE = "key_simulation_mode"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(prefs.getString(KEY_THEME_MODE, "DARK") ?: "DARK")
    val themeMode: StateFlow<String> = _themeMode

    private val _accentColor = MutableStateFlow(prefs.getString(KEY_ACCENT_COLOR, "BLUE") ?: "BLUE")
    val accentColor: StateFlow<String> = _accentColor

    private val _cardStyle = MutableStateFlow(prefs.getString(KEY_CARD_STYLE, "FILLED") ?: "FILLED")
    val cardStyle: StateFlow<String> = _cardStyle

    private val _isDeveloperMode = MutableStateFlow(prefs.getBoolean(KEY_DEVELOPER_MODE, false))
    val isDeveloperMode: StateFlow<Boolean> = _isDeveloperMode

    private val _verifyTimeoutMs = MutableStateFlow(prefs.getLong(KEY_VERIFY_TIMEOUT, 2000L))
    val verifyTimeoutMs: StateFlow<Long> = _verifyTimeoutMs

    private val _isSimulationMode = MutableStateFlow(prefs.getBoolean(KEY_SIMULATION_MODE, false))
    val isSimulationMode: StateFlow<Boolean> = _isSimulationMode

    fun setThemeMode(mode: String) {
        prefs.edit().putString(KEY_THEME_MODE, mode).apply()
        _themeMode.value = mode
    }

    fun setAccentColor(color: String) {
        prefs.edit().putString(KEY_ACCENT_COLOR, color).apply()
        _accentColor.value = color
    }

    fun setCardStyle(style: String) {
        prefs.edit().putString(KEY_CARD_STYLE, style).apply()
        _cardStyle.value = style
    }

    fun setDeveloperMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DEVELOPER_MODE, enabled).apply()
        _isDeveloperMode.value = enabled
    }

    fun setVerifyTimeoutMs(timeoutMs: Long) {
        prefs.edit().putLong(KEY_VERIFY_TIMEOUT, timeoutMs).apply()
        _verifyTimeoutMs.value = timeoutMs
    }

    fun setSimulationMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SIMULATION_MODE, enabled).apply()
        _isSimulationMode.value = enabled
    }
}
