package com.cis.cisapp.data.auth


import  com.cis.cisapp.core.Result
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : AuthRepository{

    override fun authState(): Flow<Boolean> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser != null)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun signIn(email: String, password: String): Result<String> = try {
        val authResult = auth.signInWithEmailAndPassword(email.trim(), password).await()
        val uid = authResult.user?.uid ?: auth.currentUser?.uid
        ?: return Result.Error("No se pudo obtener el UID después de signIn")
        Result.Success(uid)
    } catch (t: Throwable) {
        Result.Error(t.message ?: "Sign-in failed", t)
    }

    override suspend fun signUp(email: String, password: String): Result<String> = try {
        val authResult = auth.createUserWithEmailAndPassword(email.trim(), password).await()
        val uid = authResult.user?.uid ?: auth.currentUser?.uid
        ?: throw IllegalStateException("UID nulo después de signUp")
        Result.Success(uid)
    } catch (t: Throwable) {
        Result.Error(t.message ?: "Sign-up failed", t)
    }

    override suspend fun signOut(): Result<Unit> = try {
        auth.signOut()
        Result.Success(Unit)
    } catch (t: Throwable) {
        Result.Error(t.message ?: "Sign-out failed", t)
    }
}