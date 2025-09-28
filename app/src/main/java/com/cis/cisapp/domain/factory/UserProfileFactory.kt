package com.cis.cisapp.domain.factory

import com.cis.cisapp.core.model.UserRole
import com.cis.cisapp.core.model.UserProfile
import com.cis.cisapp.core.model.Patient
import com.cis.cisapp.core.model.Gender
import com.cis.cisapp.core.model.Professional
import com.cis.cisapp.core.model.Discipline


object UserProfileFactory {
    fun createUserProfile(
        uid: String,
        email: String,
        role: UserRole,
        details: Map<String, Any>
    ) : UserProfile? {
        return when (role) {
            UserRole.PATIENT -> Patient(
                uid = uid,
                email = email,
                role = UserRole.PATIENT,
                fullName = details["fullName"] as? String ?: "",
                phone = details["phone"] as? String ?: "",
                dob = details["dob"] as? String ?: "",
                gender = details["gender"] as? Gender ?: Gender.Unspecified
            )
            UserRole.PROFESSIONAL -> Professional(
                uid = uid,
                email = email,
                role = UserRole.PROFESSIONAL,
                fullName = details["fullName"] as? String ?: "",
                licenseNumber = details["licenseNumber"] as? String ?: "",
                verified = details["verified"] as? Boolean ?: false,
                mainDiscipline = details["mainDiscipline"] as? Discipline
                    ?: Discipline.PSYCHOLOGY
            )
            UserRole.ADMIN -> TODO()
            UserRole.UNDEFINED -> null
        }
    }

    fun getUserCollection(role: UserRole): String {
        return when (role) {
            UserRole.PATIENT -> "patients"
            UserRole.PROFESSIONAL -> "professionals"
            UserRole.ADMIN -> "admins"
            UserRole.UNDEFINED -> "undefined"
        }
    }
}