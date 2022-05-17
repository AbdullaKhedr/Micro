package com.mobileapp.micro.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mobileapp.micro.ui.theme.MicroTheme

@ExperimentalComposeUiApi
@Composable
fun ConstraintTF(
    textCharLimit: Int,
    text: MutableState<String>,
    isValidText: MutableState<Boolean>,
    constraintMessage: String,
    label: String,
    placeHolder: String,
    singleLine: Boolean,
    keyboardController: SoftwareKeyboardController?,
    focusManager: FocusManager,
    onDoneAction: ImeAction,
    modifier: Modifier = Modifier
) {
    val message = remember { mutableStateOf("") }
    if (isValidText.value)
        message.value = ""
    else
        message.value = constraintMessage
    Box(modifier = modifier) {
        OutlinedTextField(
            modifier = Modifier.fillMaxSize(),
            value = text.value,
            onValueChange = {
                text.value = it
                isValidText.value = text.value.length <= textCharLimit
            },
            placeholder = { Text(placeHolder) },
            label = { Text(label) },
            singleLine = singleLine,
            keyboardOptions = KeyboardOptions(
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
            isError = !isValidText.value,
            trailingIcon = {
                if (!isValidText.value)
                    Icon(Icons.Filled.Error, "error", tint = MaterialTheme.colors.error)
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


@OptIn(ExperimentalComposeUiApi::class)
@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
private fun ConstraintTFPreview() {

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    MicroTheme {
        Surface {
            ConstraintTF(
                100,
                mutableStateOf(""),
                mutableStateOf(true),
                "text must be less that 100 chars",
                "Test TF",
                "text...",
                false,
                keyboardController,
                focusManager,
                ImeAction.Next
            )
        }
    }
}