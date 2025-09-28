package com.cis.cisapp.ui.feature.auth.login

data class LoginUiState (
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null
)