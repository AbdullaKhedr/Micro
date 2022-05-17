package com.mobileapp.micro.ui.components

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

@Composable
fun SimpleAlertDialog(
    isOpenDialog: MutableState<Boolean>,
    title: String,
    text: @Composable () -> Unit,
    onSubmit: () -> Unit,
    onDismiss: (() -> Unit)? = null,
) {
    AlertDialog(
        title = { Text(text = title) },
        text = { text() },
        confirmButton = {
            TextButton(onClick = { onSubmit() }) { Text(text = "Confirm") }
        },
        dismissButton = {
            TextButton(onClick = {
                if (onDismiss != null) onDismiss()
                isOpenDialog.value = false
            }) {
                Text(text = "Dismiss")
            }
        },
        onDismissRequest = {
            if (onDismiss != null) {
                onDismiss()
            }
            isOpenDialog.value = false
        }
    )
}