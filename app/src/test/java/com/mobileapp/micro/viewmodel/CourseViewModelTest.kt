package com.mobileapp.micro.viewmodel

import com.mobileapp.micro.model.Course
import com.mobileapp.micro.repository.CoursesRepositoryTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CourseViewModelTest {
	private lateinit var coursesRepository: CoursesRepositoryTest

	@Before
	fun setUp() {
		coursesRepository = CoursesRepositoryTest()
	}

	@Test
	fun `Add course test`() {
		val course = Course("1", "Add course unit test", "This is just a test course")
		coursesRepository.addCourse(course)

		assertEquals(coursesRepository.getCourseById("1")?.title, "Add course unit test")
	}
}