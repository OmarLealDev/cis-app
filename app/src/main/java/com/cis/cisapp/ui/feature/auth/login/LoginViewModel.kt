package com.cis.cisapp.ui.feature.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.cis.cisapp.data.auth.AuthRepository
import com.cis.cisapp.data.auth.FirebaseAuthRepository
import com.cis.cisapp.core.Result

class LoginViewModel(
    private val repo: AuthRepository = FirebaseAuthRepository()
): ViewModel() {

    private var _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        // Observe Firebase auth state
        viewModelScope.launch {
            repo.authState().collect { logged ->
                _uiState.update { it.copy(isLoggedIn = logged, error = null, isLoading = false) }
            }
        }
    }

    fun onEmailChange(v: String)    = _uiState.update { it.copy(email = v,     error = null) }
    fun onPasswordChange(v: String) = _uiState.update { it.copy(password = v,  error = null) }
    fun togglePasswordVisibility()  = _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }

    fun signIn(onUid: (String) -> Unit = {}) =
        runAuth(block = { repo.signIn(_uiState.value.email, _uiState.value.password) },
            onSuccess = onUid)
    fun signUp(onUid: (String) -> Unit) =
        runAuth(block = { repo.signUp(_uiState.value.email, _uiState.value.password) },
            onSuccess = onUid)
    fun signOut() = runAuth(block = { repo.signOut() })

    private fun <T> runAuth(
        block: suspend () -> Result<T>,
        onSuccess: (T) -> Unit = {}
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val res = block()) {
                is Result.Success -> {
                    onSuccess(res.data)
                    _uiState.update { it.copy(isLoading = false, error = null) }
                }
                is Result.Error -> _uiState.update { it.copy(isLoading = false, error = res.message) }
                Result.Loading -> _uiState.update { it.copy(isLoading = true) }
                is Result.UserProfile<*> -> TODO()
            }
        }
    }
}