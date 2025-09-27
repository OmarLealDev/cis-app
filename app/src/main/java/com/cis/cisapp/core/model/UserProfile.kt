package com.cis.cisapp.core.model

sealed interface UserProfile {

    val uid: String
    val email: String
    val role: UserRole
}