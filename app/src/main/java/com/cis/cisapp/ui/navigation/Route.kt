package com.cis.cisapp.ui.navigation

sealed class Route(val path: String){
    data object Login: Route("login")
    data object Home  : Route("home")
    data object Forgot: Route("forgot")
    data object RegisterPatient: Route("register_patient")
    data object RegisterPro: Route("register_pro")
}