package com.cis.cisapp.data.auth

import kotlinx.coroutines.flow.Flow
import com.cis.cisapp.core.Result

interface AuthRepository {
    fun authState(): Flow<Boolean>
    suspend fun signIn(email: String, password: String): Result<String>
    suspend fun signUp(email: String, password: String): Result<String>
    suspend fun signOut(): Result<Unit>
}
