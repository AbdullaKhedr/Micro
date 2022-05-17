package com.mobileapp.micro.ui.screens.createScreens.studygroup

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobileapp.micro.R
import com.mobileapp.micro.ui.components.*
import com.mobileapp.micro.ui.screens.authScreens.isValidEmail
import com.mobileapp.micro.viewmodel.StudyGroupViewModel
import com.mobileapp.micro.viewmodel.create.CreateStudyGroupViewModel
import kotlinx.coroutines.launch

@Composable
fun CreateStudyGroupScreen(
    onNavigateBack: () -> Unit,
    onCreateGroup: () -> Unit
) {
    val context = LocalContext.current
    val createStudyGroupViewModel =
        viewModel<CreateStudyGroupViewModel>(context as ComponentActivity)
    val studyGroupViewModel = viewModel<StudyGroupViewModel>(context)
    Scaffold(
        topBar = {
            SimpleTopBar(
                onNavigateBack = {
                    onNavigateBack()
                    createStudyGroupViewModel.clearUIData()
                },
                backArrow = true,
                title = "Study Group"
            )
        },
        content = {
            CreateStudyGroupScreenContent(
                createStudyGroupViewModel = createStudyGroupViewModel,
                studyGroupViewModel = studyGroupViewModel,
                onCreateGroup = onCreateGroup
            )
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun CreateStudyGroupScreenContent(
    createStudyGroupViewModel: CreateStudyGroupViewModel,
    studyGroupViewModel: StudyGroupViewModel,
    onCreateGroup: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val progressDialog = remember { mutableStateOf(false) }
        if (progressDialog.value)
            LoadingDialog(dialogState = progressDialog, message = "")

        val imagePicker = rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            println(">> Debug: Select image Uri: $uri")
            uri?.let {
                val fileName = uri.lastPathSegment
                createStudyGroupViewModel.displayImageName.value = fileName ?: ""
                createStudyGroupViewModel.localImageUri.value = uri.toString()
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DefaultImage(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.LightGray, CircleShape)
                    .clickable {
                        imagePicker.launch("image/*")
                    },
                localUri = createStudyGroupViewModel.localImageUri.value,
                onlineUri = createStudyGroupViewModel.localImageUri.value,
                placeHolder = R.drawable.empty_image,
                error = R.drawable.empty_image,
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = createStudyGroupViewModel.groupName.value,
                onValueChange = { createStudyGroupViewModel.groupName.value = it },
                label = { Text("Group Name") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Down) }),
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Column {
            val newMemberEmail = remember { mutableStateOf("") }
            val isValidEmail = remember { mutableStateOf(false) }
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = createStudyGroupViewModel.groupDescription.value,
                onValueChange = { createStudyGroupViewModel.groupDescription.value = it },
                label = { Text("Group Description") },
                singleLine = false,
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = newMemberEmail.value,
                onValueChange = {
                    newMemberEmail.value = it
                    isValidEmail.value = isValidEmail(newMemberEmail.value)
                },
                placeholder = { Text("example@email.com") },
                label = { Text("Add Members") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                trailingIcon = {
                    IconButton(
                        enabled = isValidEmail.value && newMemberEmail.value.isNotEmpty(),
                        onClick = {
                            createStudyGroupViewModel.groupMemberEmails.add(newMemberEmail.value)
                            val distinctValues =
                                createStudyGroupViewModel.groupMemberEmails.distinct()
                            createStudyGroupViewModel.groupMemberEmails.clear()
                            createStudyGroupViewModel.groupMemberEmails.addAll(distinctValues)
                            newMemberEmail.value = ""
                        }
                    ) {
                        Icon(Icons.Default.Add, null)
                    }
                },
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(createStudyGroupViewModel.groupMemberEmails) {
                    MemberItem(
                        email = it,
                        onDelete = {
                            createStudyGroupViewModel.groupMemberEmails.remove(it)
                        }
                    )
                    Divider()
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            DefaultButton(
                text = "Done",
                enable = createStudyGroupViewModel.groupName.value.isNotEmpty(),
                onClick = {
                    coroutineScope.launch {
                        createStudyGroupViewModel.createStudyGroupUiState.collect {
                            when (it) {
                                is CreateStudyGroupViewModel.CreateStudyGroupUiState.Loading -> {
                                    progressDialog.value = true
                                }
                                is CreateStudyGroupViewModel.CreateStudyGroupUiState.Success -> {
                                    progressDialog.value = false
                                    if (it.createdGroupId != null) {
                                        studyGroupViewModel.currentStudyGroupId.value =
                                            it.createdGroupId
                                        if (studyGroupViewModel.currentStudyGroupId.value.isNotEmpty()) {
                                            studyGroupViewModel.loadCurrentStudyGroup()
                                            studyGroupViewModel.loadCurrentGroupMessages()
                                        }
                                    }
                                    Log.d(
                                        "Current Study Group id",
                                        studyGroupViewModel.currentStudyGroupId.value
                                    )
                                    onCreateGroup()
                                }
                                is CreateStudyGroupViewModel.CreateStudyGroupUiState.Error -> {
                                    progressDialog.value = false
                                }
                                else -> {}
                            }
                        }
                    }
                    createStudyGroupViewModel.createStudyGroup()
                }
            )
        }
    }
}