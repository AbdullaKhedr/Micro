package com.mobileapp.micro.ui.screens.createScreens.channel

import androidx.activity.ComponentActivity
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
import com.mobileapp.micro.common.getMediaPicker
import com.mobileapp.micro.ui.components.*
import com.mobileapp.micro.ui.screens.authScreens.isValidEmail
import com.mobileapp.micro.viewmodel.ChannelViewModel
import com.mobileapp.micro.viewmodel.UserViewModel
import com.mobileapp.micro.viewmodel.create.CreateChannelViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
fun CreateChannelScreen(
    onNavigateBack: () -> Unit,
    onCreateChannel: () -> Unit
) {
    val context = LocalContext.current
    val createChannelViewModel = viewModel<CreateChannelViewModel>(context as ComponentActivity)
    val channelViewModel = viewModel<ChannelViewModel>(context)
    val userViewModel = viewModel<UserViewModel>(context)
    Scaffold(
        topBar = {
            SimpleTopBar(
                onNavigateBack = {
                    onNavigateBack()
                    createChannelViewModel.clearChannelUiData()
                },
                backArrow = true,
                title = "Channel"
            )
        },
        content = {
            CreateChannelScreenContent(
                createChannelViewModel = createChannelViewModel,
                channelViewModel = channelViewModel,
                onCreateChannel = onCreateChannel,
                userViewModel = userViewModel,
            )
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CreateChannelScreenContent(
    createChannelViewModel: CreateChannelViewModel,
    channelViewModel: ChannelViewModel,
    userViewModel: UserViewModel,
    onCreateChannel: () -> Unit
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

        val imagePicker = getMediaPicker { uri ->
            val fileName = uri.lastPathSegment
            createChannelViewModel.displayImageName.value = fileName ?: ""
            createChannelViewModel.localImageUri.value = uri.toString()
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
                localUri = createChannelViewModel.localImageUri.value,
                onlineUri = createChannelViewModel.localImageUri.value,
                placeHolder = R.drawable.empty_image,
                error = R.drawable.empty_image,
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = createChannelViewModel.channelName.value,
                onValueChange = { createChannelViewModel.channelName.value = it },
                label = { Text("Channel Name") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Down) }),
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Column {
            val newMemberEmail = remember { mutableStateOf("") }
            val isValidEmail = remember { mutableStateOf(true) }
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = createChannelViewModel.channelDescription.value,
                onValueChange = { createChannelViewModel.channelDescription.value = it },
                label = { Text("Channel Description") },
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
                            createChannelViewModel.channelMemberEmails.add(newMemberEmail.value)
                            val distinctValues =
                                createChannelViewModel.channelMemberEmails.distinct()
                            createChannelViewModel.channelMemberEmails.clear()
                            createChannelViewModel.channelMemberEmails.addAll(distinctValues)
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
                items(createChannelViewModel.channelMemberEmails) {
                    MemberItem(
                        email = it,
                        onDelete = {
                            createChannelViewModel.channelMemberEmails.remove(it)
                        }
                    )
                    Divider()
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            DefaultButton(
                text = "Done",
                enable = createChannelViewModel.channelName.value.isNotEmpty(),
                onClick = {
                    coroutineScope.launch {
                        createChannelViewModel.createChannelUiState.collect {
                            when (it) {
                                is CreateChannelViewModel.CreateChannelUiState.Loading -> {
                                    progressDialog.value = true
                                }
                                is CreateChannelViewModel.CreateChannelUiState.Success -> {
                                    progressDialog.value = false
                                    if (it.createdChannelId != null) {
                                        channelViewModel.currentChannelId.value =
                                            it.createdChannelId
                                        if (channelViewModel.currentChannelId.value.isNotEmpty()) {
                                            channelViewModel.loadChannel()
                                        }
                                    }
                                    onCreateChannel()
                                }
                                is CreateChannelViewModel.CreateChannelUiState.Error -> {
                                    progressDialog.value = false
                                }
                                else -> {}
                            }
                        }
                    }
                    createChannelViewModel.createChannel()
                }
            )
        }
    }
}