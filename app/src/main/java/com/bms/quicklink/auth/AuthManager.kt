package com.bms.quicklink.auth

import com.bms.quicklink.db.BmsDatabaseHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest

sealed class AuthState {
    object Loading : AuthState()
    object SetupRequired : AuthState()
    object Locked : AuthState()
    object Authenticated : AuthState()
}

class AuthManager(private val dbHelper: BmsDatabaseHelper) {

    private val authScope = CoroutineScope(Dispatchers.Default)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        authScope.launch {
            _isLoading.value = true
            val pinHash = dbHelper.getPinHash()
            if (pinHash == null) {
                _authState.value = AuthState.SetupRequired
            } else {
                _authState.value = AuthState.Locked
            }
            _isLoading.value = false
        }
    }

    fun setupPin(pin: String) {
        if (pin.length < 4) {
            _errorMessage.value = "PIN must be at least 4 digits"
            return
        }
        authScope.launch {
            _isLoading.value = true
            val hashed = hashPin(pin)
            val success = dbHelper.savePinHash(hashed)
            if (success) {
                _errorMessage.value = null
                _authState.value = AuthState.Authenticated
            } else {
                _errorMessage.value = "Failed to save PIN. Please try again."
            }
            _isLoading.value = false
        }
    }

    fun verifyPin(pin: String) {
        authScope.launch {
            _isLoading.value = true
            val storedHash = dbHelper.getPinHash()
            if (storedHash == null) {
                _authState.value = AuthState.SetupRequired
                _isLoading.value = false
                return@launch
            }

            val enteredHash = hashPin(pin)
            if (enteredHash == storedHash) {
                _errorMessage.value = null
                _authState.value = AuthState.Authenticated
            } else {
                _errorMessage.value = "Incorrect PIN"
            }
            _isLoading.value = false
        }
    }

    fun logout() {
        authScope.launch {
            if (dbHelper.getPinHash() != null) {
                _authState.value = AuthState.Locked
            } else {
                _authState.value = AuthState.SetupRequired
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    private suspend fun hashPin(pin: String): String = withContext(Dispatchers.Default) {
        val bytes = MessageDigest.getInstance("SHA-256").digest(pin.toByteArray())
        bytes.joinToString("") { "%02x".format(it) }
    }
}
