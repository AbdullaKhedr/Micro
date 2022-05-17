package com.mobileapp.micro

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector? = null,
    val iconResourceId: Int? = null
) {
    // Auth Screens
    object Login : Screen(route = "login", title = "Log In", icon = Icons.Outlined.Login)
    object SignUp : Screen(route = "signup", title = "Sign Up", icon = Icons.Outlined.PersonAdd)

    // Main Screens
    object Home : Screen(route = "home", title = "Home", icon = Icons.Filled.Home)
    object Search : Screen(route = "search", title = "Search", icon = Icons.Filled.Search)
    object Learning : Screen(route = "learning", title = "Learning", icon = Icons.Filled.Book)
    object Profile : Screen(route = "profile", title = "Profile", icon = Icons.Filled.Person)

    // START: Create Screens
    object EditCourse : Screen(route = "edit-course", title = "Edit Course")
    object CourseLessonList : Screen(route = "course-lessons", title = "Course Lessons")
    object EditCourseLesson : Screen(route = "edit-course-lesson", title = "Edit Lesson")

    object CreateChannel : Screen(route = "create-channel", title = "Create Channel")
    object EditChannelLesson : Screen(route = "edit-channel-lesson", title = "Edit Lesson")

    object CreateStudyGroup : Screen(route = "create-study-group", title = "Create StudyGroup")
    // END: Create Screens

    // The Screens where the user can study a Course
    object Course : Screen(route = "course", title = "Course")
    object Lesson : Screen(route = "lesson", title = "Lesson")

    // The screen of StudyGroup
    object StudyGroup : Screen(route = "study-group", title = "Study Group")

    // The screen of Channel
    object Channel : Screen(route = "channel", title = "Channel")

    object Friends : Screen(route = "friends", title = "Friends")
    object LearningList : Screen(route = "learning-list", title = "Learning List")
    object UserCreatedCoursesScreen : Screen(route = "user-created-course", title = "")
    object EditProfile : Screen(route = "edit-profile", title = "Edit Profile")
    object Settings : Screen(route = "Settings", title = "Settings", icon = Icons.Outlined.Settings)
}