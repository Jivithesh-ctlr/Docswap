package com.example.docswap.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.docswap.data.local.DocSwapDatabase
import com.example.docswap.data.local.SessionManager
import com.example.docswap.data.local.SettingsRepository
import com.example.docswap.repository.AuthRepository
import com.example.docswap.repository.ConversionRepository

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val database = DocSwapDatabase.getDatabase(context)
        val sessionManager = SessionManager(context)
        val settingsRepository = SettingsRepository(context)
        
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(AuthRepository(database.userDao(), sessionManager)) as T
            }
            modelClass.isAssignableFrom(ConversionViewModel::class.java) -> {
                ConversionViewModel(ConversionRepository(database.recentConversionDao())) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(settingsRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
