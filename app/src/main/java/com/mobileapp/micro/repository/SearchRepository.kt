package com.mobileapp.micro.repository

import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SearchRepository {
    private val coursesCollectionRef by lazy { Firebase.firestore.collection("courses") }

    fun searchCoursesByAuthorName(name: String): Query {
        return coursesCollectionRef.whereGreaterThanOrEqualTo("authorName", name)
    }

    fun searchCoursesByTitle(title: String): Query {
        return coursesCollectionRef.whereEqualTo("title", title)
    }

    fun searchCoursesByTags(tag: String): Query {
        return coursesCollectionRef.whereArrayContains("tags", tag)
    }

    fun searchCoursesByCategory(category: String): Query {
        return coursesCollectionRef.whereEqualTo("category", category)
    }
}