package com.mobileapp.micro.ui.screens.studygroupScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mobileapp.micro.model.GroupMessage
import com.mobileapp.micro.viewmodel.CourseViewModel

@Composable
fun StudyGroupMessagesList(
    modifier: Modifier = Modifier,
    messages: SnapshotStateList<GroupMessage>,
    courseViewModel: CourseViewModel
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val messageItems = messages.asReversed()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            reverseLayout = true,
        ) {
            items(messageItems) { message ->
                MessageCard(
                    messageItem = message,
                    courseViewModel = courseViewModel,
                )
            }
        }
    }
}