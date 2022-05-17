package com.mobileapp.micro.ui.screens.mainScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.mobileapp.micro.R
import com.mobileapp.micro.Screen

@Composable
fun NavDrawer(
) {
    Column {
        // Header
        Image(
            painter = painterResource(R.drawable.micro_blue),
            contentDescription = null,
            modifier = Modifier
                .size(150.dp)
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        )
        Divider()
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f)
        ) {

        }
        Text(
            modifier = Modifier
                .padding(12.dp)
                .align(Alignment.CenterHorizontally),
            text = "Developed by Micro Team\nCopyright Reserved \u00a9",
            textAlign = TextAlign.Center,
            fontSize = 12.sp
        )
    }
}

@Composable
fun DrawerItem(item: Screen, selected: Boolean, onItemClick: (Screen) -> Unit) {
    val background = if (selected)
        MaterialTheme.colors.primaryVariant
    else
        MaterialTheme.colors.primarySurface

    if (item.title == "Divider") {
        Divider()
    } else {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { onItemClick(item) })
                .height(50.dp)
                //.background(background)
                .padding(start = 10.dp)
        ) {
            // For each screen either an icon or vector resource is provided
            val icon = item.icon ?: ImageVector.vectorResource(item.iconResourceId!!)
            Spacer(modifier = Modifier.width(10.dp))
            Image(
                imageVector = icon,
                contentDescription = item.title,
                colorFilter = ColorFilter.tint(Color.Gray),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = item.title,
                fontSize = 18.sp,
                //color = Color.White
            )
        }
    }
}

@Preview
@Composable
fun DrawerPreview() {
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val navController = rememberNavController()
    NavDrawer()
}