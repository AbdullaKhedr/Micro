package com.mobileapp.micro.ui.screens.authScreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation


@ExperimentalComposeUiApi
@Composable
fun PasswordTF(
    password: MutableState<String>,
    isValidPass: MutableState<Boolean>,
    keyboardController: SoftwareKeyboardController?,
    focusManager: FocusManager,
    onDoneAction: ImeAction
) {
    val message = remember { mutableStateOf("") }
    val isVisible = remember { mutableStateOf(false) }
    Column {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = password.value,
            onValueChange = {
                password.value = it
                isValidPass.value = isValidPass(password.value)
            },
            leadingIcon = {
                Icon(Icons.Filled.Lock, "lock")
            },
            label = { Text("Password") },
            singleLine = true,
            placeholder = { Text("********") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = onDoneAction
            ),
            visualTransformation = if (isVisible.value) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(onClick = { isVisible.value = !isVisible.value }) {
                    Icon(
                        imageVector = if (isVisible.value) {
                            Icons.Filled.Visibility
                        } else {
                            Icons.Filled.VisibilityOff
                        }, contentDescription = null
                    )
                }
            },
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                },
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            isError = !isValidPass.value
        )
        if (isValidPass.value)
            message.value = ""
        else
            message.value = "The password should not be less than six characters"
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = message.value,
            color = MaterialTheme.colors.error,
            style = MaterialTheme.typography.caption
        )
    }
}


fun isValidPass(password: String): Boolean {
    val passwordMinCharLength = 6
    return password.length >= passwordMinCharLength
}