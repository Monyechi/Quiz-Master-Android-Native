package com.quizmaster.app.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quizmaster.app.data.repository.AuthRepository
import com.quizmaster.app.data.repository.AuthResult
import com.quizmaster.app.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: AuthRepository,
    private val session: SessionManager
) : ViewModel() {

    private val _authState = MutableLiveData<AuthUiState>()
    val authState: LiveData<AuthUiState> = _authState

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthUiState.Error("Email and password are required.")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthUiState.Loading
            when (val result = authRepo.login(email, password)) {
                is AuthResult.Success -> {
                    session.currentUserId = result.user.userId
                    session.currentUserRole = result.user.role
                    _authState.value = AuthUiState.Success(result.user.role)
                }
                is AuthResult.Error -> _authState.value = AuthUiState.Error(result.message)
            }
        }
    }

    fun register(email: String, password: String, confirmPassword: String, role: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthUiState.Error("All fields are required.")
            return
        }
        if (password != confirmPassword) {
            _authState.value = AuthUiState.Error("Passwords do not match.")
            return
        }
        if (password.length < 6) {
            _authState.value = AuthUiState.Error("Password must be at least 6 characters.")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthUiState.Loading
            when (val result = authRepo.register(email, password, role)) {
                is AuthResult.Success -> {
                    session.currentUserId = result.user.userId
                    session.currentUserRole = result.user.role
                    _authState.value = AuthUiState.Success(result.user.role)
                }
                is AuthResult.Error -> _authState.value = AuthUiState.Error(result.message)
            }
        }
    }
}

sealed class AuthUiState {
    object Loading : AuthUiState()
    data class Success(val role: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}
