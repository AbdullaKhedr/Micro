package com.mobileapp.micro.viewmodel.create

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobileapp.micro.model.MediaType
import com.mobileapp.micro.model.StudyGroup
import com.mobileapp.micro.repository.AuthRepository
import com.mobileapp.micro.repository.StorageRepository
import com.mobileapp.micro.repository.StudyGroupRepository
import com.mobileapp.micro.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class CreateStudyGroupViewModel(
    private val studyGroupRepository: StudyGroupRepository = StudyGroupRepository(),
    private val storageRepository: StorageRepository = StorageRepository(),
    private val userRepository: UserRepository = UserRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    // UI State section
    sealed class CreateStudyGroupUiState {
        data class Success(val createdGroupId: String?) : CreateStudyGroupUiState()
        object Loading : CreateStudyGroupUiState()
        object Empty : CreateStudyGroupUiState()
        data class Error(val message: String) : CreateStudyGroupUiState()
    }

    private val _createStudyGroupUiState =
        MutableStateFlow<CreateStudyGroupUiState>(CreateStudyGroupUiState.Empty)
    val createStudyGroupUiState: StateFlow<CreateStudyGroupUiState> = _createStudyGroupUiState

    // START: UI Related properties
    val groupName = mutableStateOf("")
    val groupDescription = mutableStateOf("")
    val displayImageName = mutableStateOf("")
    val localImageUri = mutableStateOf("")
    val groupMemberEmails = mutableStateListOf<String>()
    private var oldGroupToBeUpdated = StudyGroup()
    private var currentGroup = StudyGroup()
    // END: UI Related properties

    fun createStudyGroup() = viewModelScope.launch {
        try {
            _createStudyGroupUiState.value = CreateStudyGroupUiState.Loading
            authRepository.getCurrentFirebaseUser()?.let { firebaseUser ->
                currentGroup.let { group ->
                    group.authorId = firebaseUser.uid
                    group.groupName = groupName.value
                    group.groupDescription = groupDescription.value
                    group.displayImageName = displayImageName.value
                    group.localImageUri = localImageUri.value
                    group.membersEmails.clear()
                    group.membersEmails.addAll(groupMemberEmails)
                    firebaseUser.email?.let { group.membersEmails.add(it) }

                    if (group.groupId.isEmpty()) { // create new group
                        group.createdAt = Date()
                        val docRef = studyGroupRepository.addStudyGroup(group)
                        _createStudyGroupUiState.value = CreateStudyGroupUiState.Success(docRef.id)
                        if (group.localImageUri.isNotEmpty()) {
                            updateGroupImage(
                                groupId = docRef.id,
                                imageName = group.displayImageName,
                                localImageUri = group.localImageUri,
                                newImage = true,
                            )
                        }
                        addMembers(docRef.id, group.membersEmails)
                    } else {
                        group.updatedAt = Date()
                        studyGroupRepository.updatesStudyGroup(group)
                        _createStudyGroupUiState.value = CreateStudyGroupUiState.Success(null)
                        if (oldGroupToBeUpdated.displayImageName != group.displayImageName && group.localImageUri.isNotEmpty()) {
                            updateGroupImage(
                                groupId = group.groupId,
                                imageName = group.displayImageName,
                                localImageUri = group.localImageUri,
                                newImage = oldGroupToBeUpdated.displayImageName == "" // if the old channel doesn't have image, then it's like new one
                            )
                        }
                        addMembers(group.groupId, group.membersEmails)
                    }
                }
                clearUIData()
            } ?: throw Exception("Can't authenticate the user")
        } catch (e: Exception) {
            _createStudyGroupUiState.value = CreateStudyGroupUiState.Error(e.message.toString())
            Log.e("Study Group", e.message.toString())
        }
    }

    private fun updateGroupImage(
        groupId: String,
        imageName: String,
        localImageUri: String,
        newImage: Boolean
    ) = viewModelScope.launch {
        val onlineName = if (newImage)
            storageRepository.generateMediaFileName(imageName, MediaType.IMAGE)
        else imageName
        val onlineUri = storageRepository.uploadMedia(
            storageRef = storageRepository.studyGroupDisplayImagesStorageRef,
            fileName = onlineName,
            uri = localImageUri
        )
        studyGroupRepository.updateOnlineImageUri(
            groupId = groupId,
            onlineName = onlineName,
            onlineUri = onlineUri
        )
    }

    private fun addMembers(groupId: String, emails: List<String>) = viewModelScope.launch {
        emails.forEach {
            addGroupMember(groupId, it)
        }
    }

    private fun addGroupMember(groupId: String, userEmail: String) = viewModelScope.launch {
        try {
            val isAdded = userRepository.addUserGroup(groupId = groupId, userEmail = userEmail)
            if (isAdded) studyGroupRepository.incrementStudyGroupMembersCount(groupId = groupId)
        } catch (e: Exception) {

        }
    }

    fun fillGroupUiData(group: StudyGroup) {
        oldGroupToBeUpdated = group
        currentGroup = group
        groupName.value = group.groupName
        groupDescription.value = group.groupDescription
        displayImageName.value = group.displayImageName
        localImageUri.value = group.localImageUri
        groupMemberEmails.addAll(group.membersEmails)
    }

    fun clearUIData() {
        oldGroupToBeUpdated = StudyGroup()
        currentGroup = StudyGroup()
        groupName.value = ""
        groupDescription.value = ""
        displayImageName.value = ""
        localImageUri.value = ""
        groupMemberEmails.clear()
    }
}