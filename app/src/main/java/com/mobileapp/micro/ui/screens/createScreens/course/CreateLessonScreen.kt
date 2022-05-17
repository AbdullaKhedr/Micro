package com.mobileapp.micro.ui.screens.createScreens.course

import android.media.MediaPlayer
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobileapp.micro.common.getMediaPicker
import com.mobileapp.micro.common.toastMessage
import com.mobileapp.micro.model.ContentConstrains
import com.mobileapp.micro.model.MediaType
import com.mobileapp.micro.ui.components.ConstraintTF
import com.mobileapp.micro.ui.components.DefaultButton
import com.mobileapp.micro.ui.components.SimpleTopBar
import com.mobileapp.micro.viewmodel.create.CreateCourseViewModel


@Composable
fun CreateLessonScreen(
    onNavigateBack: () -> Unit,
    onSubmitLesson: () -> Unit
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
        content = {
            CreateLessonScreenContent(createCourseViewModel, onSubmitLesson)
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun CreateLessonScreenContent(
    createCourseViewModel: CreateCourseViewModel,
    onSubmitLesson: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        val context = LocalContext.current
        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current
        // If video media is chosen, (lesson content) is not mandatory
        val videoMediaChosen = remember { mutableStateOf(false) }
        val mediaPicker = getMediaPicker { uri ->
            context.contentResolver.getType(uri)?.let { type ->
                if (type.contains("video")) {
                    var durationTime: Long
                    MediaPlayer.create(context, uri).also { mediaPlayer ->
                        durationTime = (mediaPlayer.duration / 1000).toLong()
                        mediaPlayer.reset()
                        mediaPlayer.release()
                    }
                    if (durationTime <= ContentConstrains.LESSON_VIDEO_MAX_DURATION_SECONDS) {
                        createCourseViewModel.lessonMediaLocalUri.value = uri.toString()
                        createCourseViewModel.mediaType.value = MediaType.VIDEO
                        videoMediaChosen.value = true
                    } else {
                        toastMessage(context, "Video duration is more that 2 minutes")
                    }
                } else if (type.contains("image")) {
                    createCourseViewModel.lessonMediaLocalUri.value = uri.toString()
                    createCourseViewModel.mediaType.value = MediaType.IMAGE
                    videoMediaChosen.value = false
                } else {
                    createCourseViewModel.mediaType.value = MediaType.NON
                    videoMediaChosen.value = false
                }
            }
        }
        val validTitle = remember { mutableStateOf(true) }
        val validContent = remember { mutableStateOf(true) }
        ConstraintTF(
            textCharLimit = ContentConstrains.LESSON_TITLE_MAX_LENGTH,
            text = createCourseViewModel.lessonTitle,
            isValidText = validTitle,
            constraintMessage = "Reached max limit",
            label = "Title",
            placeHolder = "",
            singleLine = true,
            keyboardController = keyboardController,
            focusManager = focusManager,
            onDoneAction = ImeAction.Next
        )
        Spacer(modifier = Modifier.height(8.dp))
        MediaSelectDDM(
            createCourseViewModel,
            onSelectImage = { mediaPicker.launch("image/*") },
            onSelectVideo = { mediaPicker.launch("video/*") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        ConstraintTF(
            textCharLimit = ContentConstrains.LESSON_CONTENT_MAX_LENGTH,
            text = createCourseViewModel.lessonContent,
            isValidText = validContent,
            constraintMessage = "Reached max limit",
            label = "Content",
            placeHolder = "",
            singleLine = false,
            keyboardController = keyboardController,
            focusManager = focusManager,
            onDoneAction = ImeAction.Default,
            modifier = Modifier.weight(1F)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DefaultButton(
                modifier = Modifier.weight(1f),
                text = "Clear All",
                onClick = {
                    createCourseViewModel.clearLessonUiData()
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            DefaultButton(
                modifier = Modifier.weight(1f),
                text = "Add",
                enable = if (videoMediaChosen.value) { // video not need content
                    createCourseViewModel.lessonTitle.value.isNotEmpty() && validTitle.value
                } else { // if no video check the content
                    createCourseViewModel.lessonTitle.value.isNotEmpty() && validTitle.value
                            && createCourseViewModel.lessonContent.value.trim().isNotEmpty()
                            && validContent.value
                },
                onClick = {
                    createCourseViewModel.addLessonToCurrentCreatingCourse()
                    onSubmitLesson()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MediaSelectDDM(
    createCourseViewModel: CreateCourseViewModel,
    onSelectImage: () -> Unit,
    onSelectVideo: () -> Unit
) {
    val expandedDDM = remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        modifier = Modifier.fillMaxWidth(),
        expanded = expandedDDM.value,
        onExpandedChange = {
            expandedDDM.value = !expandedDDM.value
        }
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = createCourseViewModel.lessonMediaLocalUri.value,
            readOnly = true,
            onValueChange = { },
            label = { Text(text = "Media (Video or Image)") },
            singleLine = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expandedDDM.value
                )
            }
        )
        ExposedDropdownMenu(
            modifier = Modifier.fillMaxWidth(),
            expanded = expandedDDM.value,
            onDismissRequest = {
                expandedDDM.value = false
            }
        ) {
            DropdownMenuItem(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onSelectImage()
                    expandedDDM.value = false
                }
            ) {
                Text(text = "Image")
            }
            DropdownMenuItem(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onSelectVideo()
                    expandedDDM.value = false
                }
            ) {
                Text(text = "Video")
            }
        }
    }
}