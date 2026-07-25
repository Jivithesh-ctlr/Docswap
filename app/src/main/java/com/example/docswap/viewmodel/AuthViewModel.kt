package com.example.docswap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.docswap.repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _newPassword = MutableStateFlow("")
    val newPassword: StateFlow<String> = _newPassword.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<AuthNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        _name.value = authRepository.getUserName()
        _email.value = authRepository.getUserEmail()
    }

    fun onEmailChange(email: String) {
        _email.value = email
    }

    fun onPasswordChange(password: String) {
        _password.value = password
    }

    fun onNameChange(name: String) {
        _name.value = name
    }

    fun onNewPasswordChange(password: String) {
        _newPassword.value = password
    }

    fun login() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            authRepository.login(_email.value, _password.value)
                .onSuccess {
                    _name.value = authRepository.getUserName()
                    _navigationEvent.emit(AuthNavigationEvent.NavigateToMain)
                }
                .onFailure {
                    _error.value = it.message ?: "Login failed"
                }
            _isLoading.value = false
        }
    }

    fun signup() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            authRepository.signup(_name.value, _email.value, _password.value)
                .onSuccess {
                    _name.value = authRepository.getUserName()
                    _navigationEvent.emit(AuthNavigationEvent.NavigateToMain)
                }
                .onFailure {
                    _error.value = it.message ?: "Signup failed"
                }
            _isLoading.value = false
        }
    }

    fun logout() {
        authRepository.logout()
        viewModelScope.launch {
            _navigationEvent.emit(AuthNavigationEvent.NavigateToLogin)
        }
    }

    fun updateProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            authRepository.updateProfile(_name.value, _email.value, _newPassword.value)
                .onSuccess {
                    _newPassword.value = ""
                }
                .onFailure {
                    _error.value = it.message ?: "Update failed"
                }
            _isLoading.value = false
        }
    }

    fun isLoggedIn(): Boolean {
        return authRepository.isLoggedIn()
    }

    fun isFirstLaunch(): Boolean {
        return authRepository.isFirstLaunch()
    }

    fun setFirstLaunchComplete() {
        authRepository.setFirstLaunchComplete()
    }

    sealed class AuthNavigationEvent {
        object NavigateToMain : AuthNavigationEvent()
        object NavigateToLogin : AuthNavigationEvent()
    }
}
