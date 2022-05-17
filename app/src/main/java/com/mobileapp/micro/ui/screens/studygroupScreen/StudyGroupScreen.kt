package com.mobileapp.micro.ui.screens.studygroupScreen

import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobileapp.micro.R
import com.mobileapp.micro.model.StudyGroup
import com.mobileapp.micro.ui.components.DefaultImage
import com.mobileapp.micro.ui.components.chatViews.MessageTF
import com.mobileapp.micro.viewmodel.AuthViewModel
import com.mobileapp.micro.viewmodel.CourseViewModel
import com.mobileapp.micro.viewmodel.StudyGroupViewModel
import com.mobileapp.micro.viewmodel.create.CreateStudyGroupViewModel
import kotlinx.coroutines.flow.collect

@Composable
fun StudyGroupScreen(
    onNavigateBack: () -> Unit,
    onGroupSettings: () -> Unit
) {
    val context = LocalContext.current
    val studyGroupViewModel = viewModel<StudyGroupViewModel>(context as ComponentActivity)
    val createStudyGroupViewModel = viewModel<CreateStudyGroupViewModel>(context)
    val authViewModel = viewModel<AuthViewModel>(context)
    val courseViewModel = viewModel<CourseViewModel>(context)

    val isLoadedGroup = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        studyGroupViewModel.studyGroupUiState.collect {
            when (it) {
                is StudyGroupViewModel.StudyGroupUiState.Loading -> {
                }
                is StudyGroupViewModel.StudyGroupUiState.Success -> {
                    isLoadedGroup.value = true
                }
                is StudyGroupViewModel.StudyGroupUiState.Error -> {
                }
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            StudyGroupTopBar(
                group = studyGroupViewModel.currentStudyGroup.value,
                onNavigateBack = {
                    onNavigateBack()
                    studyGroupViewModel.onStudyGroupClosedClean()
                },
                onGroupSettings = {
                    if (studyGroupViewModel.currentStudyGroup.value.authorId == authViewModel.currentUser.value?.uid) {
                        createStudyGroupViewModel.fillGroupUiData(studyGroupViewModel.currentStudyGroup.value)
                        onGroupSettings()
                    }
                }
            )
        },
        content = {
            if (isLoadedGroup.value) {
                StudyGroupScreenContent(
                    studyGroupViewModel = studyGroupViewModel,
                    courseViewModel = courseViewModel
                )
            }
        }
    )
}

@Composable
private fun StudyGroupScreenContent(
    studyGroupViewModel: StudyGroupViewModel,
    courseViewModel: CourseViewModel
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.weight(1F)
        ) {
            StudyGroupMessagesList(
                modifier = Modifier.fillMaxSize(),
                messages = studyGroupViewModel.currentGroupMessages,
                courseViewModel = courseViewModel
            )
            println(">> Debug: ${studyGroupViewModel.currentGroupMessages.size} messages are displayed")

        }
        MessageTF(
            modifier = Modifier,
            text = studyGroupViewModel.currentMessageText,
            onSendMessage = {
                studyGroupViewModel.sendGroupMessage()
            }
        )
    }
}

@Composable
private fun StudyGroupTopBar(
    group: StudyGroup,
    backArrow: Boolean = true,
    onNavigateBack: () -> Unit,
    onGroupSettings: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onGroupSettings() },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DefaultImage(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape),
                    localUri = group.localImageUri,
                    onlineUri = group.onlineImageUri,
                    placeHolder = R.drawable.sample_avatar,
                    error = R.drawable.sample_avatar,
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = group.groupName,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "${group.membersCount} members",
                        style = TextStyle(fontSize = 10.sp)
                    )
                }
            }
        },
        navigationIcon = {
            if (backArrow)
                IconButton(onClick = { onNavigateBack() }) {
                    Icon(Icons.Default.ArrowBack, "Back")
                }
        }
    )
}