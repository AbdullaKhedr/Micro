package com.mobileapp.micro.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.mobileapp.micro.model.User
import com.mobileapp.micro.repository.AuthRepository
import com.mobileapp.micro.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    sealed class AuthUiState {
        object Success : AuthUiState()
        object Loading : AuthUiState()
        object Empty : AuthUiState()
        data class Error(val message: String) : AuthUiState()
    }

    private val _authUiState = MutableStateFlow<AuthUiState>(AuthUiState.Empty)
    val authUiState: StateFlow<AuthUiState> = _authUiState

    // FIXME should stay here? or should add an update listener, so if update happened, the ui got updated also?!
    private val _currentUser = mutableStateOf<User?>(null)
    val currentUser: State<User?> = _currentUser

    private var _currentFirebaseUser: FirebaseUser? = authRepository.getCurrentFirebaseUser()

    init {
        authRepository.getCurrentAuthState().addAuthStateListener {
            _currentFirebaseUser = it.currentUser
        }
        val currentUserId = authRepository.getCurrentFirebaseUser()?.uid
        currentUserId?.let {
            viewModelScope.launch {
                _currentUser.value = userRepository.getUserById(it)
                println(">> Debug: USER : ==> ${_currentUser.value?.uid}")
            }
        }
    }

    fun signUp(newUser: User) = viewModelScope.launch {
        _authUiState.value = AuthUiState.Loading
        try {
            // This will signUp the user, and most importantly it will
            // return the user object with uid, so that i can add it
            // to the users collection under its uid as a document key
            val signedUser = authRepository.signUp(newUser)
            // after user is created, add the user to the users collection under it's uid
            userRepository.addUser(signedUser)
            _currentUser.value = signedUser
            _authUiState.value = AuthUiState.Success
        } catch (e: Exception) {
            _authUiState.value = AuthUiState.Error(e.message.toString())
        }
    }

    fun signIn(email: String, password: String) = viewModelScope.launch {
        _authUiState.value = AuthUiState.Loading
        try {
            val firebaseUser = authRepository.signIn(email, password)
            val user = firebaseUser.uid.let { userRepository.getUserById(it) }
            _currentUser.value = user
            // If the user verified his email, then update him in db
            updateVerifyState(user!!, firebaseUser.isEmailVerified)
            _authUiState.value = AuthUiState.Success
        } catch (e: Exception) {
            _authUiState.value = AuthUiState.Error(e.message.toString())
        }
    }

    fun resetPass(email: String) = viewModelScope.launch {
        _authUiState.value = AuthUiState.Loading
        try {
            authRepository.sendEmailResetPassword(email)
            _authUiState.value = AuthUiState.Success
            Log.d("Reset Password", "Email Sent.")
        } catch (e: Exception) {
            Log.e("Reset Password", e.message.toString())
            _authUiState.value = AuthUiState.Error(e.message.toString())
        }
    }

    fun signOut() = authRepository.signOut()

    fun getAuthState(): FirebaseUser? = _currentFirebaseUser

    private fun updateVerifyState(user: User, isEmailVerified: Boolean) = viewModelScope.launch {
        // If the user isVerified is not updated in the db, but he is really verified in the auth, so we update it
        if (!user.isVerified && isEmailVerified) {
            user.isVerified = true
            try {
                userRepository.updateUser(user)
            } catch (e: Exception) {

            }
        }
    }

}