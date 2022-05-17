package com.mobileapp.micro.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.mobileapp.micro.model.GroupMessage
import com.mobileapp.micro.model.MemberOfStudyGroup
import com.mobileapp.micro.model.MessageType
import com.mobileapp.micro.model.StudyGroup
import com.mobileapp.micro.repository.AuthRepository
import com.mobileapp.micro.repository.StudyGroupRepository
import com.mobileapp.micro.repository.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class StudyGroupViewModel(
    private val studyGroupRepository: StudyGroupRepository = StudyGroupRepository(),
    private val userRepository: UserRepository = UserRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    // UI State section
    sealed class StudyGroupUiState {
        object Success : StudyGroupUiState()
        object Loading : StudyGroupUiState()
        object Empty : StudyGroupUiState()
        data class Error(val message: String) : StudyGroupUiState()
    }

    private val _studyGroupUiState = MutableStateFlow<StudyGroupUiState>(StudyGroupUiState.Empty)
    val studyGroupUiState: StateFlow<StudyGroupUiState> = _studyGroupUiState

    // To load all user's groups
    val userGroups = mutableStateListOf<StudyGroup>()
    private var currentUserGroupsIdsUpdateListener: ListenerRegistration? = null

    // Once a group is opened (Current StudyGroup)
    val currentStudyGroupId = mutableStateOf("")
    val currentStudyGroup = mutableStateOf(StudyGroup())
    private var currentGroupMessagesUpdateListener: ListenerRegistration? = null
    val currentGroupMessages = mutableStateListOf<GroupMessage>()
    val currentMessageText = mutableStateOf("")
    private val messageToSend = mutableStateOf(GroupMessage())

    // In case sharing lessons from course to Study Group
    var lessonTitleToShar: String = ""
    var lessonIdToShar: String = ""
    val isSharingLesson = mutableStateOf(false)

    init {
        getUserGroupsList()
    }

    private fun getUserGroupsList() = viewModelScope.launch {
        _studyGroupUiState.value = StudyGroupUiState.Loading
        try {
            val firebaseUser = authRepository.getCurrentFirebaseUser()
            if (firebaseUser != null) {
                val idsToInitMessagesHashMap = mutableListOf<String>()
                // Get all study groups ids related to the user
                currentUserGroupsIdsUpdateListener?.remove()
                val userGroupsIDsQuery =
                    userRepository.getStudyGroupsIdsWhereUserMember(firebaseUser.uid)
                currentUserGroupsIdsUpdateListener =
                    userGroupsIDsQuery.addSnapshotListener { snapshot1, e1 ->
                        if (e1 != null) {
                            println(">> Debug: User's Study Group Ids list Update Listener failed. ${e1.message}")
                            return@addSnapshotListener
                        }
                        val groupIds = snapshot1?.toObjects(MemberOfStudyGroup::class.java)
                        if (groupIds != null) {
                            idsToInitMessagesHashMap.addAll(groupIds.toList().map { it.groupId })
                        }
                        // Get all study groups using the ids
                        if (!groupIds.isNullOrEmpty()) {
                            val results = studyGroupRepository.getStudyGroupsList(groupIds)
                            println(">> Debug: User is a member in ${results.size} Study groups")
                            userGroups.clear()
                            results.forEach { documentReference ->
                                documentReference.addSnapshotListener { snapshot2, e2 ->
                                    if (e2 != null) {
                                        println(">> Debug: User's Study Group Update Listener failed. ${e2.message}")
                                        return@addSnapshotListener
                                    }
                                    val userGroupsResults =
                                        snapshot2?.toObject(StudyGroup::class.java)
                                    userGroupsResults?.let { studyGroup ->
                                        // remove the group from the list if it was there,
                                        // in order to add the new one coming from the db
                                        userGroups.find { it.groupId == userGroupsResults.groupId }
                                            ?.let { userGroups.remove(it) }
                                        userGroups.add(studyGroup)
                                    }
                                }
                            }
                        }
                    }
                // (can't make it as firebase query)
                // Sort the groups once based on the last sent messages,
                // then whenever change happens will automatically be at the top
                println("=================Sorting Start==============")
                delay(1000) // the (userGroups) still didn't filled, so the filtering wasn't happening
                userGroups.forEach { println(it.groupName) }
                userGroups.sortBy {//FIXME not working
                    it.lastMessage?.createdAt
                }
                println("=================Sorting Done==============")
            }
            _studyGroupUiState.value = StudyGroupUiState.Success
        } catch (e: Exception) {
            _studyGroupUiState.value = StudyGroupUiState.Error(e.message.toString())
        }
    }

    fun loadCurrentStudyGroup() = viewModelScope.launch {
        _studyGroupUiState.value = StudyGroupUiState.Loading
        try {
            val group = studyGroupRepository.getStudyGroupById(currentStudyGroupId.value)
            if (group != null) {
                currentStudyGroup.value = group
                println(">> Debug: Group object is loaded")
            }
            // This must be here...!!!,
            // to make sure that loadStudyGroup() finishes before sharing the lesson.
            if (isSharingLesson.value) {
                sendGroupMessage()
            }
            _studyGroupUiState.value = StudyGroupUiState.Success
        } catch (e: Exception) {
            _studyGroupUiState.value = StudyGroupUiState.Error(e.message.toString())
        }
    }

    fun loadCurrentGroupMessages() {
        currentGroupMessagesUpdateListener?.remove()
        val query = studyGroupRepository.getGroupMessages(currentStudyGroupId.value)
        currentGroupMessagesUpdateListener = query.addSnapshotListener { snapshot, e ->
            if (e != null) {
                println(">> Debug: Group Messages Update Listener failed. ${e.message}")
                return@addSnapshotListener
            }
            println(">> Debug: Start loading group messages")
            val results = snapshot?.toObjects(GroupMessage::class.java)
            currentGroupMessages.clear()
            results?.let {
                authRepository.getCurrentFirebaseUser()?.let { firebaseUser ->
                    it.forEach { message ->
                        if (firebaseUser.uid == message.authorId)
                            message.isMine = true
                    }
                }
                currentGroupMessages.addAll(it)
                println(">> Debug: Current Group has ${currentGroupMessages.size} messages")
            }
        }
    }

    fun sendGroupMessage() = viewModelScope.launch {
        try {
            if (currentStudyGroupId.value.isNotEmpty()) {
                authRepository.getCurrentFirebaseUser()?.let { firebaseUser ->
                    messageToSend.value.let {
                        it.groupId = currentStudyGroup.value.groupId
                        it.authorId = firebaseUser.uid
                        it.authorName = firebaseUser.displayName.toString()
                        it.createdAt = Date()
                        it.text = currentMessageText.value
                        if (isSharingLesson.value) {
                            it.type = MessageType.LESSON
                            it.lessonTitle = lessonTitleToShar
                            it.lessonId = lessonIdToShar
                            it.text = "Shared Lesson"
                        }
                    }
                    // FIXME: problem when sending too much messages after each other,
                    //  the last message object wont be updated correctly
                    studyGroupRepository.addGroupMessage(messageToSend.value)
                    currentStudyGroup.value.lastMessage = messageToSend.value
                    studyGroupRepository.updatesStudyGroup(currentStudyGroup.value)
                    onSentMessageClean()
                }
            }
        } catch (e: Exception) {

        }
    }

    fun onStudyGroupClosedClean() {
        currentStudyGroupId.value = ""
        currentStudyGroup.value = StudyGroup()
        currentGroupMessages.clear()
        currentMessageText.value = ""
        messageToSend.value = GroupMessage()
    }

    private fun onSentMessageClean() {
        messageToSend.value = GroupMessage()
        currentMessageText.value = ""
        // In case the message was lesson
        isSharingLesson.value = false
        lessonTitleToShar = ""
        lessonIdToShar = ""
    }

    fun leaveGroup(groupId: String) = viewModelScope.launch {
        try {
            authRepository.getCurrentFirebaseUser()?.let {
                userRepository.leaveGroup(uid = it.uid, groupId = groupId)
            }
        } catch (e: Exception) {
            Log.e("Leave Group", e.message.toString())
        }
    }
}
