package com.mobileapp.micro.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import java.util.*

data class Course(
    @DocumentId var courseId: String = "",
    var title: String = "",
    var description: String = "",
    var tags: MutableList<String> = mutableListOf(),
    var lessonsIds: MutableList<String> = mutableListOf(),
    var displayImageName: String = "",
    var onlineImageUri: String = "",
    var localImageUri: String = "",
    var category: String = "",
    // Number of times the course is visited
    var visits: Int = 0,
    // Number of likes the course gets
    var likes: Int = 0,
    // Number of people studying the course
    var studied: Int = 0,
    var authorId: String = "",
    var authorName: String = "",
    var authorImageUri: String = "",
    var createdAt: Date = Date(),
    var updatedAt: Date = Date()
) {
    override fun toString(): String {
        return title
    }
}

data class CourseLike(
    var userId: String = "",
    var courseId: String = "",
    var createdAt: Date = Date(),
)

data class CourseComment(
    var authorId: String = "",
    var authorName: String = "",
    var courseId: String = "",
    var createdAt: Date = Date(),
    var text: String = "",
    @get:Exclude var isMine: Boolean = false,
    @DocumentId var commentId: String = "",
) {
    override fun toString(): String {
        return text
    }
}

data class CourseCategory(val category: String = "") {
    override fun toString(): String {
        return category
    }
}