package com.cis.cisapp.data.userprofile

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.cis.cisapp.core.model.UserProfile
import com.cis.cisapp.domain.factory.UserProfileFactory
import com.cis.cisapp.core.Result
import com.cis.cisapp.core.model.UserRole

class FirestoreUserProfileRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : UserProfileRepository {

    override suspend fun createUserProfile(userProfile: UserProfile): Result<String> = try {
        val collectionPath = UserProfileFactory.getUserCollection(userProfile.role)
        firestore.collection(collectionPath)
            .document(userProfile.uid)
            .set(userProfile)
            .await()
        Result.Success(userProfile.uid)
    } catch (e: Exception) {
        Result.Error(e.message ?: "Failed to create user profile", e)
    }

    override suspend fun getUserProfile(uid: String, role: UserRole): Result<UserProfile> = try {
        val collectionPath = UserProfileFactory.getUserCollection(role)
        val documentSnapshot = firestore.collection(collectionPath)
            .document(uid)
            .get()
            .await()
        val userProfile: UserProfile? = documentSnapshot.toObject(UserProfile::class.java)

        if (userProfile != null) {
            Result.UserProfile(userProfile)
        } else {
            Result.Error("User profile not found for UID: $uid")
        }
    } catch (e: Exception) {
        Result.Error(e.message ?: "Failed to get user profile", e)
    }
}