package com.mobileapp.micro.ui.screens.createScreens.course

import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobileapp.micro.model.ContentConstrains
import com.mobileapp.micro.model.Lesson
import com.mobileapp.micro.ui.components.DefaultButton
import com.mobileapp.micro.ui.components.InfoDialog
import com.mobileapp.micro.ui.components.LoadingDialog
import com.mobileapp.micro.ui.components.SimpleTopBar
import com.mobileapp.micro.viewmodel.create.CreateCourseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * This Screen has the final step of creating a course which is (lessons)
 * once all lessons are created, the user will click on create course button.
 */

@Composable
fun LessonsScreen(
    onNavigateBack: () -> Unit,
    onCreateLesson: () -> Unit,
    onSubmitCourse: () -> Unit
) {
    val context = LocalContext.current
    val createCourseViewModel = viewModel<CreateCourseViewModel>(context as ComponentActivity)
    Scaffold(
        topBar = {
            SimpleTopBar(
                onNavigateBack = onNavigateBack,
                backArrow = true,
                title = "Lesson Editor"
            )
        },
        floatingActionButton = {
            if (createCourseViewModel.currentCourseLessonsList.size < ContentConstrains.MAX_LESSONS_PER_COURSE)
                ExtendedFloatingActionButton(
                    modifier = Modifier.padding(bottom = 70.dp),
                    onClick = { onCreateLesson() },
                    text = { Text(text = "Add Lesson") },
                    icon = { Icon(imageVector = Icons.Outlined.Add, contentDescription = "Add") }
                )
        },
        content = {
            LessonsScreenContent(
                onSubmitCourse,
                createCourseViewModel,
                onCreateLesson
            )
        }
    )
}

@Composable
private fun LessonsScreenContent(
    onSubmitCourse: () -> Unit,
    createCourseViewModel: CreateCourseViewModel,
    onCreateLesson: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val infoDialog = remember { mutableStateOf(false) }
    val progressDialog = remember { mutableStateOf(false) }
    val dialogMessage = remember { mutableStateOf("") }

    if (progressDialog.value)
        LoadingDialog(dialogState = progressDialog, message = "")

    if (infoDialog.value)
        InfoDialog(openDialog = infoDialog, title = "Error", text = dialogMessage.value)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1F)
        ) {
            items(
                count = createCourseViewModel.currentCourseLessonsList.size,
                key = { it },
                itemContent = { index ->
                    LessonCard(
                        lesson = createCourseViewModel.currentCourseLessonsList[index],
                        onDelete = {
                            createCourseViewModel.currentCourseLessonsList.removeAt(
                                index
                            )
                        },
                        onOpen = {
                            createCourseViewModel.getLessonToEdit(createCourseViewModel.currentCourseLessonsList[index])
                            createCourseViewModel.indexToUpdate = index
                            // go for create screen
                            onCreateLesson()
                        }
                    )
                }
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        DefaultButton(
            text = "Done",
            enable = createCourseViewModel.currentCourseLessonsList.size >= ContentConstrains.MIN_LESSONS_PER_COURSE,
            onClick = {
                coroutineScope.launch {
                    createCourseViewModel.createCoursesUiState.collect {
                        when (it) {
                            is CreateCourseViewModel.CreateCoursesUiState.Loading -> {
                                progressDialog.value = true
                            }
                            is CreateCourseViewModel.CreateCoursesUiState.Success -> {
                                progressDialog.value = false
                                onSubmitCourse()
                            }
                            is CreateCourseViewModel.CreateCoursesUiState.Error -> {
                                progressDialog.value = false
                                dialogMessage.value = it.message
                                infoDialog.value = true
                            }
                            else -> {}
                        }
                    }
                }
                createCourseViewModel.addCourse()
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun LessonCard(
    lesson: Lesson,
    onDelete: () -> Unit,
    onOpen: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(top = 8.dp),
        elevation = 5.dp,
        onClick = { onOpen() }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                text = lesson.title,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { onDelete() },
                imageVector = Icons.Outlined.Delete,
                contentDescription = "Delete"
            )
        }
    }
}