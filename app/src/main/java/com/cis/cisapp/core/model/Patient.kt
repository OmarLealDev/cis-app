package com.cis.cisapp.core.model

data class Patient(
    override val uid: String = "",
    override val email: String,
    override val role: UserRole,
    val fullName: String = "",
    val phone: String = "",
    val dob: String = "",
    val gender: Gender = Gender.Unspecified
) : UserProfile
