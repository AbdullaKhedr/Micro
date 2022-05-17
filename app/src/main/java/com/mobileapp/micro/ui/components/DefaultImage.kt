package com.mobileapp.micro.ui.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter

@OptIn(ExperimentalCoilApi::class)
@Composable
fun DefaultImage(
    modifier: Modifier = Modifier,
    localUri: String,
    onlineUri: String,
    placeHolder: Int,
    error: Int,
    contentScale: ContentScale,
    loadingIndicator: Boolean = false
) {
    val context = LocalContext.current
    val localImageExist = DocumentFile.fromSingleUri(context, Uri.parse(localUri))?.exists()
    val painter = rememberImagePainter(
        data = if (localImageExist == true) localUri else onlineUri,
        builder = {
            placeholder(placeHolder)
            error(error)
        }
    )
    val painterState = painter.state
    Box(
        modifier = modifier
    ) {
        Image(
            modifier = modifier,
            painter = painter,
            contentDescription = null,
            contentScale = contentScale
        )
        if (painterState is ImagePainter.State.Loading && loadingIndicator) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}