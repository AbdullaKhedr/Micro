package com.mobileapp.micro.ui.screens.authScreens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobileapp.micro.R
import com.mobileapp.micro.common.toastMessage
import com.mobileapp.micro.ui.components.DefaultButton
import com.mobileapp.micro.ui.components.InfoDialog
import com.mobileapp.micro.ui.components.ResetPasswordDialog
import com.mobileapp.micro.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onSignup: () -> Unit
) {
    val context = LocalContext.current
    val authViewModel = viewModel<AuthViewModel>(context as ComponentActivity)
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val openResetPassDialog = remember { mutableStateOf(false) }
    val isLoadingBtn = remember { mutableStateOf(false) }
    val infoDialog = remember { mutableStateOf(false) }
    val dialogMessage = remember { mutableStateOf("") }

    if (infoDialog.value)
        InfoDialog(openDialog = infoDialog, title = "Error", text = dialogMessage.value)

    if (openResetPassDialog.value)
        ResetPasswordDialog(openResetPassDialog, authViewModel)
    /*
    ResetPasswordDialog(
            isOpenDialog = openResetPassDialog,
            email = resetPasswordEmail,
            onSubmitEmail = { authViewModel.resetPass(resetPasswordEmail.value) }
        )
    */

    Column(
        modifier = Modifier
            .padding(start = 45.dp, end = 45.dp, bottom = 16.dp, top = 16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        val email = remember { mutableStateOf("") }
        val isValidEmail = remember { mutableStateOf(true) }
        val password = remember { mutableStateOf("") }
        val isValidPassword = remember { mutableStateOf(true) }

        Image(
            painter = painterResource(id = R.drawable.micro_blue),
            contentDescription = "The Logo",
            modifier = Modifier.size(160.dp)
        )
        Spacer(Modifier.size(32.dp))
        EmailTF(email, isValidEmail, keyboardController, focusManager, ImeAction.Next)
        PasswordTF(password, isValidPassword, keyboardController, focusManager, ImeAction.Done)

        Spacer(Modifier.size(8.dp))

        DefaultButton(
            text = "Log In",
            enable = email.value.isNotEmpty()
                    && password.value.isNotEmpty()
                    && isValidEmail.value
                    && isValidPassword.value
                    && !isLoadingBtn.value,
            onClick = {
                coroutineScope.launch {
                    authViewModel.authUiState.collect {
                        when (it) {
                            is AuthViewModel.AuthUiState.Loading -> {
                                isLoadingBtn.value = true
                            }
                            is AuthViewModel.AuthUiState.Success -> {
                                isLoadingBtn.value = false
                                onLoginSuccess()
                            }
                            is AuthViewModel.AuthUiState.Error -> {
                                isLoadingBtn.value = false
                                dialogMessage.value = it.message
                                infoDialog.value = true
                            }
                            else -> {
                                isLoadingBtn.value = false
                            }
                        }
                    }
                }
                authViewModel.signIn(email.value.trim(), password.value.trim())
            },
            isLoadingBtn = isLoadingBtn
        )

        Spacer(Modifier.size(16.dp))
        ClickableText( // Can ba a normal Text() also!
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = AnnotatedString("Forgot password?"),
            style = TextStyle(color = Color.Blue),
            onClick = {
                openResetPassDialog.value = true
            }
        )
        Spacer(Modifier.size(32.dp))
        Divider()
        Spacer(Modifier.size(16.dp))
        Row {
            Text(text = "You are not a member?  ")
            Text(
                modifier = Modifier.clickable {
                    onSignup()
                },
                text = "Register",
                style = TextStyle(
                    color = Color.Blue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )
        }
        Spacer(Modifier.size(16.dp))
    }
}
