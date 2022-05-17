package com.mobileapp.micro.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mobileapp.micro.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class CourseRepository {

    private val coursesCollectionRef by lazy { Firebase.firestore.collection("courses") }
    private val courseCommentsCollectionRef by lazy { Firebase.firestore.collection("course-comments") }
    private val coursesLikesCollectionRef by lazy { Firebase.firestore.collection("courses-likes") }
    private val categoriesCollectionRef by lazy { Firebase.firestore.collection("course-category") }
    private val dispatchers = Dispatchers.IO

    /**
     * Important: Using suspend doesn't tell Kotlin to run a function on a background thread.
     * It's normal for suspend functions to operate on the main thread.
     * It's also common to launch coroutines on the main thread.
     * You should always use withContext() inside a suspend function when you need main-safety,
     * such as when reading from or writing to disk, performing network operations,
     * or running CPU-intensive operations.
     */

    suspend fun getCourseCategories(): MutableList<CourseCategory> {
        return categoriesCollectionRef.orderBy("category", Query.Direction.ASCENDING)
            .get().await().toObjects(CourseCategory::class.java)
    }

    suspend fun addCourse(course: Course): DocumentReference = withContext(dispatchers) {
        coursesCollectionRef.add(course).await()
    }

    fun getCourses(): CollectionReference {
        return coursesCollectionRef
    }

    suspend fun updateCourse(course: Course) = withContext(dispatchers) {
        coursesCollectionRef.document(course.courseId).set(course).await()
    }

    suspend fun updateOnlineImageUri(courseId: String, onlineName: String, onlineUri: String) =
        withContext(dispatchers) {
            coursesCollectionRef.document(courseId).update("onlineImageUri", onlineUri).await()
            coursesCollectionRef.document(courseId).update("displayImageName", onlineName).await()
        }

    suspend fun addLessonIdToCourse(courseId: String, lessonsId: String) =
        withContext(dispatchers) {
            coursesCollectionRef.document(courseId)
                .update("lessonsIds", FieldValue.arrayUnion(lessonsId))
                .await()
        }

    suspend fun deleteCourse(courseId: String) = withContext(dispatchers) {
        coursesCollectionRef.document(courseId).delete().await()
    }

    suspend fun updateCourseVisits(courseId: String) = withContext(dispatchers) {
        coursesCollectionRef.document(courseId).update("visits", FieldValue.increment(1)).await()
    }

    suspend fun addCourseLike(courseId: String, userId: String) = withContext(dispatchers) {
        val isLikedState = userLikedCourse(userId = userId, courseId = courseId)
        if (isLikedState.isEmpty) {
            // if no prev like, Add the like
            coursesCollectionRef.document(courseId).update("likes", FieldValue.increment(1))
                .await()
            coursesLikesCollectionRef.add(
                CourseLike(
                    userId = userId,
                    courseId = courseId,
                    createdAt = Date()
                )
            ).await()
        } else {
            // Remove the like
            coursesCollectionRef.document(courseId).update("likes", FieldValue.increment(-1))
                .await()
            isLikedState.forEach {
                it.reference.delete()
            }
        }
    }

    suspend fun incrementCourseStudies(courseId: String) = withContext(dispatchers) {
        coursesCollectionRef.document(courseId).update("studied", FieldValue.increment(1)).await()
    }

    suspend fun getAllCourses(): QuerySnapshot = withContext(dispatchers) {
        coursesCollectionRef.get().await()
    }

    suspend fun getCourseById(courseId: String) = withContext(dispatchers) {
        coursesCollectionRef.document(courseId).get().await().toObject(Course::class.java)
    }

    fun getCoursesByAuthorId(authorId: String): Query {
        return coursesCollectionRef.whereEqualTo("authorId", authorId)
    }

    fun getUserStudyingCoursesByIds(coursesIds: List<CourseStudy>): Query {
        val ids = coursesIds.map { it.courseId }
        return coursesCollectionRef.whereIn(FieldPath.documentId(), ids)
    }

    fun getRecentCourses(): Query {
        return coursesCollectionRef.orderBy("createdAt", Query.Direction.DESCENDING).limit(10)
    }

    // Get top 5 visited
    fun getTopCoursesVisited(): Query {
        return coursesCollectionRef.orderBy("visits", Query.Direction.DESCENDING).limit(10)
    }

    // Get top 5 liked
    fun getTopCoursesLiked(): Query {
        return coursesCollectionRef.orderBy("likes", Query.Direction.DESCENDING).limit(10)
    }

    // Check if a user liked a course
    suspend fun userLikedCourse(userId: String, courseId: String): QuerySnapshot =
        withContext(dispatchers) {
            coursesLikesCollectionRef
                .whereEqualTo("userId", userId)
                .whereEqualTo("courseId", courseId)
                .limit(1)
                .get()
                .await()
        }

    /** START: Course Comments */
    suspend fun addCourseComment(comment: CourseComment) = withContext(dispatchers) {
        courseCommentsCollectionRef.document(comment.courseId)
            .collection("comments")
            .add(comment)
            .await()
    }

    fun getCourseComments(courseId: String): Query {
        return courseCommentsCollectionRef.document(courseId)
            .collection("comments")
            .orderBy("createdAt", Query.Direction.ASCENDING)
    }
    /** END: Course Comments */

}