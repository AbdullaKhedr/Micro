package com.mobileapp.micro.ui.components

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

@Composable
fun InfoDialog(
    openDialog: MutableState<Boolean>,
    title: String = "",
    text: String
) {
    AlertDialog(
        title = { Text(text = title) },
        text = { Text(text = text) },
        confirmButton = {
            TextButton(
                onClick = { openDialog.value = false }
            ) { Text(text = "Close") }
        },
        onDismissRequest = { openDialog.value = true }
    )
}