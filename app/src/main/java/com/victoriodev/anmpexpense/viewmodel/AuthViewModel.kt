package com.victoriodev.anmpexpense.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.victoriodev.anmpexpense.util.AuthRepository
import com.victoriodev.anmpexpense.util.LoginResult
import com.victoriodev.anmpexpense.util.RegisterResult
import com.victoriodev.anmpexpense.util.UserSession
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository(application)
    private val userSession = UserSession(application)

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _registerResult = MutableLiveData<RegisterResult>()
    val registerResult: LiveData<RegisterResult> = _registerResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _loginResult.value = LoginResult.Error("Username and password cannot be empty")
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            val result = authRepository.loginUser(username, password)
            _loginResult.value = result

            if (result is LoginResult.Success) {
                userSession.saveUserSession(
                    result.user.userId,
                    result.user.username,
                    result.user.firstname,
                    result.user.lastname
                )
            }
            _isLoading.value = false
        }
    }

    fun register(username: String, firstname: String, lastname: String, password: String, repeatPassword: String) {
        // Validation
        when {
            username.isBlank() -> {
                _registerResult.value = RegisterResult.Error("Username cannot be empty")
                return
            }
            firstname.isBlank() -> {
                _registerResult.value = RegisterResult.Error("First name cannot be empty")
                return
            }
            lastname.isBlank() -> {
                _registerResult.value = RegisterResult.Error("Last name cannot be empty")
                return
            }
            password.isBlank() -> {
                _registerResult.value = RegisterResult.Error("Password cannot be empty")
                return
            }
            password != repeatPassword -> {
                _registerResult.value = RegisterResult.Error("Passwords do not match")
                return
            }
        }

        _isLoading.value = true
        viewModelScope.launch {
            val result = authRepository.registerUser(username, firstname, lastname, password)
            _registerResult.value = result
            _isLoading.value = false
        }
    }

    fun checkSession(): Boolean {
        return userSession.hasValidSession()
    }

    fun clearResults() {
        _loginResult.value = null
        _registerResult.value = null
    }
}