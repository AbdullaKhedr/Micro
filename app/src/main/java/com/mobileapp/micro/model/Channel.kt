package com.mobileapp.micro.model

import com.google.firebase.firestore.DocumentId
import java.util.*

data class Channel(
    @DocumentId var channelId: String = "",
    var channelName: String = "",
    var channelDescription: String = "",
    var membersCount: Int = 0,
    var authorId: String = "",
    var membersEmails: MutableList<String> = mutableListOf(),
    var lessonsIds: List<String> = listOf(),
    var createdAt: Date = Date(),
    var updatedAt: Date = Date(),
    var displayImageName: String = "",
    var onlineImageUri: String = "",
    var localImageUri: String = ""
)
