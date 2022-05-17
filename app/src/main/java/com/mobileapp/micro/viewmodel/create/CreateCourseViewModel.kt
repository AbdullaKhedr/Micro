package com.mobileapp.micro.viewmodel.create

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobileapp.micro.model.Course
import com.mobileapp.micro.model.CourseCategory
import com.mobileapp.micro.model.Lesson
import com.mobileapp.micro.model.MediaType
import com.mobileapp.micro.repository.AuthRepository
import com.mobileapp.micro.repository.CourseRepository
import com.mobileapp.micro.repository.LessonRepository
import com.mobileapp.micro.repository.StorageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

/**
 * This VM is for Create Course Screens, to create course and it's lessons
 */
class CreateCourseViewModel(
    private val courseRepository: CourseRepository = CourseRepository(),
    private val lessonRepository: LessonRepository = LessonRepository(),
    private val storageRepository: StorageRepository = StorageRepository(),
    private val authRepository: AuthRepository = AuthRepository()

) : ViewModel() {

    private val COURSE_TAG = "Course"
    private val COURSE_IMAGE_TAG = "Course Image"
    private val LESSON_TAG = "Lesson"
    private val LESSON_MEDIA_TAG = "Lesson Media"

    sealed class CreateCoursesUiState {
        object Success : CreateCoursesUiState()
        object Loading : CreateCoursesUiState()
        object Empty : CreateCoursesUiState()
        data class Error(val message: String) : CreateCoursesUiState()
    }

    private val _createCoursesUiState =
        MutableStateFlow<CreateCoursesUiState>(CreateCoursesUiState.Empty)
    val createCoursesUiState: StateFlow<CreateCoursesUiState> = _createCoursesUiState

    val categories = mutableStateListOf<CourseCategory>()

    // START : current Course UI related Properties
    val courseTitle = mutableStateOf("")
    val courseDescription = mutableStateOf("")
    val courseCategory = mutableStateOf("")
    val tagsList = mutableStateListOf<String>()
    val courseImageLocalUri = mutableStateOf("")
    // END : current Course UI related Properties

    // START : current Lesson UI related Properties
    val lessonTitle = mutableStateOf("")
    val lessonContent = mutableStateOf("")
    val lessonMediaLocalUri = mutableStateOf("")
    val mediaType = mutableStateOf(MediaType.NON)
    // END : current Lesson UI related Properties

    var indexToUpdate = -1
    // END : current Lesson UI related Properties

    // current displayed course in the Course Screen
    var currentCourse: Course = Course()
    val currentCourseLessonsList = mutableStateListOf<Lesson>()

    // If a course will be updated
    private val oldCourseLessonsListToBeUpdates = mutableStateListOf<Lesson>()
    private var oldCourseToBeUpdated = Course()

    init {
        viewModelScope.launch {
            categories.addAll(courseRepository.getCourseCategories())
        }
    }

    fun addCourse() = viewModelScope.launch {
        _createCoursesUiState.value = CreateCoursesUiState.Loading
        try {
            authRepository.getCurrentFirebaseUser()?.let { firebaseUser ->
                currentCourse.let { course ->
                    course.authorId = firebaseUser.uid
                    // note: name could be known from uid, but this is easy to display the courses.
                    course.authorName = firebaseUser.displayName.toString()
                    course.title = courseTitle.value
                    course.description = courseDescription.value
                    course.category = courseCategory.value
                    course.tags = tagsList
                    course.localImageUri = courseImageLocalUri.value

                    var courseId = ""
                    if (course.courseId.isEmpty()) { // add a new course
                        course.createdAt = Date()
                        val docRef = courseRepository.addCourse(course)
                        courseId = docRef.id
                        Log.d(COURSE_TAG, "Course created @ID:${docRef.id}")
                    } else { // update course
                        course.updatedAt = Date()
                        courseRepository.updateCourse(course)
                        courseId = course.courseId
                        Log.d(COURSE_TAG, "Course updated @ID:${course.courseId}")
                    }

                    if (oldCourseToBeUpdated.localImageUri != course.localImageUri)
                        uploadCourseImage(
                            courseId = courseId,
                            imageName = course.displayImageName,
                            localImageUri = course.localImageUri,
                            newImage = oldCourseToBeUpdated.localImageUri == "",
                        )

                    currentCourseLessonsList.forEachIndexed { index, lesson ->
                        val oldLesson = getOldLesson(lesson.lessonId)
                        var lessonId = ""
                        if (lesson.lessonId.isEmpty()) {
                            lesson.lessonIndex = index
                            lesson.createdAt = Date()
                            val docRef = lessonRepository.addLesson(lesson)
                            lessonId = docRef.id
                            courseRepository.addLessonIdToCourse(
                                courseId = courseId,
                                lessonsId = lessonId
                            )
                            Log.d(LESSON_TAG, "Lesson created @ID:${docRef.id}")
                        } else {
                            lesson.updatedAt = Date()
                            lessonRepository.updateLesson(lesson)
                            lessonId = lesson.lessonId
                            Log.d(LESSON_TAG, "Lesson updated @ID:${lesson.lessonId}")
                        }
                        if (oldLesson != null) {
                            if (oldLesson.localMediaUri != lesson.localMediaUri) {
                                uploadLessonMedia(
                                    lessonId = lessonId,
                                    mediaName = lesson.mediaName,
                                    localImageUri = lesson.localMediaUri,
                                    mediaType = lesson.mediaType,
                                    newMedia = oldLesson.mediaName == "",
                                )
                            }
                        }
                    }
                    _createCoursesUiState.value = CreateCoursesUiState.Success
                    clearCourseCreationUiData()
                }
            } ?: throw Exception("Can't Authenticate the user")
        } catch (e: Exception) {
            _createCoursesUiState.value = CreateCoursesUiState.Error(e.message.toString())
            Log.e(COURSE_TAG, e.message.toString())
        }
    }

    private fun uploadCourseImage(
        courseId: String,
        imageName: String,
        localImageUri: String,
        newImage: Boolean
    ) = viewModelScope.launch {
        try {
            val onlineName = if (newImage)
                storageRepository.generateMediaFileName(imageName, MediaType.IMAGE)
            else imageName
            val onlineUri = storageRepository.uploadMedia(
                storageRef = storageRepository.courseDisplayImagesStorageRef,
                fileName = onlineName,
                uri = localImageUri
            )
            Log.d(COURSE_IMAGE_TAG, "Name: $onlineName, Uri: $onlineUri")
            if (onlineUri.isNotEmpty())
                courseRepository.updateOnlineImageUri(courseId, onlineName, onlineUri)
            Log.d(COURSE_IMAGE_TAG, "Image uploaded for course @ID:${courseId}")
        } catch (e: Exception) {
            Log.e(COURSE_IMAGE_TAG, e.message.toString())
        }
    }

    private fun uploadLessonMedia(
        lessonId: String,
        mediaName: String,
        localImageUri: String,
        mediaType: MediaType,
        newMedia: Boolean
    ) = viewModelScope.launch {
        try {
            val onlineName = if (newMedia)
                storageRepository.generateMediaFileName(mediaName, mediaType)
            else mediaName
            val onlineUri = storageRepository.uploadMedia(
                storageRef = storageRepository.lessonMediaStorageRef,
                fileName = onlineName,
                uri = localImageUri
            )
            Log.d(LESSON_MEDIA_TAG, "Name: $onlineName, Uri: $onlineUri")
            if (onlineUri.isNotEmpty())
                lessonRepository.updateOnlineMediaUri(
                    lessonId = lessonId,
                    onlineName = onlineName,
                    onlineUri = onlineUri
                )
            Log.d(LESSON_MEDIA_TAG, "Media uploaded for lesson @ID:${lessonId}")
        } catch (e: Exception) {
            Log.e(LESSON_MEDIA_TAG, e.message.toString())
        }
    }

    fun addLessonToCurrentCreatingCourse() {
        if (indexToUpdate >= 0) {
            currentCourseLessonsList[indexToUpdate].let { // update the existing one
                it.title = lessonTitle.value
                it.content = lessonContent.value
                it.localMediaUri = lessonMediaLocalUri.value
                it.mediaType = mediaType.value
            }
        } else {
            currentCourseLessonsList.add(
                Lesson( // create new one
                    title = lessonTitle.value,
                    content = lessonContent.value,
                    localMediaUri = lessonMediaLocalUri.value,
                    mediaType = mediaType.value
                )
            )
        }
        clearLessonUiData()
    }

    fun deleteCourse(course: Course) =
        viewModelScope.launch {
            try {
                // get the lessons to know the media name for each one to delete it
                val lessons = lessonRepository.getCourseLessonsList(course.lessonsIds)
                courseRepository.deleteCourse(course.courseId)
                storageRepository.deleteMedia(
                    storageRef = storageRepository.courseDisplayImagesStorageRef,
                    fileName = course.displayImageName
                )
                // FIXME not working properly
                lessons.forEach { lesson ->
                    lessonRepository.deleteLesson(lesson.lessonId)
                    storageRepository.deleteMedia(
                        storageRef = storageRepository.courseDisplayImagesStorageRef,
                        fileName = lesson.mediaName
                    )
                }
                // TODO delete comments, likes, studies, etc. (cascading delete!!?)
            } catch (e: Exception) {

            }
        }

    fun getCourseToEdit(course: Course) = viewModelScope.launch {
        // Fill UI data
        courseTitle.value = course.title
        courseDescription.value = course.description
        courseCategory.value = course.category
        courseImageLocalUri.value = course.localImageUri
        tagsList.addAll(course.tags)
        // Fill the course and lessons holders
        oldCourseToBeUpdated = course.copy()
        currentCourse = course
        try {
            val lessons = lessonRepository.getCourseLessonsList(course.lessonsIds)
            lessons.sortBy { it.lessonIndex }
            currentCourseLessonsList.addAll(lessons)
            oldCourseLessonsListToBeUpdates.addAll(lessons)
        } catch (e: Exception) {
            Log.e(LESSON_TAG, e.message.toString())
        }
    }

    fun getLessonToEdit(lesson: Lesson) {
        // refill views by the lesson
        lessonTitle.value = lesson.title
        lessonContent.value = lesson.content
        lessonMediaLocalUri.value = lesson.onlineMediaUri
        mediaType.value = lesson.mediaType
    }

    private fun getOldLesson(id: String) =
        oldCourseLessonsListToBeUpdates.find { it.lessonId == id }

    fun clearCourseCreationUiData() {
        // reset the object creator
        currentCourse = Course()
        // Clear UI related variables
        courseTitle.value = ""
        courseDescription.value = ""
        courseCategory.value = ""
        tagsList.clear()
        courseImageLocalUri.value = ""
        currentCourseLessonsList.clear()
        clearLessonUiData()
    }

    fun clearLessonUiData() {
        lessonTitle.value = ""
        lessonContent.value = ""
        lessonMediaLocalUri.value = ""
        mediaType.value = MediaType.NON
        indexToUpdate = -1
    }
}