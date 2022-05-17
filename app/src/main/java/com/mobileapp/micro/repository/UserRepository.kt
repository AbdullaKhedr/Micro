package com.mobileapp.micro.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mobileapp.micro.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class UserRepository {

    private val userCollectionRef by lazy { Firebase.firestore.collection("users") }
    private val userMessagesCollectionRef by lazy { Firebase.firestore.collection("users-messages") }
    private val dispatchers = Dispatchers.IO

    suspend fun addUser(user: User) = withContext(dispatchers) {
        userCollectionRef.document(user.uid).set(user).await()
    }

    suspend fun updateUser(user: User) = withContext(dispatchers) {
        userCollectionRef.document(user.uid).set(user).await()
    }

    fun getCurrentUserDocRef(uid: String): DocumentReference {
        return userCollectionRef.document(uid)
    }

    suspend fun getUserById(uid: String): User? = withContext(dispatchers) {
        userCollectionRef.document(uid).get().await().toObject(User::class.java)
    }

    suspend fun addFriendEmail(uid: String, email: String) = withContext(dispatchers) {
        userCollectionRef.document(uid).collection("friends").add(UserFriend(friendEmail = email))
    }

    suspend fun removeFriend(uid: String, friend: UserFriend) = withContext(dispatchers) {
        userCollectionRef.document(uid).collection("friends").document(friend.id).delete()
    }

    fun getUserFriendEmails(uid: String): CollectionReference {
        return userCollectionRef.document(uid).collection("friends")
    }

    suspend fun addUserStudyGroup(groupId: String, userId: String): DocumentReference =
        withContext(dispatchers) {
            userCollectionRef.document(userId)
                .collection("study-group-member")
                .add(MemberOfStudyGroup(groupId = groupId, createdAt = Date()))
                .await()
        }

    suspend fun addUserChannel(channelId: String, userEmail: String) =
        withContext(dispatchers) {
            val userByEmail = userCollectionRef
                .whereEqualTo("email", userEmail).get().await().toObjects(User::class.java)
            if (userByEmail.isNotEmpty()) {
                val user = userByEmail[0]
                val query = userCollectionRef.document(user.uid)
                    .collection("channel-member")
                    .whereEqualTo("channelId", channelId).get().await()
                if (query.isEmpty) {
                    userCollectionRef.document(user.uid)
                        .collection("channel-member")
                        .add(MemberOfChannels(channelId = channelId, createdAt = Date()))
                    true
                } else false
            } else false
        }

    suspend fun addUserGroup(groupId: String, userEmail: String) =
        withContext(dispatchers) {
            val userByEmail = userCollectionRef
                .whereEqualTo("email", userEmail).get().await().toObjects(User::class.java)
            if (userByEmail.isNotEmpty()) {
                val user = userByEmail[0]
                val query = userCollectionRef.document(user.uid)
                    .collection("group-member")
                    .whereEqualTo("groupId", groupId).get().await()
                if (query.isEmpty) {
                    userCollectionRef.document(user.uid)
                        .collection("group-member")
                        .add(MemberOfStudyGroup(groupId = groupId, createdAt = Date()))
                    true
                } else false
            } else false
        }

    fun getStudyGroupsIdsWhereUserMember(userId: String): CollectionReference {
        return userCollectionRef.document(userId).collection("group-member")
    }

    suspend fun addUserStudyingCourse(courseId: String, userId: String) = withContext(dispatchers) {
        val isStudied = isUserStudiedCourse(courseId = courseId, uid = userId)
        if (!isStudied) {
            userCollectionRef.document(userId)
                .collection("course-study")
                .add(CourseStudy(courseId = courseId, createdAt = Date()))
                .await()
        }
    }

    suspend fun removeUserStudyingCourse(uid: String, courseId: String) = withContext(dispatchers) {
        userCollectionRef.document(uid)
            .collection("course-study")
            .whereEqualTo("courseId", courseId).get().addOnSuccessListener {
                it.forEach {
                    it.reference.delete()
                }
            }
    }

    fun getUserStudyingCoursesIds(uid: String): CollectionReference {
        return userCollectionRef.document(uid).collection("course-study")
    }

    fun getUserChannelsIds(uid: String): CollectionReference {
        return userCollectionRef.document(uid).collection("channel-member")
    }

    // Check if a user studied a course
    suspend fun isUserStudiedCourse(uid: String, courseId: String): Boolean =
        withContext(dispatchers) {
            val course = getUserStudyingCoursesIds(uid).get().await()
                .toObjects(CourseStudy::class.java).find {
                    it.courseId == courseId
                }
            course != null
        }

    suspend fun updateProfileImageOnlineUri(uid: String, onlineName: String, onlineUri: String) =
        withContext(dispatchers) {
            userCollectionRef.document(uid).update("onlinePhotoUri", onlineUri).await()
            userCollectionRef.document(uid).update("profilePhotoName", onlineName).await()
        }

    suspend fun sendMessageToApp(uid: String, message: String) = withContext(dispatchers) {
        userMessagesCollectionRef.add(
            hashMapOf(
                "uid" to uid,
                "message" to message
            )
        )
    }

    suspend fun lessonToLearningList(uid: String, lesson: Lesson) = withContext(dispatchers) {
        userCollectionRef.document(uid).collection("learning-list").add(lesson)
    }

    fun getUserLearningList(uid: String): CollectionReference {
        return userCollectionRef.document(uid).collection("learning-list")
    }

    suspend fun unsubscribeChannel(uid: String, channelId: String) = withContext(dispatchers) {
        userCollectionRef.document(uid)
            .collection("channel-member")
            .whereEqualTo("channelId", channelId).get().addOnSuccessListener {
                it.forEach {
                    it.reference.delete()
                }
            }
    }

    suspend fun leaveGroup(uid: String, groupId: String) = withContext(dispatchers) {
        userCollectionRef.document(uid)
            .collection("group-member")
            .whereEqualTo("groupId", groupId).get().addOnSuccessListener {
                it.forEach {
                    it.reference.delete()
                }
            }
    }
}

