package com.mobileapp.micro.ui.screens.authScreens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@ExperimentalComposeUiApi
@Composable
fun EmailTF(
    email: MutableState<String>,
    isValidEmail: MutableState<Boolean>,
    keyboardController: SoftwareKeyboardController?,
    focusManager: FocusManager,
    onDoneAction: ImeAction
) {
    val message = remember { mutableStateOf("") }
    if (isValidEmail.value)
        message.value = ""
    else
        message.value = "Invalid Email"
    Box {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 0.dp),
            value = email.value,
            onValueChange = {
                email.value = it
                isValidEmail.value = isValidEmail(email.value)
            },
            leadingIcon = {
                Icon(Icons.Default.Email, "email")
            },
            placeholder = { Text("example@mail.com") },
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = onDoneAction
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                },
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            isError = !isValidEmail.value,
            trailingIcon = {
                if (!isValidEmail.value)
                    Icon(Icons.Default.Error, "error", tint = MaterialTheme.colors.error)
            },
        )
        Text(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 4.dp, bottom = 2.dp),
            text = message.value,
            color = MaterialTheme.colors.error,
            style = MaterialTheme.typography.caption
        )
    }
}


fun isValidEmail(email: String): Boolean {
    return if (email.isEmpty() || email.isBlank())
        false
    else
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}