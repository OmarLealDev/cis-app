package com.cis.cisapp.data.userprofile


import com.cis.cisapp.core.Result
import com.cis.cisapp.core.model.UserRole
import com.cis.cisapp.core.model.UserProfile

interface UserProfileRepository {
    suspend fun createUserProfile(userProfile: UserProfile): Result<String>
    suspend fun getUserProfile(uid: String, role: UserRole): Result<UserProfile>
}