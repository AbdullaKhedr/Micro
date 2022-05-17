package com.mobileapp.micro.repository

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mobileapp.micro.model.Channel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ChannelRepository {
    private val channelsCollectionRef by lazy { Firebase.firestore.collection("channels") }
    private val dispatchers = Dispatchers.IO

    suspend fun addChannel(channel: Channel): DocumentReference = withContext(dispatchers) {
        channelsCollectionRef.add(channel).await()
    }

    fun getChannelById(channelId: String): DocumentReference {
        return channelsCollectionRef.document(channelId)
    }

    suspend fun updateChannel(channel: Channel) = withContext(dispatchers) {
        channelsCollectionRef.document(channel.channelId).set(channel).await()
    }

    suspend fun addLessonId(channelId: String, lessonId: String) = withContext(dispatchers) {
        channelsCollectionRef.document(channelId)
            .update("lessonsIds", FieldValue.arrayUnion(lessonId)).await()
    }

    suspend fun deleteChannel(channelId: String) = withContext(dispatchers) {
        channelsCollectionRef.document(channelId).delete().await()
    }

    suspend fun incrementChannelMembersCount(channelId: String) =
        withContext(dispatchers) {
            channelsCollectionRef.document(channelId)
                .update("membersCount", FieldValue.increment(1)).await()
        }

    suspend fun updateOnlineImageUri(channelId: String, onlineName: String, onlineUri: String) =
        withContext(dispatchers) {
            channelsCollectionRef.document(channelId).update("onlineImageUri", onlineUri).await()
            channelsCollectionRef.document(channelId).update("displayImageName", onlineName).await()
        }
}