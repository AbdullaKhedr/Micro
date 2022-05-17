package com.mobileapp.micro.ui.screens.profileScreen

import androidx.activity.ComponentActivity
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobileapp.micro.ui.components.CourseItem
import com.mobileapp.micro.ui.components.SimpleTopBar
import com.mobileapp.micro.viewmodel.CourseViewModel
import com.mobileapp.micro.viewmodel.create.CreateCourseViewModel

@Composable
fun UserCreatedCoursesScreen(
    onNavigateBack: () -> Unit,
    onEditCourse: () -> Unit
) {
    val context = LocalContext.current
    val courseViewModel = viewModel<CourseViewModel>(context as ComponentActivity)
    val createCourseViewModel = viewModel<CreateCourseViewModel>(context)
    Scaffold(
        topBar = {
            SimpleTopBar(
                onNavigateBack = onNavigateBack,
                title = "Created Courses"
            )
        },
        content = {
            UserCreatedCoursesScreenContent(
                courseViewModel = courseViewModel,
                createCourseViewModel = createCourseViewModel,
                onEditCourse = onEditCourse
            )
        }
    )
}

@Composable
private fun UserCreatedCoursesScreenContent(
    courseViewModel: CourseViewModel,
    createCourseViewModel: CreateCourseViewModel,
    onEditCourse: () -> Unit
) {
    LazyColumn {
        items(courseViewModel.userCreatedCourses) { course ->
            CourseItem(
                course = course,
                onClick = {}
            ) { showCourseOptionMenu ->
                DropdownMenuItem(
                    onClick = {
                        /*TODO on remove course*/
                        createCourseViewModel.getCourseToEdit(course)
                        onEditCourse()
                        showCourseOptionMenu.value = false
                    }
                ) {
                    Text(text = "Edit")
                }
                DropdownMenuItem(
                    onClick = {
                        /*TODO on remove course*/
                        createCourseViewModel.deleteCourse(course)
                        showCourseOptionMenu.value = false
                    }
                ) {
                    Text(text = "Delete")
                }
            }
            Divider()
        }
    }
}