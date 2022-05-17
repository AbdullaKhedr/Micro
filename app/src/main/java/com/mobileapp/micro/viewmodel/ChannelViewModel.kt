package com.mobileapp.micro.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import com.mobileapp.micro.model.Channel
import com.mobileapp.micro.model.Lesson
import com.mobileapp.micro.model.MediaType
import com.mobileapp.micro.model.MemberOfChannels
import com.mobileapp.micro.repository.*
import kotlinx.coroutines.launch
import java.util.*

class ChannelViewModel(
    private val channelRepository: ChannelRepository = ChannelRepository(),
    private val lessonRepository: LessonRepository = LessonRepository(),
    private val storageRepository: StorageRepository = StorageRepository(),
    private val userRepository: UserRepository = UserRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    // To load all user's channels
    private var userChannelsIdsUpdateListener: ListenerRegistration? = null
    val userChannels = mutableStateListOf<Channel>()

    // To open one channel
    val currentChannelId = mutableStateOf("")

    private var currentChannelUpdateListener: ListenerRegistration? = null
    val currentChannel = mutableStateOf(Channel())
    private var currentChannelLessonsUpdateListener: ListenerRegistration? = null
    val currentChannelLessons = mutableStateListOf<Lesson>()

    // START: UI Related properties (lesson)
    val lessonTitle = mutableStateOf("")
    val lessonMediaName = mutableStateOf("")
    val lessonMediaUri = mutableStateOf("")
    val lessonContent = mutableStateOf("")
    val mediaType = mutableStateOf(MediaType.NON)
    var currentLessonToEdit: Lesson = Lesson()
    // END: UI Related properties (lesson)

    init {
        initUserChannels()
    }

    fun loadChannel() = viewModelScope.launch {
        currentChannelUpdateListener?.remove()
        try {
            val query = channelRepository.getChannelById(currentChannelId.value)
            currentChannelUpdateListener = query.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    println(">> Debug: Channel Update Listener failed. ${e.message}")
                    return@addSnapshotListener
                }
                val results = snapshot?.toObject(Channel::class.java)
                results?.let {
                    currentChannel.value = it
                    loadChannelLessons(currentChannel.value.lessonsIds)
                }
            }
        } catch (e: Exception) {
            Log.e("Channel", e.message.toString())
        }
    }

    fun loadChannelLessons(lessonsIds: List<String>) = viewModelScope.launch {
        //currentChannelLessonsUpdateListener?.remove()
        try {
            val query = lessonRepository.getChannelLessonsList(lessonsIds)
            currentChannelLessons.clear()
            query.forEach {
                it.addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        println(">> Debug: Channel Update Listener failed. ${e.message}")
                        return@addSnapshotListener
                    }
                    val results = snapshot?.toObject(Lesson::class.java)
                    results?.let { lesson ->
                        currentChannelLessons.remove(lesson)
                        currentChannelLessons.add(lesson)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Channel Lessons", e.message.toString())
        }
    }

    fun addLessonToChannel() = viewModelScope.launch {
        currentLessonToEdit.let {
            it.title = lessonTitle.value
            it.content = lessonContent.value
            if (it.lessonId.isEmpty()) it.mediaName = lessonMediaName.value
            it.onlineMediaUri = lessonMediaUri.value
            it.mediaType = mediaType.value
            it.createdAt = Date()
        }
        var result: DocumentReference? = null
        result = lessonRepository.addLesson(currentLessonToEdit)

        channelRepository.addLessonId(currentChannelId.value, result.id)
        println(">> Debug: Lesson @ID:${result.id} is add to channel @ID:$currentChannelId")

        if (currentLessonToEdit.onlineMediaUri.isNotEmpty() && currentLessonToEdit.mediaName.isNotEmpty()) {
            uploadLessonMedia(
                result.id,
                currentLessonToEdit.mediaType,
                currentLessonToEdit.mediaName,
                currentLessonToEdit.onlineMediaUri
            )
        }
        clearLessonEditorUiData()
    }

    private fun uploadLessonMedia(
        lessonId: String,
        mediaType: MediaType,
        mediaName: String,
        localUri: String
    ) = viewModelScope.launch {
        val onlineName = storageRepository.generateMediaFileName(mediaName, mediaType)
        val onlineUri = storageRepository.uploadMedia(
            storageRef = storageRepository.channelDisplayImagesStorageRef,
            fileName = onlineName,
            uri = localUri
        )
        lessonRepository.updateOnlineMediaUri(lessonId, onlineName, onlineUri)
    }

    fun cleanUiData() {
        currentChannelId.value = ""
        currentChannelUpdateListener = null
        currentChannel.value = Channel()
        currentChannelLessons.clear()
    }

    fun clearLessonEditorUiData() {
        lessonTitle.value = ""
        lessonContent.value = ""
        lessonMediaName.value = ""
        lessonMediaUri.value = ""
        mediaType.value = MediaType.NON
        // reset the object creator
        currentLessonToEdit = Lesson()
    }

    private fun initUserChannels() {
        userChannelsIdsUpdateListener?.remove()
        try {
            authRepository.getCurrentFirebaseUser()?.let { firebaseUser ->
                val listOfChannelIds = mutableStateListOf<MemberOfChannels>()
                val query = userRepository.getUserChannelsIds(firebaseUser.uid)
                userChannelsIdsUpdateListener = query.addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        println(">> Debug: Channels list Update Listener failed. ${e.message}")
                        return@addSnapshotListener
                    }
                    val idsResults = snapshot?.toObjects(MemberOfChannels::class.java)
                    listOfChannelIds.clear()
                    idsResults?.let {
                        listOfChannelIds.addAll(it)
                        if (listOfChannelIds.isNotEmpty()) {
                            userChannels.clear()
                            listOfChannelIds.forEach { userChannel ->
                                channelRepository.getChannelById(userChannel.channelId)
                                    .addSnapshotListener { snapshot, e ->
                                        if (e != null) {
                                            println(">> Debug: Channels list Update Listener failed. ${e.message}")
                                            return@addSnapshotListener
                                        }
                                        val channelsResults =
                                            snapshot?.toObject(Channel::class.java)
                                        channelsResults?.let { channel ->
                                            // remove the channel from the list if it was there,
                                            // in order to add the new one coming from the db
                                            userChannels.find { it.channelId == channel.channelId }
                                                ?.let { userChannels.remove(it) }
                                            userChannels.add(channel)
                                        }
                                    }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("User Channels List", e.message.toString())
        }
    }

    fun unsubscribeChannel(channelId: String) = viewModelScope.launch {
        try {
            authRepository.getCurrentFirebaseUser()?.let {
                userRepository.unsubscribeChannel(uid = it.uid, channelId = channelId)
            }
        } catch (e: Exception) {
            Log.e("Unsubscribe Channel", e.message.toString())
        }
    }
}