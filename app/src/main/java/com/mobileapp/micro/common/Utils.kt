package com.mobileapp.micro.common

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun getMediaPicker(
    onMediaSelected: (uri: Uri) -> Unit
) = rememberLauncherForActivityResult(
    ActivityResultContracts.GetContent()
) { uri ->
    println(">> Debug: Select image Uri: $uri")
    uri?.let {
        onMediaSelected(it)
    }
}

@Composable
fun getCurrentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

fun toastMessage(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

fun getDateFormatted(date: Date, formatPattern: String): String {
    var result = ""
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val localDate = date.toInstant()
            .atZone(ZoneId.systemDefault()).toLocalDateTime()
        val formatter = DateTimeFormatter.ofPattern(formatPattern)
        result = localDate.format(formatter).toString()
    }
    return result
}

fun shareTextContent(context: Context, content: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, content)
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(intent, "Share via"))
}