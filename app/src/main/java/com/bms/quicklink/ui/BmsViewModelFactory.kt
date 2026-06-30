package com.bms.quicklink.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bms.quicklink.auth.AuthManager
import com.bms.quicklink.data.BmsRepository

class BmsViewModelFactory(
    private val repository: BmsRepository,
    private val authManager: AuthManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BmsViewModel::class.java)) {
            return BmsViewModel(repository, authManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
