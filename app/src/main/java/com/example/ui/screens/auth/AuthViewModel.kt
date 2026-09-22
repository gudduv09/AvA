package com.example.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.AuthRepository
import com.example.data.repository.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AuthTab {
    LOGIN,
    REGISTER
}

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    val authState: StateFlow<AuthState> = authRepository.authState

    private val _currentTab = MutableStateFlow(AuthTab.LOGIN)
    val currentTab: StateFlow<AuthTab> = _currentTab.asStateFlow()

    private val _identifier = MutableStateFlow("ava_creator")
    val identifier: StateFlow<String> = _identifier.asStateFlow()

    private val _email = MutableStateFlow("creator@ava.mbs.com")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _displayName = MutableStateFlow("AVA Creator")
    val displayName: StateFlow<String> = _displayName.asStateFlow()

    private val _password = MutableStateFlow("vibe2026")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _isPasswordVisible = MutableStateFlow(false)
    val isPasswordVisible: StateFlow<Boolean> = _isPasswordVisible.asStateFlow()

    private val _uiErrorMessage = MutableStateFlow<String?>(null)
    val uiErrorMessage: StateFlow<String?> = _uiErrorMessage.asStateFlow()

    fun setTab(tab: AuthTab) {
        _currentTab.value = tab
        _uiErrorMessage.value = null
    }

    fun onIdentifierChanged(value: String) {
        _identifier.value = value
        _uiErrorMessage.value = null
    }

    fun onEmailChanged(value: String) {
        _email.value = value
        _uiErrorMessage.value = null
    }

    fun onDisplayNameChanged(value: String) {
        _displayName.value = value
        _uiErrorMessage.value = null
    }

    fun onPasswordChanged(value: String) {
        _password.value = value
        _uiErrorMessage.value = null
    }

    fun togglePasswordVisibility() {
        _isPasswordVisible.value = !_isPasswordVisible.value
    }

    fun quickFillAccount(username: String, pass: String, name: String = "", mail: String = "") {
        _identifier.value = username
        _password.value = pass
        if (name.isNotBlank()) _displayName.value = name
        if (mail.isNotBlank()) _email.value = mail
        _uiErrorMessage.value = null
    }

    fun submit(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiErrorMessage.value = null
            if (_currentTab.value == AuthTab.LOGIN) {
                if (_identifier.value.isBlank()) {
                    _uiErrorMessage.value = "Please enter your email or username"
                    return@launch
                }
                if (_password.value.isBlank()) {
                    _uiErrorMessage.value = "Please enter your password"
                    return@launch
                }
                val result = authRepository.login(_identifier.value, _password.value)
                if (result.isSuccess) {
                    onSuccess()
                } else {
                    _uiErrorMessage.value = result.exceptionOrNull()?.message ?: "Login failed"
                }
            } else {
                if (_email.value.isBlank() || !_email.value.contains("@")) {
                    _uiErrorMessage.value = "Please enter a valid email address"
                    return@launch
                }
                if (_identifier.value.isBlank()) {
                    _uiErrorMessage.value = "Please choose a username"
                    return@launch
                }
                if (_password.value.length < 6) {
                    _uiErrorMessage.value = "Password must be at least 6 characters"
                    return@launch
                }
                val result = authRepository.register(
                    email = _email.value,
                    username = _identifier.value,
                    displayName = _displayName.value.ifBlank { _identifier.value },
                    password = _password.value
                )
                if (result.isSuccess) {
                    onSuccess()
                } else {
                    _uiErrorMessage.value = result.exceptionOrNull()?.message ?: "Registration failed"
                }
            }
        }
    }

    fun loginWithSocial(provider: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiErrorMessage.value = null
            val result = authRepository.socialLogin(provider)
            if (result.isSuccess) {
                onSuccess()
            } else {
                _uiErrorMessage.value = result.exceptionOrNull()?.message ?: "Social sign-in failed"
            }
        }
    }

    fun continueAsGuest(onSuccess: () -> Unit) {
        authRepository.continueAsGuest()
        onSuccess()
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
