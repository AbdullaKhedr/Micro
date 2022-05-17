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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobileapp.micro.R
import com.mobileapp.micro.common.getDateFormatted
import com.mobileapp.micro.model.GroupMessage
import com.mobileapp.micro.model.MessageType
import com.mobileapp.micro.model.StudyGroup
import com.mobileapp.micro.ui.components.DefaultImage
import com.mobileapp.micro.ui.components.EmptyContentScreen
import com.mobileapp.micro.ui.components.SimpleAlertDialog
import com.mobileapp.micro.viewmodel.StudyGroupViewModel
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun GroupsListPage(
    studyGroupViewModel: StudyGroupViewModel,
    onOpenStudyGroup: () -> Unit
) {
    if (studyGroupViewModel.userGroups.isEmpty()) {
        EmptyContentScreen(
            image = R.drawable.logo,
            text = "You are not a member in any study group"
        )
    } else {
        GroupsListPageContent(
            studyGroupViewModel = studyGroupViewModel,
            onOpenStudyGroup = onOpenStudyGroup
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GroupsListPageContent(
    studyGroupViewModel: StudyGroupViewModel,
    onOpenStudyGroup: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(studyGroupViewModel.userGroups.asReversed()) { studyGroup ->
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
                    title = studyGroup.groupName,
                    text = { Text(text = "Do you want to leave this group?") },
                    onSubmit = {
                        leaveChannelConfirmationDialog.value = false
                        studyGroupViewModel.userGroups.remove(studyGroup)
                        studyGroupViewModel.leaveGroup(studyGroup.groupId)
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
                            contentDescription = "Leave",
                            tint = Color.White,
                            modifier = Modifier.align(Alignment.CenterEnd)
                        )
                    }
                },
                dismissContent = {
                    StudyGroupsListItem(
                        studyGroup = studyGroup,
                        onClick = {
                            studyGroupViewModel.currentStudyGroupId.value = studyGroup.groupId
                            studyGroupViewModel.loadCurrentStudyGroup()
                            studyGroupViewModel.loadCurrentGroupMessages()
                            onOpenStudyGroup()
                            println(">> Debug: Current Study Group id : ${studyGroupViewModel.currentStudyGroupId.value}")
                        }
                    )
                }
            )
            Divider()
        }
    }
}

@Composable
private fun StudyGroupsListItem(
    studyGroup: StudyGroup,
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
                localUri = studyGroup.localImageUri,
                onlineUri = studyGroup.onlineImageUri,
                placeHolder = R.drawable.sample_avatar,
                error = R.drawable.sample_avatar,
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = studyGroup.groupName,
                    style = TextStyle(fontWeight = FontWeight.Bold),
                    fontSize = 18.sp,
                )
                Spacer(modifier = Modifier.height(8.dp))
                studyGroup.lastMessage?.let {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = "${it.authorName}: ${it.text}",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = getDateFormatted(it.createdAt, "H:mm"),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun StudyGroupsListItemPreview() {
    Surface {
        Column {
            StudyGroupsListItem(
                StudyGroup(
                    groupName = "Study Group",
                    lastMessage = GroupMessage(
                        authorName = "Abdullah",
                        text = "This is the sent last message",
                        type = MessageType.TEXT,
                        createdAt = Date(),
                    ),
                )
            ) {}
        }
    }
}
