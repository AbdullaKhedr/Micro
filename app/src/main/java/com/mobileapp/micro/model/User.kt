package com.mobileapp.micro.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import java.util.*

data class User(
    var firstName: String = "",
    var lastName: String = "",
    var email: String = "",
    @get:Exclude var password: String = "",
    var bio: String = "",
    @DocumentId var uid: String = "",
    var isVerified: Boolean = false,
    var profilePhotoName: String = "",
    var onlinePhotoUri: String = "",
    var localPhotoUri: String = "",
    var createdAt: Date = Date(),
    // Those instead of making them as lists,
    // they will be collections in the user document
//    val CoursesStudied: List<CourseStudy> = listOf(),
//    val memberOfStudyGroups: List<MemberOfStudyGroup> = listOf(),
//    val memberOfChannels: List<MemberOfChannels> = listOf(),
) {
    /**
     * The user will not enter a username,
     * instead it will be first part of his email
     */
    val username: String = email.split("@")[0]
    val fullName: String
        get() {
            return "$firstName $lastName"
        }
    val displayName: String
        get() {
            return "$firstName $lastName"
        }

    override fun toString() = displayName
}

data class UserFriend(
    @DocumentId var id: String = "",
    var friendEmail: String = ""
)

data class CourseStudy(
    @DocumentId var id: String = "",
    var courseId: String = "",
    var createdAt: Date = Date(),
)

data class MemberOfStudyGroup(
    @DocumentId var id: String = "",
    var groupId: String = "",
    var createdAt: Date = Date(),
)

data class MemberOfChannels(
    @DocumentId var id: String = "",
    var channelId: String = "",
    var createdAt: Date = Date(),
)