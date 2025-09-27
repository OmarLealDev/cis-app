package com.cis.cisapp.core.model

data class Professional(
    override val uid: String = "",
    override val email: String,
    override val role: UserRole,
    val fullName: String = "",
    val licenseNumber: String = "",
    val verified: Boolean = false,
    val mainDiscipline: Discipline = Discipline.PSYCHOLOGY,
) : UserProfile
