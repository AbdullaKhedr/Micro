package com.mobileapp.micro.model

import com.google.firebase.firestore.DocumentId
import java.util.*

enum class MediaType { NON, IMAGE, VIDEO }

data class Lesson(
    @DocumentId val lessonId: String = "",
    var title: String = "",
    var content: String = "",
    // TODO: this should be removed since the course has its lessons ids
    // but we will keep it so if the lesson shared to a channel or group,
    // user can know it belongs to which course
    var courseId: String = "",
    var lessonIndex: Int = -1,
    var mediaName: String = "",
    var onlineMediaUri: String = "",
    var localMediaUri: String = "",
    var mediaType: MediaType = MediaType.NON,
    var visited: Int = 0,
    var createdAt: Date = Date(),
    var updatedAt: Date = Date(),
    var authorId: String = "",
) {
    override fun toString(): String {
        return title
    }
}