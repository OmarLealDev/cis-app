package com.cis.cisapp.ui.feature.auth.signup.professional

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.LocalDate
import java.time.ZoneId
import com.cis.cisapp.core.model.Gender
import com.cis.cisapp.core.model.Discipline
import com.cis.cisapp.ui.theme.AppTypography

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalSignUpScreen(
    modifier: Modifier = Modifier,
    vm: SignUpViewModel = viewModel(), // tu VM para profesionales
    onBack: () -> Unit = {},
    onSignInClick: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    val focus = LocalFocusManager.current
    val context = LocalContext.current

    var genderMenuExpanded by remember { mutableStateOf(false) }
    var disciplineMenuExpanded by remember { mutableStateOf(false) }
    var showDobPicker by remember { mutableStateOf(false) }

    // Pickers de documentos (PDF)
    val cvPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            // conservar permiso persistente
            context.contentResolver.takePersistableUriPermission(
                it, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            vm.onCvPicked(it)
        }
    }

    val cedulaPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            vm.onCedulaPicked(it)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                title = { Text("Crear cuenta", style = AppTypography.titleLarge, color = MaterialTheme.colorScheme.primary) }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Nombre completo
            OutlinedTextField(
                value = state.fullName,
                onValueChange = vm::onFullNameChange,
                label = { Text("Nombre completo") },
                leadingIcon = { Icon(Icons.Filled.Person, null) },
                isError = state.fullNameError.isNotEmpty(),
                supportingText = { if (state.fullNameError.isNotEmpty()) Text(state.fullNameError) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            // Email
            OutlinedTextField(
                value = state.email,
                onValueChange = vm::onEmailChange,
                label = { Text("Correo electrónico") },
                leadingIcon = { Icon(Icons.Filled.Email, null) },
                isError = state.emailError.isNotEmpty(),
                supportingText = { if (state.emailError.isNotEmpty()) Text(state.emailError) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            // Contraseña
            OutlinedTextField(
                value = state.password,
                onValueChange = vm::onPasswordChange,
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Filled.Lock, null) },
                trailingIcon = {
                    IconButton(onClick = vm::onTogglePasswordVisibility) {
                        Icon(
                            imageVector = if (state.isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = null
                        )
                    }
                },
                visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = state.passwordError.isNotEmpty(),
                supportingText = { if (state.passwordError.isNotEmpty()) Text(state.passwordError) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                )
            )

            // Confirmar contraseña
            OutlinedTextField(
                value = state.confirmPassword,
                onValueChange = vm::onConfirmPasswordChange,
                label = { Text("Confirmar contraseña") },
                leadingIcon = { Icon(Icons.Filled.Lock, null) },
                trailingIcon = {
                    IconButton(onClick = vm::onToggleConfirmVisibility) {
                        Icon(
                            imageVector = if (state.isConfirmVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = null
                        )
                    }
                },
                visualTransformation = if (state.isConfirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = state.confirmPasswordError.isNotEmpty(),
                supportingText = { if (state.confirmPasswordError.isNotEmpty()) Text(state.confirmPasswordError) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onDone = { focus.clearFocus() })
            )

            // Cédula profesional (número)
            OutlinedTextField(
                value = state.licenseNumber,
                onValueChange = vm::onLicenseChange,
                label = { Text("Cédula") },
                leadingIcon = { Icon(Icons.Filled.Badge, null) },
                isError = state.licenseError.isNotEmpty(),
                supportingText = { if (state.licenseError.isNotEmpty()) Text(state.licenseError) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )

            // Fecha de nacimiento
            OutlinedTextField(
                value = state.dob,
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha de nacimiento") },
                leadingIcon = {
                    IconButton(onClick = { showDobPicker = true }) {
                        Icon(Icons.Filled.CalendarMonth, contentDescription = "Seleccionar fecha")
                    }
                },
                isError = state.dobError.isNotEmpty(),
                supportingText = { if (state.dobError.isNotEmpty()) Text(state.dobError) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDobPicker = true }
            )

            if (showDobPicker) {
                val initialMillis = remember(state.dob) {
                    try {
                        if (state.dob.isNotBlank()) {
                            val d = LocalDate.parse(state.dob)
                            d.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                        } else null
                    } catch (_: Exception) { null }
                }
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = initialMillis,
                    yearRange = 1900..LocalDate.now().year
                )
                DatePickerDialog(
                    onDismissRequest = { showDobPicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                datePickerState.selectedDateMillis?.let { vm.onDobSelected(it) }
                                showDobPicker = false
                            },
                            enabled = datePickerState.selectedDateMillis != null
                        ) { Text("Aceptar") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDobPicker = false }) { Text("Cancelar") }
                    }
                ) {
                    DatePicker(
                        state = datePickerState,
                        showModeToggle = false
                    )
                }
            }

            // Género (igual a tu pantalla paciente)
            ExposedDropdownMenuBox(
                expanded = genderMenuExpanded,
                onExpandedChange = { genderMenuExpanded = !genderMenuExpanded },
            ) {
                OutlinedTextField(
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    readOnly = true,
                    value = when (state.gender) {
                        Gender.Male -> "Masculino"
                        Gender.Female -> "Femenino"
                        Gender.Other -> "Otro"
                        Gender.Unspecified -> ""
                    },
                    onValueChange = {},
                    label = { Text("Género") },
                    isError = state.genderError.isNotEmpty(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderMenuExpanded) },
                    supportingText = { if (state.genderError.isNotEmpty()) Text(state.genderError) }
                )
                ExposedDropdownMenu(
                    expanded = genderMenuExpanded,
                    onDismissRequest = { genderMenuExpanded = false }
                ) {
                    DropdownMenuItem(text = { Text("Masculino") }, onClick = {
                        vm.onGenderChange(Gender.Male); genderMenuExpanded = false
                    })
                    DropdownMenuItem(text = { Text("Femenino") }, onClick = {
                        vm.onGenderChange(Gender.Female); genderMenuExpanded = false
                    })
                    DropdownMenuItem(text = { Text("Otro") }, onClick = {
                        vm.onGenderChange(Gender.Other); genderMenuExpanded = false
                    })
                }
            }

            // ⬇️ DISCIPLINA (nuevo combo, igual estilo que género)
            ExposedDropdownMenuBox(
                expanded = disciplineMenuExpanded,
                onExpandedChange = { disciplineMenuExpanded = !disciplineMenuExpanded },
            ) {
                OutlinedTextField(
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    readOnly = true,
                    value = state.discipline?.let { it.name.replace('_', ' ') } ?: "",
                    onValueChange = {},
                    label = { Text("Especialidad") },
                    isError = state.disciplineError.isNotEmpty(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = disciplineMenuExpanded) },
                    supportingText = { if (state.disciplineError.isNotEmpty()) Text(state.disciplineError) }
                )
                ExposedDropdownMenu(
                    expanded = disciplineMenuExpanded,
                    onDismissRequest = { disciplineMenuExpanded = false }
                ) {
                    // Si tu enum tiene `entries`, puedes recorrerlo:
                    Discipline.entries.forEach { d ->
                        DropdownMenuItem(
                            text = { Text(d.name.replace('_', ' ')) },
                            onClick = {
                                vm.onDisciplineChange(d)
                                disciplineMenuExpanded = false
                            }
                        )
                    }
                }
            }

            // 📄 Cargar Curriculum Vitae (PDF)
            FileInputRow(
                title = "Curriculum vitae.pdf",
                selected = state.cvUri != null,
                onClick = { cvPicker.launch(arrayOf("application/pdf")) }
            )

            // 🪪 Cargar Cédula profesional (PDF)
            FileInputRow(
                title = "Cedula profesional.pdf",
                selected = state.cedulaUri != null,
                onClick = { cedulaPicker.launch(arrayOf("application/pdf")) }
            )

            // Términos
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Checkbox(checked = state.acceptedTerms, onCheckedChange = { vm.onAcceptTermsChange(it) })
                Spacer(Modifier.width(8.dp))
                Text("Acepto la política de privacidad y los términos del servicio", style = AppTypography.bodyMedium)
            }
            if (state.termsError.isNotEmpty()) {
                Text(
                    text = state.termsError,
                    color = MaterialTheme.colorScheme.error,
                    style = AppTypography.labelSmall
                )
            }

            // Mensajes
            if (state.successMessage.isNotEmpty()) {
                AssistChip(onClick = {}, label = { Text(state.successMessage) }, leadingIcon = {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null)
                })
            }
            if (state.errorMessage.isNotEmpty()) {
                AssistChip(onClick = {}, label = { Text(state.errorMessage) }, leadingIcon = {
                    Icon(Icons.Filled.Error, contentDescription = null)
                })
            }

            // Botón registrar
            Button(
                onClick = {
                    vm.submit(
                        onSuccess = onNavigateToLogin,
                        onError = { /* ya lo muestras en UI */ }
                    )
                },
                enabled = !state.isLoading && state.isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
                } else {
                    Text("Registrarse")
                }
            }

            // Ir a iniciar sesión
            TextButton(
                onClick = onSignInClick,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) { Text("¿Ya tienes cuenta? Inicia sesión") }
        }
    }
}

/** Fila visual para los inputs de archivo con iconos y estado seleccionado */
@Composable
private fun FileInputRow(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        tonalElevation = if (selected) 2.dp else 0.dp,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Description,
                contentDescription = null
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.width(10.dp))
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = if (selected) Icons.Filled.CheckCircle else Icons.Filled.CloudUpload,
                    contentDescription = null,
                    tint = if (selected) MaterialTheme.colorScheme.primary else LocalContentColor.current
                )
            }
        }
    }
}