package com.mobileapp.micro.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.mobileapp.micro.model.Course
import com.mobileapp.micro.model.CourseComment
import com.mobileapp.micro.model.CourseStudy
import com.mobileapp.micro.model.Lesson
import com.mobileapp.micro.repository.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

/**
 * This VM is for main screen to get the courses and display them in sections
 */
class CourseViewModel(
    private val courseRepository: CourseRepository = CourseRepository(),
    private val lessonRepository: LessonRepository = LessonRepository(),
    private val storageRepository: StorageRepository = StorageRepository(),
    private val userRepository: UserRepository = UserRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    // UI State section
    sealed class CoursesUiState {
        object Success : CoursesUiState()
        object Loading : CoursesUiState()
        object Empty : CoursesUiState()
        data class Error(val message: String) : CoursesUiState()
    }

    private val _coursesUiState = MutableStateFlow<CoursesUiState>(CoursesUiState.Empty)
    val coursesUiState: StateFlow<CoursesUiState> = _coursesUiState

    // Home Screen views Section
    private var recentCoursesUpdateListener: ListenerRegistration? = null
    val recentCourses = mutableStateListOf<Course>()
    private var topCoursesLikedUpdateListener: ListenerRegistration? = null
    val topCoursesLiked = mutableStateListOf<Course>()
    private var topCoursesVisitedUpdateListener: ListenerRegistration? = null
    val topCoursesVisited = mutableStateListOf<Course>()

    // Related to user current studying courses list (My learning screen)
    private var userStudyingCoursesIdsUpdateListener: ListenerRegistration? = null
    private var userStudyingCoursesUpdateListener: ListenerRegistration? = null
    val userStudyingCourses = mutableStateListOf<Course>()

    // In user's profile a list of his created courses
    private var userCreatedCoursesUpdateListener: ListenerRegistration? = null
    val userCreatedCourses = mutableStateListOf<Course>()

    // START: Current Course Study
    val currentStudyCourse = mutableStateOf(Course())
    val isLiked = mutableStateOf(false)
    val isStudied = mutableStateOf(false)
    val currentStudyCourseLessons = mutableStateListOf<Lesson>()
    val currentLessonIndex = mutableStateOf(0)

    // Related to course comments section
    private var currentCourseCommentsUpdateListener: ListenerRegistration? = null
    var currentCourseComments = mutableStateListOf<CourseComment>()
    val currentCommentText = mutableStateOf("")
    private val commentToSend = mutableStateOf(CourseComment())

    // Current lesson Study
    val currentLesson: MutableState<Lesson>
        get() {
            var lesson = mutableStateOf(Lesson())
            try {
                lesson = mutableStateOf(currentStudyCourseLessons[currentLessonIndex.value])
            } catch (e: Exception) {
            }
            return lesson
        }

    // get single lesson by id (used for the lesson dialog)
    val lessonById = mutableStateOf(Lesson())
    // END: Current Course Study

    init {
        loadCourses()
        getUserStudyingCourses()
        authRepository.getCurrentFirebaseUser()?.uid?.let { getUserCreatedCourses(it) }
    }

    fun loadCourses() = viewModelScope.launch {
        _coursesUiState.value = CoursesUiState.Loading
        try {
            getRecentCourses()
            getTopCoursesLiked()
            getTopCoursesVisited()
            _coursesUiState.value = CoursesUiState.Success
        } catch (e: Exception) {
            _coursesUiState.value = CoursesUiState.Error(e.message.toString())
        }
    }

    // Used for Study course screen filling views
    fun getCourseToStudy(course: Course) {
        try {
            authRepository.getCurrentFirebaseUser()?.uid?.let {
                clearCurrentStudyCourse()
                initCurrentStudyCourse(course)
                initIsLikedStatus(uid = it, courseId = course.courseId)
                initIsStudiedStatus(uid = it, courseId = course.courseId)
                incrementVisits(course.courseId)
                initCurrentStudyCourseComments(course.courseId)
                // FIXME: anything after coroutine function won't be executed!!!!
                initCurrentStudyCourseLessons(course.lessonsIds)
            }
        } catch (e: Exception) {

        }
    }

    fun addCourseComment() = viewModelScope.launch {
        try {
            authRepository.getCurrentFirebaseUser()?.let { firebaseUser ->
                commentToSend.value.let {
                    it.authorId = firebaseUser.uid
                    it.authorName = firebaseUser.displayName.toString()
                    it.courseId = currentStudyCourse.value.courseId
                    it.createdAt = Date()
                    it.text = currentCommentText.value
                }
                courseRepository.addCourseComment(commentToSend.value)
            }
        } catch (e: Exception) {

        }
    }

    fun likeCourse(courseId: String) = viewModelScope.launch {
        try {
            isLiked.value = !isLiked.value
            authRepository.getCurrentFirebaseUser()?.uid?.let {
                courseRepository.addCourseLike(courseId = courseId, userId = it)
            }
        } catch (e: Exception) {

        }
    }

    fun studyCourse(courseId: String) = viewModelScope.launch {
        try {
            isStudied.value = true
            authRepository.getCurrentFirebaseUser()?.uid?.let {
                userRepository.addUserStudyingCourse(courseId = courseId, userId = it)
                courseRepository.incrementCourseStudies(courseId = courseId)
            }
        } catch (e: Exception) {

        }
    }

    fun getLessonById(id: String) = viewModelScope.launch {
        lessonById.value = lessonRepository.getLessonById(id)!!
    }

    private fun getUserStudyingCourses() {
        userStudyingCoursesIdsUpdateListener?.remove()
        try {
            authRepository.getCurrentFirebaseUser()?.let { firebaseUser ->
                val idsQuery = userRepository.getUserStudyingCoursesIds(firebaseUser.uid)
                userStudyingCoursesIdsUpdateListener =
                    idsQuery.addSnapshotListener { idsSnapshot, idsE ->
                        if (idsE != null) {
                            println(">> Debug: User Studying Courses ids Update Listener failed. ${idsE.message}")
                            return@addSnapshotListener
                        }
                        val idsResults = idsSnapshot?.toObjects(CourseStudy::class.java)
                        val userStudyingCoursesIds = mutableStateListOf<CourseStudy>()
                        idsResults?.let {
                            userStudyingCoursesIds.addAll(it)
                        }
                        userStudyingCoursesUpdateListener?.remove()

                        if (userStudyingCoursesIds.size > 0) {
                            val coursesQuery =
                                courseRepository.getUserStudyingCoursesByIds(userStudyingCoursesIds)
                            userStudyingCoursesUpdateListener =
                                coursesQuery.addSnapshotListener { coursesSnapshot, coursesE ->
                                    if (coursesE != null) {
                                        println(">> Debug: User Studying Courses Update Listener failed. ${coursesE.message}")
                                        return@addSnapshotListener
                                    }
                                    val coursesResults =
                                        coursesSnapshot?.toObjects(Course::class.java)
                                    userStudyingCourses.clear()
                                    coursesResults?.let {
                                        userStudyingCourses.addAll(it)
                                    }
                                }
                        }
                    }
            }
        } catch (e: Exception) {
        }
    }

    private fun getRecentCourses() = viewModelScope.launch {
        try {
            recentCoursesUpdateListener?.remove()
            val query = courseRepository.getRecentCourses()
            recentCoursesUpdateListener = query.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                val results = snapshot?.toObjects(Course::class.java)
                recentCourses.clear()
                results?.let {
                    recentCourses.addAll(it)
                }
            }
        } catch (e: Exception) {
        }
    }

    private fun getTopCoursesLiked() = viewModelScope.launch {
        try {
            topCoursesLikedUpdateListener?.remove()
            val query = courseRepository.getTopCoursesLiked()
            topCoursesLikedUpdateListener = query.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                val results = snapshot?.toObjects(Course::class.java)
                topCoursesLiked.clear()
                results?.let {
                    topCoursesLiked.addAll(it)
                }
            }
        } catch (e: Exception) {
        }
    }


    private fun getTopCoursesVisited() = viewModelScope.launch {
        try {
            topCoursesVisitedUpdateListener?.remove()
            val query = courseRepository.getTopCoursesVisited()
            topCoursesVisitedUpdateListener = query.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                val results = snapshot?.toObjects(Course::class.java)
                topCoursesVisited.clear()
                results?.let {
                    topCoursesVisited.addAll(it)
                }
            }
        } catch (e: Exception) {
        }
    }

    private fun getUserCreatedCourses(uid: String) = viewModelScope.launch {
        userCreatedCoursesUpdateListener?.remove()
        val coursesQuery = courseRepository.getCoursesByAuthorId(uid)
        userCreatedCoursesUpdateListener =
            coursesQuery.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    println(">> Debug: User Created Courses Update Listener failed. ${e.message}")
                    return@addSnapshotListener
                }
                val coursesResults =
                    snapshot?.toObjects(Course::class.java)
                userCreatedCourses.clear()
                coursesResults?.let {
                    userCreatedCourses.addAll(it)
                }
            }
    }

    /** START: init current study course */
    private fun initCurrentStudyCourse(course: Course) {
        currentStudyCourse.value = course
    }

    private fun initCurrentStudyCourseLessons(lessonsIds: List<String>) = viewModelScope.launch {
        try {
            currentStudyCourseLessons.clear()
            currentStudyCourseLessons.addAll(lessonRepository.getCourseLessonsList(lessonsIds))
            currentStudyCourseLessons.sortBy { it.lessonIndex }
        } catch (e: Exception) {
        }
    }

    private fun initIsLikedStatus(uid: String, courseId: String) = viewModelScope.launch {
        isLiked.value = !courseRepository.userLikedCourse(userId = uid, courseId = courseId).isEmpty
    }

    private fun initIsStudiedStatus(uid: String, courseId: String) = viewModelScope.launch {
        isStudied.value = userRepository.isUserStudiedCourse(courseId = courseId, uid = uid)
    }

    private fun initCurrentStudyCourseComments(courseId: String) {
        currentCourseCommentsUpdateListener?.remove()
        try {
            val query = courseRepository.getCourseComments(courseId)
            currentCourseCommentsUpdateListener = query.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    println(">> Debug: Course Comments Update Listener failed. ${e.message}")
                    return@addSnapshotListener
                }
                val results = snapshot?.toObjects(CourseComment::class.java)
                currentCourseComments.clear()
                results?.let {
                    authRepository.getCurrentFirebaseUser()?.let { firebaseUser ->
                        it.forEach { comment ->
                            if (firebaseUser.uid == comment.authorId)
                                comment.isMine = true
                        }
                    }
                    currentCourseComments.addAll(it)
                }
            }
        } catch (e: Exception) {
        }
    }

    /** END: init current study course */

    private fun clearCurrentStudyCourse() {
        // clear course staff
        isStudied.value = false
        isLiked.value = false
        currentStudyCourse.value = Course()
        // Clear lessons staff
        currentStudyCourseLessons.clear()
        currentLessonIndex.value = 0
        // clear comments staff
        commentToSend.value = CourseComment()
        currentCourseComments = mutableStateListOf()
    }

    private fun incrementVisits(courseId: String) = viewModelScope.launch {
        try {
            courseRepository.updateCourseVisits(courseId = courseId)
        } catch (e: Exception) {

        }
    }

    fun removeStudyingCourse(courseId: String) = viewModelScope.launch {
        try {
            authRepository.getCurrentFirebaseUser()?.let {
                userRepository.removeUserStudyingCourse(
                    uid = it.uid,
                    courseId = courseId
                )
            }
        } catch (e: Exception) {

        }
    }

}