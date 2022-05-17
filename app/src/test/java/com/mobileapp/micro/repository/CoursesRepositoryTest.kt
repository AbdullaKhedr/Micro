package com.mobileapp.micro.repository

import com.mobileapp.micro.model.Course

class CoursesRepositoryTest {
	private val courses = mutableListOf<Course>()

	fun addCourse(course: Course): Int {
		courses.add(course)
		return courses.size-1
	}

	fun getCourseById(id: String): Course? {
		return courses.find {it.courseId == id}
	}
}