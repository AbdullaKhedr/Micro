package com.mobileapp.micro.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DefaultButton(
    modifier: Modifier = Modifier,
    text: String,
    enable: Boolean = true,
    onClick: () -> Unit,
    isLoadingBtn: MutableState<Boolean> = mutableStateOf(false)
) {
    Button(
        modifier = modifier.fillMaxWidth(),
        enabled = enable,
        onClick = { onClick() }
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            if (isLoadingBtn.value)
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.Center)
                )
        }
    }
}

@Composable
@Preview
fun DefaultFullButtonPreview() {
    Surface {
        Column {
            DefaultButton(
                text = "Confirm",
                enable = true,
                onClick = {},
                isLoadingBtn = remember { mutableStateOf(true) }
            )
        }
    }
}
