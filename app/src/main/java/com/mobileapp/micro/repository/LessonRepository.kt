package com.mobileapp.micro.repository

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mobileapp.micro.model.Lesson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LessonRepository {
    private val lessonsCollectionRef by lazy { Firebase.firestore.collection("lessons") }
    private val dispatchers = Dispatchers.IO

    suspend fun addLesson(lesson: Lesson): DocumentReference = withContext(dispatchers) {
        lessonsCollectionRef.add(lesson).await()
    }

    suspend fun updateLesson(lesson: Lesson) = withContext(dispatchers) {
        lessonsCollectionRef.document(lesson.lessonId).set(lesson).await()
    }

    suspend fun deleteLesson(lessonId: String) = withContext(dispatchers) {
        lessonsCollectionRef.document(lessonId).delete().await()
    }

    suspend fun getCourseLessonsList(lessonsListIds: List<String>): MutableList<Lesson> =
        withContext(dispatchers) {
            if (lessonsListIds.isNotEmpty())
                lessonsCollectionRef.whereIn(FieldPath.documentId(), lessonsListIds).get().await()
                    .toObjects(Lesson::class.java)
            else
                mutableListOf()
            // .orderBy("lessonIndex", Query.Direction.ASCENDING)
        }

    fun getChannelLessonsList(lessonsListIds: List<String>): MutableList<DocumentReference> {
        val result: MutableList<DocumentReference> = mutableListOf()
        lessonsListIds.forEach {
            result.add(lessonsCollectionRef.document(it))
        }
        return result
    }

    suspend fun getLessonById(lessonId: String): Lesson? = withContext(dispatchers) {
        lessonsCollectionRef.document(lessonId).get().await().toObject(Lesson::class.java)
    }

    suspend fun updateOnlineMediaUri(lessonId: String, onlineName: String, onlineUri: String) =
        withContext(dispatchers) {
            lessonsCollectionRef.document(lessonId).update("onlineMediaUri", onlineUri).await()
            lessonsCollectionRef.document(lessonId).update("mediaName", onlineName).await()
        }

}