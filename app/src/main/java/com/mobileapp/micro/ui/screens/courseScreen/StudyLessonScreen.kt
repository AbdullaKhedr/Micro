package com.mobileapp.micro.ui.screens.courseScreen

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.mobileapp.micro.R
import com.mobileapp.micro.model.Lesson
import com.mobileapp.micro.model.MediaType
import com.mobileapp.micro.ui.components.DefaultImage
import com.mobileapp.micro.ui.components.SimpleTopBar
import com.mobileapp.micro.viewmodel.CourseViewModel

@Composable
fun StudyLessonScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val courseViewModel = viewModel<CourseViewModel>(context as ComponentActivity)
    Scaffold(
        topBar = {
            SimpleTopBar(
                onNavigateBack = onNavigateBack,
                backArrow = true,
                title = courseViewModel.currentLesson.value.title
            )
        },
        content = {
            StudyLessonScreenContent(
                courseViewModel = courseViewModel,
            )
        }
    )
}


@Composable
private fun StudyLessonScreenContent(
    courseViewModel: CourseViewModel,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.weight(1F)
        ) {
            LessonContent(courseViewModel.currentLesson.value)
        }
        Divider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                modifier = Modifier
                    .height(40.dp)
                    .width(100.dp),
                onClick = {
                    courseViewModel.currentLessonIndex.value--
                },
                enabled = courseViewModel.currentLessonIndex.value > 0
            ) {
                Text(text = "Back")
            }
            Text(text = "${courseViewModel.currentLessonIndex.value + 1} / ${courseViewModel.currentStudyCourseLessons.size}")
            Button(
                modifier = Modifier
                    .height(40.dp)
                    .width(100.dp),
                onClick = {
                    courseViewModel.currentLessonIndex.value++
                },
                enabled = courseViewModel.currentLessonIndex.value < courseViewModel.currentStudyCourseLessons.size - 1
            ) {
                Text(text = "Next")
            }
        }
    }
}

@Composable
fun LessonContent(
    lesson: Lesson
) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (lesson.mediaType == MediaType.IMAGE) {
            DefaultImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                localUri = lesson.localMediaUri,
                onlineUri = lesson.onlineMediaUri,
                placeHolder = R.drawable.image_placeholder,
                error = R.drawable.image_placeholder,
                contentScale = ContentScale.Crop,
                loadingIndicator = true
            )
        } else if (lesson.mediaType == MediaType.VIDEO) {
            MyPlayer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                uri = lesson.onlineMediaUri
            )
        }
        Spacer(modifier = Modifier.height(0.dp))
        LazyColumn(
            modifier = Modifier
                .weight(1F)
                .padding(16.dp),
            content = {
                item {
                    Text(
                        modifier = Modifier.padding(top = 0.dp, bottom = 16.dp),
                        text = lesson.content
                    )
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun MyPlayer(
    modifier: Modifier,
    uri: String
) {
    val context = LocalContext.current
    val player = ExoPlayer.Builder(context).build()
    val playerView = StyledPlayerView(context)
    val mediaItem = MediaItem.fromUri(uri)
    val playWhenReady = rememberSaveable {
        mutableStateOf(true)
    }
    player.setMediaItem(mediaItem)
    playerView.player = player
    LaunchedEffect(player) {
        player.prepare()
        player.playWhenReady = playWhenReady.value

    }
    AndroidView(
        modifier = modifier,
        factory = {
            playerView
        }
    )
}
