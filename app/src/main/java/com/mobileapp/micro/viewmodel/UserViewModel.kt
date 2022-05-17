package com.mobileapp.micro.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.mobileapp.micro.model.Lesson
import com.mobileapp.micro.model.MediaType
import com.mobileapp.micro.model.User
import com.mobileapp.micro.model.UserFriend
import com.mobileapp.micro.repository.AuthRepository
import com.mobileapp.micro.repository.StorageRepository
import com.mobileapp.micro.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val userRepository: UserRepository = UserRepository(),
    private val storageRepository: StorageRepository = StorageRepository(),
) : ViewModel() {

    // START: Profile UI related Properties
    val firstName = mutableStateOf("")
    val lastName = mutableStateOf("")
    val bio = mutableStateOf("")
    val photoName = mutableStateOf("")
    val localPhotoUri = mutableStateOf("")
    var currentUser = User()
    private var oldUserToUpdate = User()
    // END: Profile UI related Properties

    private var userFriendsUpdateListener: ListenerRegistration? = null
    val userFriendEmails = mutableStateListOf<UserFriend>()

    private var userLearningListUpdateListener: ListenerRegistration? = null
    val userLearningList = mutableStateListOf<Lesson>()

    init {
        authRepository.getCurrentFirebaseUser()?.let {
            viewModelScope.launch {
                userRepository.getUserById(it.uid) // what this for?
                loadFriends(it.uid)
                loadLearningList(it.uid)
            }
        }
    }

    private fun loadFriends(uid: String) = viewModelScope.launch {
        userFriendsUpdateListener?.remove()
        try {
            val query = userRepository.getUserFriendEmails(uid)
            userFriendsUpdateListener = query.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    println(">> Debug: Friends Update Listener failed. ${e.message}")
                    return@addSnapshotListener
                }
                val results = snapshot?.toObjects(UserFriend::class.java)
                userFriendEmails.clear()
                results?.let {
                    userFriendEmails.addAll(it)
                }
            }
        } catch (e: Exception) {
            Log.e("Channel", e.message.toString())
        }
    }

    private fun loadLearningList(uid: String) = viewModelScope.launch {
        userLearningListUpdateListener?.remove()
        try {
            val query = userRepository.getUserLearningList(uid)
            userLearningListUpdateListener = query.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    println(">> Debug: Learning List Update Listener failed. ${e.message}")
                    return@addSnapshotListener
                }
                val results = snapshot?.toObjects(Lesson::class.java)
                userLearningList.clear()
                results?.let {
                    userLearningList.addAll(it)
                }
            }
        } catch (e: Exception) {
            Log.e("Learning List", e.message.toString())
        }
    }

    fun addFriend(email: String) = viewModelScope.launch {
        try {
            authRepository.getCurrentFirebaseUser()?.let {
                userRepository.addFriendEmail(
                    email = email,
                    uid = it.uid
                )
                Log.d("Friend", "Friend added")
            } ?: throw Exception("Can't authenticate the user")
        } catch (e: Exception) {
            Log.e("Friend", e.message.toString())
        }
    }

    fun addLessonToLearningList(lesson: Lesson) = viewModelScope.launch {
        try {
            authRepository.getCurrentFirebaseUser()?.let {
                userRepository.lessonToLearningList(
                    uid = it.uid,
                    lesson = lesson
                )
                Log.d("Learning List", "Lesson added")
            } ?: throw Exception("Can't authenticate the user")
        } catch (e: Exception) {
            Log.e("Learning List", e.message.toString())
        }
    }

    fun updateUser() = viewModelScope.launch {
        try {
            currentUser.let { user ->
                user.firstName = firstName.value
                user.lastName = lastName.value
                user.bio = bio.value
                user.profilePhotoName = photoName.value
                user.localPhotoUri = localPhotoUri.value

                if (user.localPhotoUri.isNotEmpty()) {
                    if (oldUserToUpdate.localPhotoUri != user.localPhotoUri) {
                        Log.d("Profile Photo", "Started uploading")
                        updateUserImage(
                            uid = user.uid,
                            imageName = photoName.value,
                            localImageUri = user.localPhotoUri,
                            newImage = oldUserToUpdate.profilePhotoName == "",
                        )
                    }
                }
                userRepository.updateUser(user)
            }
            clearUiData()
        } catch (e: Exception) {
        }
    }

    private fun updateUserImage(
        uid: String,
        imageName: String,
        localImageUri: String,
        newImage: Boolean
    ) = viewModelScope.launch {
        try {
            val onlineName = if (newImage)
                storageRepository.generateMediaFileName(imageName, MediaType.IMAGE)
            else imageName
            val onlineUri = storageRepository.uploadMedia(
                storageRef = storageRepository.userProfileImagesStorageRef,
                fileName = onlineName,
                uri = localImageUri
            )
            userRepository.updateProfileImageOnlineUri(
                uid = uid,
                onlineName = onlineName,
                onlineUri = onlineUri
            )
            Log.d("Profile Photo", "Photo Uploaded")
        } catch (e: Exception) {
            Log.e("Profile Photo", e.message.toString())
        }
    }

    private fun clearUiData() {
        firstName.value = ""
        lastName.value = ""
        bio.value = ""
        localPhotoUri.value = ""
        photoName.value = ""
        currentUser = User()
        oldUserToUpdate = User()
    }

    fun removeFriend(friend: UserFriend) = viewModelScope.launch {
        authRepository.getCurrentFirebaseUser()?.let {
            userRepository.removeFriend(it.uid, friend)
        }
    }

    fun sendMessageToApp(message: String) = viewModelScope.launch {
        authRepository.getCurrentFirebaseUser()?.let {
            userRepository.sendMessageToApp(it.uid, message)
        }
    }

    fun fillEditUserUiData(user: User) {
        currentUser = user
        oldUserToUpdate = user.copy()
        firstName.value = user.firstName
        lastName.value = user.lastName
        bio.value = user.bio
        localPhotoUri.value = user.localPhotoUri
        photoName.value = user.profilePhotoName
    }
}
