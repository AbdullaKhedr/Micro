package com.mobileapp.micro.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.mobileapp.micro.model.Course
import com.mobileapp.micro.repository.CourseRepository
import kotlinx.coroutines.launch

class SearchViewModel(
    private val courseRepository: CourseRepository = CourseRepository()
) : ViewModel() {

    private var coursesUpdateListener: ListenerRegistration? = null
    private val _courses = mutableStateListOf<Course>()
    val filteredCourses = mutableStateListOf<Course>()

    enum class Filter(
        val filterName: String,
        var isSelected: MutableState<Boolean> = mutableStateOf(false)
    ) {
        TITLE("Course Title", mutableStateOf(true)),
        AUTHOR("Course Author"),
        TAGS("Course Tags"),
        CATEGORY("Course Category")
    }

    val searchText = mutableStateOf("")
    val filters = mutableStateListOf(Filter.TITLE, Filter.AUTHOR, Filter.TAGS, Filter.CATEGORY)

    init {
        viewModelScope.launch {
            coursesUpdateListener?.remove()
            try {
                val query = courseRepository.getCourses()
                coursesUpdateListener = query.addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        println(">> Debug: Courses Search Update Listener failed. ${e.message}")
                        return@addSnapshotListener
                    }
                    val results = snapshot?.toObjects(Course::class.java)
                    _courses.clear()
                    results?.let {
                        _courses.addAll(it)
                    }
                }
            } catch (e: Exception) {
                Log.e("Course Search", e.message.toString())
            }
        }
    }

    fun search() {
        if (searchText.value == "") {
            filteredCourses.clear()
            return
        }
        val result = mutableListOf<Course>()
        filters.forEach {
            if (it == Filter.TITLE && it.isSelected.value) {
                result.addAll(_courses.filter { course ->
                    course.title.contains(
                        searchText.value,
                        ignoreCase = true
                    )
                })
            }
            if (it == Filter.AUTHOR && it.isSelected.value) {
                result.addAll(_courses.filter { course ->
                    course.authorName.contains(
                        searchText.value,
                        ignoreCase = true
                    )
                })
            }
            if (it == Filter.TAGS && it.isSelected.value) {
                result.addAll(_courses.filter { course ->
                    course.tags.contains(
                        searchText.value
                    )
                })
            }
            if (it == Filter.CATEGORY && it.isSelected.value) {
                result.addAll(_courses.filter { course ->
                    course.category.contains(
                        searchText.value,
                        ignoreCase = true
                    )
                })
            }
        }
        filteredCourses.clear()
        filteredCourses.addAll(result)
    }
}