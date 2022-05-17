package com.mobileapp.micro.ui.screens.myLearningScreens.subPages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobileapp.micro.R
import com.mobileapp.micro.model.Channel
import com.mobileapp.micro.ui.components.DefaultImage
import com.mobileapp.micro.ui.components.EmptyContentScreen
import com.mobileapp.micro.ui.components.SimpleAlertDialog
import com.mobileapp.micro.viewmodel.ChannelViewModel
import kotlinx.coroutines.launch

@Composable
fun ChannelsPage(
    channelViewModel: ChannelViewModel,
    onOpenChannel: () -> Unit
) {
    if (channelViewModel.userChannels.isEmpty()) {
        EmptyContentScreen(
            image = R.drawable.logo,
            text = "You are not subscribed to any channel"
        )
    } else {
        ChannelsPageContent(
            channelViewModel = channelViewModel,
            onOpenChannel = onOpenChannel
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChannelsPageContent(
    channelViewModel: ChannelViewModel,
    onOpenChannel: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        items(channelViewModel.userChannels.asReversed()) { channel ->
            val coroutine = rememberCoroutineScope()
            val leaveChannelConfirmationDialog = remember { mutableStateOf(false) }
            val state = rememberDismissState(
                confirmStateChange = {
                    if (it == DismissValue.DismissedToStart) {
                        leaveChannelConfirmationDialog.value = true
                    }
                    true
                }
            )
            if (leaveChannelConfirmationDialog.value)
                SimpleAlertDialog(
                    isOpenDialog = leaveChannelConfirmationDialog,
                    title = channel.channelName,
                    text = { Text(text = "Do you want to Unsubscribe this channel?") },
                    onSubmit = {
                        leaveChannelConfirmationDialog.value = false
                        channelViewModel.userChannels.remove(channel)
                        channelViewModel.unsubscribeChannel(channel.channelId)
                    },
                    onDismiss = { coroutine.launch { state.reset() } }
                )
            SwipeToDismiss(
                state = state,
                directions = setOf(DismissDirection.EndToStart),
                background = {
                    val color = when (state.dismissDirection) {
                        DismissDirection.StartToEnd -> Color.Transparent
                        DismissDirection.EndToStart -> Color.Red
                        null -> Color.Transparent
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = color)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Unsubscribe",
                            tint = Color.White,
                            modifier = Modifier.align(Alignment.CenterEnd)
                        )
                    }
                },
                dismissContent = {
                    ChannelListItem(
                        channel = channel,
                        onClick = {
                            channelViewModel.currentChannelId.value = channel.channelId
                            channelViewModel.currentChannel.value = channel
                            channelViewModel.loadChannelLessons(channel.lessonsIds)
                            onOpenChannel()
                        }
                    )
                }
            )
            Divider()
        }
    }
}

@Composable
private fun ChannelListItem(
    channel: Channel,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable { onClick() }
            .background(MaterialTheme.colors.surface)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DefaultImage(
                modifier = Modifier
                    .size(51.dp)
                    .clip(CircleShape),
                localUri = channel.localImageUri,
                onlineUri = channel.onlineImageUri,
                placeHolder = R.drawable.sample_avatar,
                error = R.drawable.sample_avatar,
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = channel.channelName,
                    style = TextStyle(fontWeight = FontWeight.Bold),
                    fontSize = 18.sp,
                )
//            Spacer(modifier = Modifier.height(8.dp))
//            channel.lastMessage?.let {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    Text(
//                        modifier = Modifier.weight(1f),
//                        text = "${it.authorName}: ${it.text}",
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis,
//                    )
//                    Spacer(modifier = Modifier.width(10.dp))
//                    Text(
//                        text = getDateFormatted(it.createdAt, "H:mm"),
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis,
//                    )
//                }
//            }
            }
        }
    }
}

@Composable
@Preview
private fun ChannelListItemPreview() {
    Surface {
        Column {
            ChannelListItem(
                channel = Channel(channelName = "This is a Channel"),
                onClick = {}
            )
        }
    }
}