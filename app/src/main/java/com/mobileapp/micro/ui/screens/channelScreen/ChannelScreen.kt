package com.mobileapp.micro.ui.screens.channelScreen

import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobileapp.micro.R
import com.mobileapp.micro.model.Channel
import com.mobileapp.micro.ui.components.DefaultImage
import com.mobileapp.micro.viewmodel.AuthViewModel
import com.mobileapp.micro.viewmodel.ChannelViewModel
import com.mobileapp.micro.viewmodel.CourseViewModel
import com.mobileapp.micro.viewmodel.create.CreateChannelViewModel

@Composable
fun ChannelScreen(
    onNavigateBack: () -> Unit,
    onCreateLesson: () -> Unit,
    onChannelSettings: () -> Unit
) {
    val context = LocalContext.current
    val channelViewModel = viewModel<ChannelViewModel>(context as ComponentActivity)
    val createChannelViewModel = viewModel<CreateChannelViewModel>(context)
    val courseViewModel = viewModel<CourseViewModel>(context)
    val authViewModel = viewModel<AuthViewModel>(context)

    Scaffold(
        topBar = {
            ChannelTopBar(
                channel = channelViewModel.currentChannel.value,
                onNavigateBack = {
                    onNavigateBack()
                    channelViewModel.cleanUiData()
                },
                onChannelSettings = {
                    if(channelViewModel.currentChannel.value.authorId == authViewModel.currentUser.value?.uid){
                        createChannelViewModel.fillChannelUiData(channelViewModel.currentChannel.value)
                        onChannelSettings()
                    }
                }
            )
        },
        floatingActionButton = {
            if (channelViewModel.currentChannel.value.authorId == authViewModel.getAuthState()?.uid ?: "")
                ExtendedFloatingActionButton(
                    onClick = { onCreateLesson() },
                    text = { Text(text = "New Lesson") },
                    icon = { Icon(imageVector = Icons.Outlined.Add, contentDescription = null) }
                )
        },
        content = {
            ChannelScreenContent(
                channelViewModel = channelViewModel,
                courseViewModel = courseViewModel
            )
        }
    )
}

@Composable
private fun ChannelScreenContent(
    channelViewModel: ChannelViewModel,
    courseViewModel: CourseViewModel
) {
    Column(modifier = Modifier.fillMaxSize()) {
        ChannelLessonsList(
            courseViewModel = courseViewModel,
            modifier = Modifier.fillMaxSize(),
            messages = channelViewModel.currentChannelLessons
        )
    }
}

@Composable
private fun ChannelTopBar(
    channel: Channel,
    backArrow: Boolean = true,
    onNavigateBack: () -> Unit,
    onChannelSettings: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onChannelSettings() },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DefaultImage(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape),
                    localUri = channel.localImageUri,
                    onlineUri = channel.onlineImageUri,
                    placeHolder = R.drawable.sample_avatar,
                    error = R.drawable.sample_avatar,
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = channel.channelName,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "${channel.membersCount} members",
                        style = TextStyle(fontSize = 10.sp)
                    )
                }
            }
        },
        navigationIcon = {
            if (backArrow)
                IconButton(onClick = { onNavigateBack() }) {
                    Icon(Icons.Default.ArrowBack, "Back")
                }
        }
    )
}