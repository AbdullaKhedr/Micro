package com.mobileapp.micro.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import java.util.*

data class StudyGroup(
    @DocumentId var groupId: String = "",
    var groupName: String = "",
    var groupDescription: String = "",
    var authorId: String = "",
    var createdAt: Date = Date(),
    var updatedAt: Date = Date(),
    var membersCount: Int = 0,
    var displayImageName: String = "",
    var localImageUri: String = "",
    var onlineImageUri: String = "",
    var lastMessage: GroupMessage? = null,
    var membersEmails: MutableList<String> = mutableListOf()
)

data class GroupMessage(
    var groupId: String = "",
    var authorId: String = "",
    var authorName: String = "",
    var text: String = "",
    var type: MessageType = MessageType.TEXT,
    var lessonTitle: String = "",
    var lessonId: String = "",
    var createdAt: Date = Date(),
    @get:Exclude var isMine: Boolean = false,
)

enum class MessageType {
    TEXT, LESSON
}