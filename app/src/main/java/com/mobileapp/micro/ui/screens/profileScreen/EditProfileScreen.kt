package com.mobileapp.micro.ui.screens.profileScreen

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobileapp.micro.R
import com.mobileapp.micro.common.getMediaPicker
import com.mobileapp.micro.ui.components.DefaultButton
import com.mobileapp.micro.ui.components.DefaultImage
import com.mobileapp.micro.ui.components.SimpleTopBar
import com.mobileapp.micro.viewmodel.AuthViewModel
import com.mobileapp.micro.viewmodel.UserViewModel

@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val profileViewModel = viewModel<UserViewModel>(context as ComponentActivity)
    val authViewModel = viewModel<AuthViewModel>(context)
    Scaffold(
        topBar = {
            SimpleTopBar(
                onNavigateBack = onNavigateBack,
                title = "Edit Profile"
            )
        },
        content = {
            EditProfileScreenContent(
                userViewModel = profileViewModel,
                authViewModel = authViewModel,
                onNavigateBack = onNavigateBack,
            )
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun EditProfileScreenContent(
    userViewModel: UserViewModel,
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit
) {
    val photoPicker = getMediaPicker { localUri ->
        val fileName = localUri.lastPathSegment
        userViewModel.photoName.value = fileName.toString()
        userViewModel.localPhotoUri.value = localUri.toString()
    }
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            DefaultImage(
                modifier = Modifier
                    .size(170.dp)
                    .padding(16.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.LightGray, CircleShape)
                    .align(Alignment.Center),
                localUri = userViewModel.localPhotoUri.value,
                onlineUri = userViewModel.currentUser.onlinePhotoUri,
                placeHolder = R.drawable.sample_avatar,
                error = R.drawable.sample_avatar,
                contentScale = ContentScale.Crop
            )
            IconButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .clip(CircleShape)
                    .background(MaterialTheme.colors.secondary),
                onClick = {
                    photoPicker.launch("image/*")
                }
            ) {
                Icon(imageVector = Icons.Outlined.PhotoCamera, contentDescription = null)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        DataTF(
            label = "First Name",
            data = userViewModel.firstName,
            keyboardController = keyboardController,
            focusManager = focusManager
        )
        Spacer(modifier = Modifier.height(8.dp))
        DataTF(
            label = "Last Name",
            data = userViewModel.lastName,
            keyboardController = keyboardController,
            focusManager = focusManager
        )
        Spacer(modifier = Modifier.height(8.dp))
        DataTF(
            label = "Bio",
            data = userViewModel.bio,
            keyboardController = keyboardController,
            focusManager = focusManager,
            singleLine = false,
            imeAction = ImeAction.Done
        )
        Spacer(modifier = Modifier.height(8.dp))
        DataTF(
            label = "Email",
            data = remember { mutableStateOf(userViewModel.currentUser.email) },
            keyboardController = keyboardController,
            focusManager = focusManager,
            enable = false
        )
        Spacer(modifier = Modifier.height(16.dp))
        DefaultButton(
            text = "Update",
            onClick = {
                userViewModel.updateUser()
                onNavigateBack()
            }
        )

    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun DataTF(
    label: String,
    data: MutableState<String>,
    keyboardController: SoftwareKeyboardController?,
    focusManager: FocusManager,
    enable: Boolean = true,
    singleLine: Boolean = true,
    imeAction: ImeAction = ImeAction.Next
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = data.value,
        onValueChange = { data.value = it },
        label = { Text(label) },
        enabled = enable,
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
            },
            onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }
        )
    )
}