package com.mobileapp.micro.ui.screens.authScreens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.mobileapp.micro.R
import com.mobileapp.micro.model.User
import com.mobileapp.micro.ui.components.DefaultButton
import com.mobileapp.micro.ui.components.InfoDialog
import com.mobileapp.micro.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@ExperimentalComposeUiApi
@Composable
fun SignUpScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val authViewModel = viewModel<AuthViewModel>(context as ComponentActivity)
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    val isLoadingBtn = remember { mutableStateOf(false) }
    val infoDialog = remember { mutableStateOf(false) }
    val dialogMessage = remember { mutableStateOf("") }

    if (infoDialog.value)
        InfoDialog(openDialog = infoDialog, text = dialogMessage.value)

    Column(
        modifier = Modifier
            .padding(start = 45.dp, end = 45.dp, bottom = 16.dp, top = 16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val firstName = remember { mutableStateOf("") }
        val lastName = remember { mutableStateOf("") }
        val email = remember { mutableStateOf("") }
        val isValidEmail = remember { mutableStateOf(true) }
        val password = remember { mutableStateOf("") }
        val isValidPassword = remember { mutableStateOf(true) }

        Image(
            painter = rememberImagePainter(R.drawable.sample_avatar),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier =
            Modifier
                .size(150.dp)
                .clip(CircleShape)
                .border(1.dp, Color.LightGray, CircleShape)
        )
        Spacer(Modifier.size(20.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = firstName.value,
            onValueChange = { firstName.value = it },
            leadingIcon = {
                Icon(Icons.Filled.Person, "email")
            },
            label = { Text("First Name") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            )
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = lastName.value,
            onValueChange = { lastName.value = it },
            leadingIcon = {
                Icon(Icons.Filled.Person, "email")
            },
            label = { Text("Last Name") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            )
        )
        EmailTF(email, isValidEmail, keyboardController, focusManager, ImeAction.Next)
        PasswordTF(password, isValidPassword, keyboardController, focusManager, ImeAction.Done)
        Spacer(Modifier.size(8.dp))
        DefaultButton(
            text = "Register",
            enable = firstName.value.isNotEmpty()
                    && lastName.value.isNotEmpty()
                    && email.value.isNotEmpty()
                    && password.value.isNotEmpty()
                    && isValidEmail.value
                    && isValidPassword.value
                    && !isLoadingBtn.value,
            isLoadingBtn = isLoadingBtn,
            onClick = {
                val newUser = User(
                    firstName.value.trim(),
                    lastName.value.trim(),
                    email.value.trim(),
                    password.value.trim()
                )
                coroutineScope.launch {
                    authViewModel.authUiState.collect {
                        when (it) {
                            is AuthViewModel.AuthUiState.Loading -> {
                                isLoadingBtn.value = true
                            }
                            is AuthViewModel.AuthUiState.Success -> {
                                isLoadingBtn.value = false
                                onNavigateBack()
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
                authViewModel.signUp(newUser)
            }
        )
        Spacer(Modifier.size(32.dp))
        Divider()
        Spacer(Modifier.size(16.dp))
        Row {
            Text(text = "Already a member?  ")
            Text(
                modifier = Modifier.clickable {
                    onNavigateBack()
                },
                text = "Log in",
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