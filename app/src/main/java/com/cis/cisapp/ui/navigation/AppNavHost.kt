package com.cis.cisapp.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.cis.cisapp.ui.feature.auth.login.LoginScreen
import com.cis.cisapp.ui.feature.auth.signup.patient.PatientSignUpScreen
import com.cis.cisapp.ui.feature.auth.signup.professional.ProfessionalSignUpScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNav() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Route.Login.path) {
        composable(Route.Login.path) {
            LoginScreen(
                onLoginSuccess = { nav.navigate(Route.Home.path) { popUpTo(0) } },
                onForgot = { nav.navigate(Route.Forgot.path) },
                onRegisterPatient = { nav.navigate(Route.RegisterPatient.path) },
                onRegisterPro = { nav.navigate(Route.RegisterPro.path) }
            )
        }
        composable(Route.Home.path) { Text("Home") }
        composable(Route.Forgot.path) { Text("Recuperar contraseña") }
        composable(Route.RegisterPatient.path) {
            PatientSignUpScreen(
                onBack = { nav.navigate(Route.Login.path) { popUpTo(0) } },
                onNavigateToLogin = { nav.navigate(Route.Login.path) { popUpTo(0) } }
            )
        }
        composable(Route.RegisterPro.path) {
            ProfessionalSignUpScreen(
                onBack = { nav.navigate(Route.Login.path) { popUpTo(0) } },
                onNavigateToLogin = { nav.navigate(Route.Login.path) { popUpTo(0) } }
            )
        }
    }
}