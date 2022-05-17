package com.mobileapp.micro.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.mobileapp.micro.common.toastMessage
import com.mobileapp.micro.ui.screens.authScreens.EmailTF
import com.mobileapp.micro.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@ExperimentalComposeUiApi
@Composable
fun ResetPasswordDialog(
    isDialogOpen: MutableState<Boolean>,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val email = remember { mutableStateOf("") }
    val isValid = remember { mutableStateOf(true) }

    Dialog(
        onDismissRequest = { isDialogOpen.value = false }
    ) {
        Surface(
            shape = RoundedCornerShape(5.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = "Reset Password",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.size(16.dp))
                EmailTF(email, isValid, keyboardController, focusManager, ImeAction.Done)
                Spacer(modifier = Modifier.size(16.dp))
                Row {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                authViewModel.authUiState.collect {
                                    when (it) {
                                        is AuthViewModel.AuthUiState.Success -> {
                                            isDialogOpen.value = false
                                            toastMessage(context, "Email Sent Successfully")
                                        }
                                        is AuthViewModel.AuthUiState.Error -> {
                                            toastMessage(context, it.message)
                                        }
                                        else -> Unit
                                    }
                                }
                            }
                            authViewModel.resetPass(email.value)
                        },
                        enabled = isValid.value && email.value.isNotEmpty()
                    ) {
                        Text("Reset password")
                    }
                    Spacer(Modifier.size(8.dp))
                    TextButton(
                        onClick = { isDialogOpen.value = false }
                    ) {
                        Text("Dismiss")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddFriendDialog(
    isOpenDialog: MutableState<Boolean>,
    email: MutableState<String>,
    onSubmitEmail: () -> Unit
) {
    val isValidEmail = remember { mutableStateOf(true) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    AlertDialog(
        title = { Text(text = "Add Friend") },
        text = {
            Column {
                Text(text = "Enter a valid user email")
                Spacer(modifier = Modifier.height(8.dp))
                EmailTF(
                    email = email,
                    isValidEmail = isValidEmail,
                    keyboardController = keyboardController,
                    focusManager = focusManager,
                    onDoneAction = ImeAction.Done
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = isValidEmail.value && email.value.isNotEmpty(),
                onClick = { onSubmitEmail(); isOpenDialog.value = false; email.value = "" }
            ) { Text(text = "Add") }
        },
        dismissButton = {
            TextButton(onClick = { isOpenDialog.value = false; email.value = "" }) {
                Text(text = "Cancel")
            }
        },
        onDismissRequest = { isOpenDialog.value = false }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ResetPasswordDialog(
    isOpenDialog: MutableState<Boolean>,
    email: MutableState<String>,
    onSubmitEmail: () -> Unit
) {
    val isValidEmail = remember { mutableStateOf(true) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    AlertDialog(
        title = { Text(text = "") /*Reset Password*/ },
        text = {
            EmailTF(
                email = email,
                isValidEmail = isValidEmail,
                keyboardController = keyboardController,
                focusManager = focusManager,
                onDoneAction = ImeAction.Done
            )
        },
        confirmButton = {
            TextButton(
                enabled = !isValidEmail.value,
                onClick = { onSubmitEmail(); isOpenDialog.value = false }
            ) { Text(text = "Submit") }
        },
        dismissButton = {
            TextButton(onClick = { isOpenDialog.value = false }) {
                Text(text = "Cancel")
            }
        },
        onDismissRequest = { isOpenDialog.value = false }
    )
}