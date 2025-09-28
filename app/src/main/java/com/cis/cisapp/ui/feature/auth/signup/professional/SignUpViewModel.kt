package com.cis.cisapp.ui.feature.auth.signup.professional

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import com.cis.cisapp.data.auth.AuthRepository
import com.cis.cisapp.data.auth.FirebaseAuthRepository
import com.cis.cisapp.core.Result
import com.cis.cisapp.data.userprofile.UserProfileRepository
import com.cis.cisapp.data.userprofile.FirestoreUserProfileRepository
import com.cis.cisapp.core.model.Gender
import com.cis.cisapp.core.model.UserRole
import com.cis.cisapp.domain.factory.UserProfileFactory
import com.cis.cisapp.core.model.Discipline

class SignUpViewModel(
    private val authRepository: AuthRepository = FirebaseAuthRepository(),
    private val firestoreRepository: UserProfileRepository = FirestoreUserProfileRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState

    // -------- Inputs --------
    fun onFullNameChange(v: String) = _uiState.update {
        it.copy(fullName = v, fullNameError = validateName(v))
    }

    fun onEmailChange(v: String) = _uiState.update {
        it.copy(email = v.trim(), emailError = validateEmail(v))
    }

    fun onPasswordChange(v: String) = _uiState.update {
        it.copy(
            password = v,
            passwordError = validatePassword(v),
            confirmPasswordError = validateConfirm(v, it.confirmPassword)
        )
    }

    fun onConfirmPasswordChange(v: String) = _uiState.update {
        it.copy(
            confirmPassword = v,
            confirmPasswordError = validateConfirm(it.password, v)
        )
    }

    fun onLicenseChange(v: String) = _uiState.update {
        it.copy(licenseNumber = v.trim(), licenseError = validateLicense(v))
    }

    fun onGenderChange(g: Gender) = _uiState.update {
        it.copy(gender = g, genderError = if (g == Gender.Unspecified) "Selecciona un género" else "")
    }

    fun onDisciplineChange(d: Discipline) = _uiState.update {
        it.copy(discipline = d, disciplineError = "")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onDobSelected(selectedMillis: Long) = _uiState.update {
        val localDate = Instant.ofEpochMilli(selectedMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        val formatted = localDate.toString() // YYYY-MM-DD
        it.copy(dob = formatted, dobError = validateDob(formatted))
    }

    fun onCvPicked(uri: Uri) = _uiState.update { it.copy(cvUri = uri) }
    fun onCedulaPicked(uri: Uri) = _uiState.update { it.copy(cedulaUri = uri) }

    fun onTogglePasswordVisibility() = _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    fun onToggleConfirmVisibility()  = _uiState.update { it.copy(isConfirmVisible = !it.isConfirmVisible) }

    fun onAcceptTermsChange(v: Boolean) = _uiState.update {
        it.copy(acceptedTerms = v, termsError = if (!v) "Debes aceptar los términos" else "")
    }

    fun clearMessages() = _uiState.update { it.copy(successMessage = "", errorMessage = "") }

    // -------- Submit --------
    @RequiresApi(Build.VERSION_CODES.O)
    fun submit(onSuccess: () -> Unit, onError: (String) -> Unit) {
        // limpia mensajes
        _uiState.update { it.copy(successMessage = "", errorMessage = "") }

        // forzar validaciones antes de enviar
        _uiState.update { s ->
            s.copy(
                fullNameError = validateName(s.fullName),
                emailError = validateEmail(s.email),
                passwordError = validatePassword(s.password),
                confirmPasswordError = validateConfirm(s.password, s.confirmPassword),
                licenseError = validateLicense(s.licenseNumber),
                disciplineError = if (s.discipline == null) "Selecciona una especialidad" else "",
                dobError = validateDob(s.dob),
                genderError = if (s.gender == Gender.Unspecified) "Selecciona un género" else "",
                termsError = if (!s.acceptedTerms) "Debes aceptar los términos" else ""
            )
        }

        val current = _uiState.value
        if (!current.isFormValid) {
            val msg = "Revisa los campos marcados."
            _uiState.update { it.copy(errorMessage = msg) }
            onError(msg)
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Crear usuario en Auth
                when (val res = authRepository.signUp(current.email, current.password)) {
                    is Result.Success<String> -> {
                        val uid = res.data

                        // Construir perfil Professional
                        val user = UserProfileFactory.createUserProfile(
                            uid = uid,
                            email = current.email.trim(),
                            role = UserRole.PROFESSIONAL,
                            details = mapOf(
                                "fullName"        to current.fullName.trim(),
                                "dob"             to current.dob,
                                "gender"          to current.gender.name,
                                "licenseNumber"   to current.licenseNumber.trim(),
                                "mainDiscipline"  to (current.discipline?.name ?: ""),
                                // para subir a storage reemplazar por URI
                                "cvUri"           to (current.cvUri?.toString() ?: ""),
                                "cedulaUri"       to (current.cedulaUri?.toString() ?: "")
                            )
                        ) ?: run {
                            val m = "No se pudo construir el perfil de usuario"
                            _uiState.update { it.copy(errorMessage = m) }
                            onError(m); return@launch
                        }

                        // Guardar en Firestore
                        when (val save = firestoreRepository.createUserProfile(user)) {
                            is Result.Success<*> -> {
                                _uiState.update { it.copy(successMessage = "¡Cuenta creada exitosamente! Redirigiendo...") }
                                delay(1200)
                                onSuccess()
                            }
                            is Result.Error -> {
                                _uiState.update { it.copy(errorMessage = save.message) }
                                onError(save.message)
                            }
                            is Result.Loading -> Unit
                            is Result.UserProfile<*> -> TODO()
                        }
                    }
                    is Result.Error -> {
                        _uiState.update { it.copy(errorMessage = res.message) }
                        onError(res.message)
                    }
                    is Result.Loading -> Unit
                    is Result.UserProfile<*> -> TODO()
                }
            } catch (e: Exception) {
                val m = e.message ?: "Error inesperado"
                _uiState.update { it.copy(errorMessage = m) }
                onError(m)
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // -------- Validaciones --------
    private fun validateName(name: String): String =
        when {
            name.isBlank()     -> "Ingresa tu nombre completo"
            name.length < 3    -> "Nombre demasiado corto"
            else               -> ""
        }

    private fun validateEmail(email: String): String {
        val regex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
        return when {
            email.isBlank()        -> "Ingresa tu correo"
            !regex.matches(email)  -> "Correo inválido"
            else                   -> ""
        }
    }

    private fun validatePassword(pw: String): String =
        when {
            pw.isBlank()                    -> "Ingresa una contraseña"
            pw.length < 8                   -> "Mínimo 8 caracteres"
            !pw.any { it.isDigit() } ||
                    !pw.any { it.isLetter() }       -> "Debe incluir letras y números"
            else                            -> ""
        }

    private fun validateConfirm(pw: String, confirm: String): String =
        when {
            confirm.isBlank()   -> "Confirma la contraseña"
            pw != confirm       -> "Las contraseñas no coinciden"
            else                -> ""
        }

    private fun validateLicense(lic: String): String =
        when {
            lic.isBlank()         -> "Ingresa tu cédula"
            lic.length < 5        -> "Cédula inválida"
            else                  -> ""
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun validateDob(dob: String): String {
        if (dob.isBlank()) return "Ingresa tu fecha de nacimiento"
        return try {
            val d = LocalDate.parse(dob)
            if (d.isAfter(LocalDate.now())) "La fecha no puede ser futura" else ""
        } catch (_: Exception) {
            "Usa el formato AAAA-MM-DD"
        }
    }
}