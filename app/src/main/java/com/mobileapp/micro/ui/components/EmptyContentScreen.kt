package com.mobileapp.micro.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter

@Composable
fun EmptyContentScreen(
    image: Int,
    text: String
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            modifier = Modifier
                .size(130.dp),
            painter = rememberImagePainter(image),
            contentDescription = "Empty Content",
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = text)
    }
}