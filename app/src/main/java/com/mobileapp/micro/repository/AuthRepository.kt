package com.mobileapp.micro.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.mobileapp.micro.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthRepository {

    private val auth = Firebase.auth
    private val dispatchers = Dispatchers.IO

    suspend fun signUp(user: User): User = withContext(dispatchers) {
        val authResult =
            auth.createUserWithEmailAndPassword(user.email, user.password).await()

        authResult.user?.let {
            val userProfileChangeRequest = userProfileChangeRequest {
                displayName = user.displayName
                photoUri = Uri.parse(user.localPhotoUri)
            }
            // Add displayName and photoUri to the user
            // Unfortunately it does not allow adding custom attribute such as role
            it.updateProfile(userProfileChangeRequest).await()

            // You may send the user a link to confirm their email address
            it.sendEmailVerification().await()
            // If needed, add further user details to Firestore
            user.uid = it.uid
            user.isVerified = it.isEmailVerified
            println(">> Debug: signUp.user.uid : ${user.uid}")
            user// This will be returned
        } ?: throw Exception("SignUp failed")
        // We force this part to throw Exception if it dose not done correctly! (user is not created)
        // But other parts are throwing Exception by default.
    }

    suspend fun signIn(email: String, password: String): FirebaseUser = withContext(dispatchers) {
        val authResult = auth.signInWithEmailAndPassword(email, password).await()
        // This will be returned
        authResult.user ?: throw Exception("Sign In failed")
    }

    suspend fun sendEmailResetPassword(email: String) = withContext(dispatchers) {
        auth.sendPasswordResetEmail(email).await()
    }

    fun getCurrentFirebaseUser(): FirebaseUser? = auth.currentUser

    fun getCurrentAuthState(): FirebaseAuth = auth

    fun signOut() = auth.signOut()
}