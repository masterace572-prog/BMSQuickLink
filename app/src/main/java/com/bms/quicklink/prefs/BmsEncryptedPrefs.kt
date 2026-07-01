package com.bms.quicklink.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BmsEncryptedPrefs(context: Context) {

    companion object {
        private const val SECURE_PREFS_NAME = "bms_secure_prefs"
        private const val KEY_IS_ONBOARDING_COMPLETE = "is_onboarding_complete"
    }

    private val masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        SECURE_PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val _isOnboardingComplete = MutableStateFlow(
        encryptedPrefs.getBoolean(KEY_IS_ONBOARDING_COMPLETE, false)
    )
    val isOnboardingComplete: StateFlow<Boolean> = _isOnboardingComplete

    fun setOnboardingComplete(complete: Boolean) {
        encryptedPrefs.edit().putBoolean(KEY_IS_ONBOARDING_COMPLETE, complete).apply()
        _isOnboardingComplete.value = complete
    }
}
