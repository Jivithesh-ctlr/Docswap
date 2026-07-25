package com.example.docswap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.docswap.data.local.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {
    val isDarkMode: StateFlow<Boolean?> = repository.darkModeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val autoDeleteEnabled: StateFlow<Boolean> = repository.autoDeleteEnabledFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val autoDeleteDays: StateFlow<Int> = repository.autoDeleteDaysFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 7
        )

    fun toggleTheme(isDark: Boolean) {
        viewModelScope.launch {
            repository.saveDarkMode(isDark)
        }
    }

    fun toggleAutoDelete(enabled: Boolean) {
        viewModelScope.launch {
            repository.saveAutoDeleteEnabled(enabled)
        }
    }

    fun setAutoDeleteDays(days: Int) {
        viewModelScope.launch {
            repository.saveAutoDeleteDays(days)
        }
    }
}
