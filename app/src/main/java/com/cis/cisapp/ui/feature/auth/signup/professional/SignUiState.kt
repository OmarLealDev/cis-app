package com.cis.cisapp.ui.feature.auth.signup.professional


import android.net.Uri
import androidx.compose.runtime.Immutable
import com.cis.cisapp.core.model.Gender
import com.cis.cisapp.core.model.Discipline

@Immutable
data class SignUpUiState(
    // Datos básicos
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",

    // Profesionales
    val licenseNumber: String = "",
    val discipline: Discipline? = null,

    // Extras
    val dob: String = "", // YYYY-MM-DD
    val gender: Gender = Gender.Unspecified,

    // Archivos
    val cvUri: Uri? = null,
    val cedulaUri: Uri? = null,

    // Flags UI
    val isPasswordVisible: Boolean = false,
    val isConfirmVisible: Boolean = false,
    val isLoading: Boolean = false,
    val acceptedTerms: Boolean = false,

    // Errores por campo
    val fullNameError: String = "",
    val emailError: String = "",
    val passwordError: String = "",
    val confirmPasswordError: String = "",
    val licenseError: String = "",
    val disciplineError: String = "",
    val dobError: String = "",
    val genderError: String = "",
    val termsError: String = "",

    // Mensajes
    val successMessage: String = "",
    val errorMessage: String = ""
) {
    val isFormValid: Boolean
        get() =
            fullName.isNotBlank() &&
                    email.isNotBlank() &&
                    password.isNotBlank() &&
                    confirmPassword.isNotBlank() &&
                    licenseNumber.isNotBlank() &&
                    discipline != null &&
                    dob.isNotBlank() &&
                    gender != Gender.Unspecified &&
                    acceptedTerms &&
                    fullNameError.isEmpty() &&
                    emailError.isEmpty() &&
                    passwordError.isEmpty() &&
                    confirmPasswordError.isEmpty() &&
                    licenseError.isEmpty() &&
                    disciplineError.isEmpty() &&
                    dobError.isEmpty() &&
                    genderError.isEmpty() &&
                    termsError.isEmpty()
}