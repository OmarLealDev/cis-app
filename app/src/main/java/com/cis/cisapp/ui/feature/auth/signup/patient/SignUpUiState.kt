package com.cis.cisapp.ui.feature.auth.signup.patient


import com.cis.cisapp.core.model.Gender
import androidx.compose.runtime.Immutable

@Immutable
data class SignUpUiState(
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val dob: String = "", // YYYY-MM-DD
    val gender: Gender = Gender.Unspecified,
    val password: String = "",
    val confirmPassword: String = "",
    val acceptedTerms: Boolean = false,

    // UI flags
    val isPasswordVisible: Boolean = false,
    val isConfirmVisible: Boolean = false,
    val isLoading: Boolean = false,

    // Success and error messages
    val successMessage: String = "",
    val errorMessage: String = "",

    // Validation messages (vacío = sin error)
    val fullNameError: String = "",
    val emailError: String = "",
    val phoneError: String = "",
    val dobError: String = "",
    val genderError: String = "",
    val passwordError: String = "",
    val confirmPasswordError: String = "",
    val termsError: String = ""
) {
    val isFormValid: Boolean
        get() = fullNameError.isEmpty()
                && emailError.isEmpty()
                && phoneError.isEmpty()
                && dobError.isEmpty()
                && genderError.isEmpty()
                && passwordError.isEmpty()
                && confirmPasswordError.isEmpty()
                && termsError.isEmpty()
                && fullName.isNotBlank()
                && email.isNotBlank()
                && phone.length == 10 // exactamente 10 dígitos
                && dob.isNotBlank()
                && gender != Gender.Unspecified
                && password.isNotBlank()
                && confirmPassword.isNotBlank()
                && acceptedTerms
}