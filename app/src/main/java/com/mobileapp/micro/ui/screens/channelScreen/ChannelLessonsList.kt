package com.mobileapp.micro.ui.screens.channelScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mobileapp.micro.model.Lesson
import com.mobileapp.micro.ui.components.LessonItem
import com.mobileapp.micro.viewmodel.CourseViewModel

@Composable
fun ChannelLessonsList(
    courseViewModel: CourseViewModel,
    modifier: Modifier = Modifier,
    messages: SnapshotStateList<Lesson>
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(messages) { lesson ->
                LessonItem(
                    courseViewModel = courseViewModel,
                    lesson = lesson
                )
            }
        }
    }
}
