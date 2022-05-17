package com.mobileapp.micro.ui.components.chatViews

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun MessageTF(
    modifier: Modifier,
    text: MutableState<String>,
    onSendMessage: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = text.value,
            onValueChange = { text.value = it },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions {
                onSendMessage()
                text.value = ""
            },
            trailingIcon = {
                IconButton(
                    enabled = text.value.isNotBlank(),
                    onClick = {
                        onSendMessage()
                        text.value = ""
                    }
                ) {
                    Icon(imageVector = Icons.Filled.Send, contentDescription = "Send")
                }
            }
        )
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
fun MessageTFPreview() {
    Surface {
        MessageTF(
            Modifier,
            mutableStateOf(""),
            {}
        )
    }
}
