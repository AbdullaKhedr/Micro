package com.mobileapp.micro.viewmodel.create

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobileapp.micro.model.Channel
import com.mobileapp.micro.model.MediaType
import com.mobileapp.micro.repository.AuthRepository
import com.mobileapp.micro.repository.ChannelRepository
import com.mobileapp.micro.repository.StorageRepository
import com.mobileapp.micro.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class CreateChannelViewModel(
    private val channelRepository: ChannelRepository = ChannelRepository(),
    private val storageRepository: StorageRepository = StorageRepository(),
    private val userRepository: UserRepository = UserRepository(),
    private val authRepository: AuthRepository = AuthRepository(),
) : ViewModel() {

    sealed class CreateChannelUiState {
        data class Success(val createdChannelId: String?) : CreateChannelUiState()
        object Loading : CreateChannelUiState()
        object Empty : CreateChannelUiState()
        data class Error(val message: String) : CreateChannelUiState()
    }

    private val _createChannelUiState =
        MutableStateFlow<CreateChannelUiState>(CreateChannelUiState.Empty)
    val createChannelUiState: StateFlow<CreateChannelUiState> = _createChannelUiState

    // START: UI Related properties
    val channelName = mutableStateOf("")
    val channelDescription = mutableStateOf("")
    val displayImageName = mutableStateOf("")
    val localImageUri = mutableStateOf("")
    val channelMemberEmails = mutableStateListOf<String>()
    private var oldChannelToBeUpdated = Channel()
    private var currentChannel = Channel()
    // END: UI Related properties

    fun createChannel() = viewModelScope.launch {
        try {
            _createChannelUiState.value = CreateChannelUiState.Loading
            authRepository.getCurrentFirebaseUser()?.let { firebaseUser ->
                currentChannel.let { channel ->
                    channel.authorId = firebaseUser.uid
                    channel.channelName = channelName.value
                    channel.channelDescription = channelDescription.value
                    channel.displayImageName = displayImageName.value
                    channel.localImageUri = localImageUri.value
                    channel.membersEmails.clear()
                    channel.membersEmails.addAll(channelMemberEmails)
                    firebaseUser.email?.let { channel.membersEmails.add(it) }

                    if (channel.channelId.isEmpty()) { // create new channel
                        channel.createdAt = Date()
                        val docRef = channelRepository.addChannel(channel)
                        _createChannelUiState.value = CreateChannelUiState.Success(docRef.id)
                        if (channel.localImageUri.isNotEmpty()) {
                            updateChannelImage(
                                channelId = docRef.id,
                                imageName = channel.displayImageName,
                                localImageUri = channel.localImageUri,
                                true
                            )
                        }
                        addMembers(docRef.id, channel.membersEmails)
                    } else { // update channel
                        channel.updatedAt = Date()
                        channelRepository.updateChannel(channel)
                        _createChannelUiState.value = CreateChannelUiState.Success(null)
                        if (oldChannelToBeUpdated.displayImageName != channel.displayImageName && channel.localImageUri.isNotEmpty()) {
                            updateChannelImage(
                                channelId = channel.channelId,
                                imageName = channel.displayImageName,
                                localImageUri = channel.localImageUri,
                                newImage = oldChannelToBeUpdated.displayImageName == "" // if the old channel doesn't have image, then it's like new one
                            )
                        }
                        addMembers(channel.channelId, channel.membersEmails)
                    }

                }
                clearChannelUiData()
            } ?: throw Exception("Can't authenticate the user")
        } catch (e: Exception) {
            _createChannelUiState.value = CreateChannelUiState.Error(e.message.toString())
            Log.e("Channel", e.message.toString())
        }
    }

    private fun updateChannelImage(
        channelId: String,
        imageName: String,
        localImageUri: String,
        newImage: Boolean
    ) = viewModelScope.launch {
        try {
            val onlineName = if (newImage)
                storageRepository.generateMediaFileName(imageName, MediaType.IMAGE)
            else imageName
            val onlineUri = storageRepository.uploadMedia(
                storageRef = storageRepository.channelDisplayImagesStorageRef,
                fileName = onlineName,
                uri = localImageUri
            )
            channelRepository.updateOnlineImageUri(
                channelId = channelId,
                onlineName = onlineName,
                onlineUri = onlineUri
            )
        } catch (e: Exception) {

        }
    }

    private fun addMembers(channelId: String, emails: List<String>) = viewModelScope.launch {
        emails.forEach {
            addChannelMember(channelId, it)
        }
    }

    private fun addChannelMember(channelId: String, userEmail: String) = viewModelScope.launch {
        try {
            val isAdded = userRepository.addUserChannel(channelId = channelId, userEmail = userEmail)
            if (isAdded) channelRepository.incrementChannelMembersCount(channelId = channelId)
        } catch (e: Exception) {

        }
    }

    fun fillChannelUiData(channel: Channel) {
        oldChannelToBeUpdated = channel
        currentChannel = channel
        channelName.value = channel.channelName
        channelDescription.value = channel.channelDescription
        displayImageName.value = channel.displayImageName
        localImageUri.value = channel.localImageUri
        channelMemberEmails.addAll(channel.membersEmails)
    }

    fun clearChannelUiData() {
        channelName.value = ""
        channelDescription.value = ""
        displayImageName.value = ""
        localImageUri.value = ""
        channelMemberEmails.clear()
    }
}
