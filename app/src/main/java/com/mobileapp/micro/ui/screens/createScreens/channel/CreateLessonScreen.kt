package com.mobileapp.micro.ui.screens.createScreens.channel

import android.media.MediaPlayer
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobileapp.micro.common.toastMessage
import com.mobileapp.micro.model.ContentConstrains
import com.mobileapp.micro.model.MediaType
import com.mobileapp.micro.ui.components.ConstraintTF
import com.mobileapp.micro.ui.components.DefaultButton
import com.mobileapp.micro.ui.components.SimpleTopBar
import com.mobileapp.micro.viewmodel.ChannelViewModel
import com.mobileapp.micro.viewmodel.create.CreateChannelViewModel


@Composable
fun EditChannelLesson(
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val channelViewModel = viewModel<ChannelViewModel>(context as ComponentActivity)
    Scaffold(
        topBar = {
            SimpleTopBar(
                onNavigateBack = onNavigateBack,
                backArrow = true,
                title = "Lesson Editor"
            )
        },
        content = {
            CreateLessonScreenContent(channelViewModel, onNavigateBack)
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun CreateLessonScreenContent(
    channelViewModel: ChannelViewModel,
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
        val mediaPicker = rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            println(">> Debug: Select image Uri:$uri")
            uri?.let {
                val fileName = DocumentFile.fromSingleUri(context, it)?.name
                context.contentResolver.getType(uri)?.let { type ->
                    if (type.contains("video")) {
                        var durationTime: Long
                        MediaPlayer.create(context, uri).also { mediaPlayer ->
                            durationTime = (mediaPlayer.duration / 1000).toLong()
                            mediaPlayer.reset()
                            mediaPlayer.release()
                        }
                        if (durationTime <= ContentConstrains.LESSON_VIDEO_MAX_DURATION_SECONDS) {
                            channelViewModel.lessonMediaName.value = fileName ?: ""
                            channelViewModel.lessonMediaUri.value = it.toString()
                            channelViewModel.mediaType.value = MediaType.VIDEO
                            videoMediaChosen.value = true
                        } else {
                            toastMessage(context, "Video duration is more that 2 minutes")
                        }
                    } else if (type.contains("image")) {
                        channelViewModel.lessonMediaName.value = fileName ?: ""
                        channelViewModel.lessonMediaUri.value = it.toString()
                        channelViewModel.mediaType.value = MediaType.IMAGE
                        videoMediaChosen.value = false
                    } else {
                        channelViewModel.mediaType.value = MediaType.NON
                        videoMediaChosen.value = false
                    }
                }
            }
        }
        val validTitle = remember { mutableStateOf(true) }
        val validContent = remember { mutableStateOf(true) }
        ConstraintTF(
            textCharLimit = ContentConstrains.LESSON_TITLE_MAX_LENGTH,
            text = channelViewModel.lessonTitle,
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
            channelViewModel,
            onSelectImage = { mediaPicker.launch("image/*") },
            onSelectVideo = { mediaPicker.launch("video/*") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        ConstraintTF(
            textCharLimit = ContentConstrains.LESSON_CONTENT_MAX_LENGTH,
            text = channelViewModel.lessonContent,
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
                    channelViewModel.clearLessonEditorUiData()
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            DefaultButton(
                modifier = Modifier.weight(1f),
                text = "Add",
                enable = if (videoMediaChosen.value) { // video not need content
                    channelViewModel.lessonTitle.value.isNotEmpty() && validTitle.value
                } else { // if no video check the content
                    channelViewModel.lessonTitle.value.isNotEmpty() && validTitle.value
                            && channelViewModel.lessonContent.value.trim().isNotEmpty()
                            && validContent.value
                },
                onClick = {
                    channelViewModel.addLessonToChannel()
                    onSubmitLesson()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MediaSelectDDM(
    channelViewModel: ChannelViewModel,
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
            value = channelViewModel.lessonMediaName.value,
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