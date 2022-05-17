package com.mobileapp.micro.repository

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mobileapp.micro.model.GroupMessage
import com.mobileapp.micro.model.MemberOfStudyGroup
import com.mobileapp.micro.model.StudyGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class StudyGroupRepository {

    private val studyGroupsCollectionRef by lazy { Firebase.firestore.collection("study-groups") }
    private val studyGroupMessagesCollectionRef by lazy { Firebase.firestore.collection("group-messages") }
    private val dispatchers = Dispatchers.IO

    suspend fun addStudyGroup(group: StudyGroup): DocumentReference = withContext(dispatchers) {
        studyGroupsCollectionRef.add(group).await()
    }

    suspend fun updatesStudyGroup(group: StudyGroup) = withContext(dispatchers) {
        studyGroupsCollectionRef.document(group.groupId).set(group).await()
    }

    suspend fun addGroupMessage(groupMessage: GroupMessage): DocumentReference =
        withContext(dispatchers) {
            studyGroupMessagesCollectionRef.document(groupMessage.groupId)
                .collection("messages")
                .add(groupMessage)
                .await()
        }

    suspend fun getStudyGroupById(groupId: String): StudyGroup? = withContext(dispatchers) {
        studyGroupsCollectionRef.document(groupId).get().await()
            .toObject(StudyGroup::class.java)
    }

    fun getStudyGroupsList(groupIdsList: List<MemberOfStudyGroup>): MutableList<DocumentReference> {
        val ids = groupIdsList.map { it.groupId }
        val result: MutableList<DocumentReference> = mutableListOf()
        ids.forEach {
            result.add(studyGroupsCollectionRef.document(it))
        }
        return result
        //return studyGroupsCollectionRef.whereIn(FieldPath.documentId(), ids)
        //.orderBy("lastMessage.createdAt", Query.Direction.DESCENDING)
    }

    suspend fun getStudyGroupsByAuthorId(authorId: String): StudyGroup? =
        withContext(dispatchers) {
            studyGroupsCollectionRef.document(authorId).get().await()
                .toObject(StudyGroup::class.java)
        }

    suspend fun incrementStudyGroupMembersCount(groupId: String) =
        withContext(dispatchers) {
            studyGroupsCollectionRef.document(groupId)
                .update("membersCount", FieldValue.increment(1)).await()
        }

    suspend fun updateOnlineImageUri(groupId: String, onlineName: String, onlineUri: String) =
        withContext(dispatchers) {
            studyGroupsCollectionRef.document(groupId).update("onlineImageUri", onlineUri).await()
            studyGroupsCollectionRef.document(groupId).update("displayImageName", onlineName).await()
        }

    fun getGroupMessages(groupId: String): Query {
        return studyGroupMessagesCollectionRef.document(groupId)
            .collection("messages")
            .orderBy("createdAt", Query.Direction.ASCENDING)
    }

}