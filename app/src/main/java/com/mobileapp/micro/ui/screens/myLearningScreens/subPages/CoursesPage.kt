package com.mobileapp.micro.ui.screens.myLearningScreens.subPages

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mobileapp.micro.R
import com.mobileapp.micro.ui.components.CourseItem
import com.mobileapp.micro.ui.components.EmptyContentScreen
import com.mobileapp.micro.viewmodel.CourseViewModel

@Composable
fun CoursesPage(
    courseViewModel: CourseViewModel,
    onOpenCourse: () -> Unit
) {
    if (courseViewModel.userStudyingCourses.isEmpty()) {
        EmptyContentScreen(
            image = R.drawable.logo,
            text = "Start studying a course and see your progress!"
        )
    } else {
        CoursesPageContent(
            courseViewModel = courseViewModel,
            onOpenCourse = onOpenCourse
        )
    }
}

@Composable
fun CoursesPageContent(
    courseViewModel: CourseViewModel,
    onOpenCourse: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        items(courseViewModel.userStudyingCourses) { course ->
            CourseItem(
                course = course,
                onClick = {
                    courseViewModel.getCourseToStudy(course)
                    onOpenCourse()
                }
            ) { showCourseOptionMenu ->
                DropdownMenuItem(
                    onClick = {
                        courseViewModel.removeStudyingCourse(course.courseId)
                        showCourseOptionMenu.value = false
                    }
                ) {
                    Text(text = "Remove")
                }
            }
            Divider()
        }
    }
}