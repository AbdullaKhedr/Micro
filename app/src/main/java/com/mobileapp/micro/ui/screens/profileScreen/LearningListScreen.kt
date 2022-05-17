package com.mobileapp.micro.ui.screens.profileScreen

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobileapp.micro.ui.components.LessonItem
import com.mobileapp.micro.ui.components.MemberItem
import com.mobileapp.micro.ui.components.SimpleTopBar
import com.mobileapp.micro.ui.components.StudyLessonDialog
import com.mobileapp.micro.viewmodel.AuthViewModel
import com.mobileapp.micro.viewmodel.CourseViewModel
import com.mobileapp.micro.viewmodel.UserViewModel

@Composable
fun LearningListScreen(
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val profileViewModel = viewModel<UserViewModel>(context as ComponentActivity)
    val courseViewModel = viewModel<CourseViewModel>(context)
    val authViewModel = viewModel<AuthViewModel>(context)
    val userViewModel = viewModel<UserViewModel>(context)

    val showLessonDialog = remember { mutableStateOf(false) }
    if (showLessonDialog.value) {
        StudyLessonDialog(
            dialogState = showLessonDialog,
            courseViewModel = courseViewModel
        )
    }

    Scaffold(
        topBar = {
            SimpleTopBar(
                onNavigateBack = onNavigateBack,
                title = "My Learning List"
            )
        },
        content = {
            LearningListScreenContent(
                userViewModel = profileViewModel,
                courseViewModel = courseViewModel
            )
        }
    )
}

@Composable
fun LearningListScreenContent(
    userViewModel: UserViewModel,
    courseViewModel: CourseViewModel
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(userViewModel.userLearningList) {
                LessonItem(
                    courseViewModel = courseViewModel,
                    lesson = it
                )
                Divider()
            }
        }
    }
}
