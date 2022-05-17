package com.mobileapp.micro.ui.screens.courseScreen

import androidx.activity.ComponentActivity
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.mobileapp.micro.common.shareTextContent
import com.mobileapp.micro.ui.components.PagesBar
import com.mobileapp.micro.ui.screens.courseScreen.subTabs.CourseCommentsPage
import com.mobileapp.micro.ui.screens.courseScreen.subTabs.CourseLessonsPage
import com.mobileapp.micro.ui.screens.courseScreen.subTabs.CourseOverViewPage
import com.mobileapp.micro.viewmodel.CourseViewModel
import com.mobileapp.micro.viewmodel.StudyGroupViewModel
import com.mobileapp.micro.viewmodel.UserViewModel

@Composable
fun StudyCourseScreen(
    onNavigateBack: () -> Unit,
    onStudyLesson: () -> Unit,
    onLessonShareToGroup: () -> Unit,
) {
    val context = LocalContext.current
    val courseViewModel = viewModel<CourseViewModel>(context as ComponentActivity)
    val studyGroupViewModel = viewModel<StudyGroupViewModel>(context)
    val userViewModel = viewModel<UserViewModel>(context)

    Scaffold {
        StudyCourseScreenContentWithCollapsableToolbar(
            courseViewModel = courseViewModel,
            studyGroupViewModel = studyGroupViewModel,
            userViewModel = userViewModel,
            onNavigateBack = onNavigateBack,
            onStudyLesson = onStudyLesson,
            onLessonShareToGroup = onLessonShareToGroup
        )
    }
}

private enum class SwipingStates {
    EXPANDED,
    COLLAPSED
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun StudyCourseScreenContentWithCollapsableToolbar(
    courseViewModel: CourseViewModel,
    studyGroupViewModel: StudyGroupViewModel,
    userViewModel: UserViewModel,
    onNavigateBack: () -> Unit,
    onStudyLesson: () -> Unit,
    onLessonShareToGroup: () -> Unit
) {
    val swipingState = rememberSwipeableState(initialValue = SwipingStates.EXPANDED)

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val heightInPx = with(LocalDensity.current) { maxHeight.toPx() } // Get height of screen
        val connection = remember {
            object : NestedScrollConnection {

                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource
                ): Offset {
                    val delta = available.y
                    return if (delta < 0) {
                        swipingState.performDrag(delta).toOffset()
                    } else {
                        Offset.Zero
                    }
                }

                override fun onPostScroll(
                    consumed: Offset,
                    available: Offset,
                    source: NestedScrollSource
                ): Offset {
                    val delta = available.y
                    return swipingState.performDrag(delta).toOffset()
                }

                override suspend fun onPostFling(
                    consumed: Velocity,
                    available: Velocity
                ): Velocity {
                    swipingState.performFling(velocity = available.y)
                    return super.onPostFling(consumed, available)
                }

                private fun Float.toOffset() = Offset(0f, this)
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .swipeable(
                    state = swipingState,
                    thresholds = { _, _ -> FractionalThreshold(0.5f) },
                    orientation = Orientation.Vertical,
                    anchors = mapOf(
                        0f to SwipingStates.COLLAPSED,
                        heightInPx to SwipingStates.EXPANDED,
                    )
                )
                .nestedScroll(connection)
        ) {
            Column {
                MotionLayoutHeader(
                    progress = if (swipingState.progress.to == SwipingStates.COLLAPSED)
                        swipingState.progress.fraction
                    else
                        1f - swipingState.progress.fraction,
                    courseViewModel = courseViewModel,
                    onNavigateBack = onNavigateBack,
                    scrollableBody = {
                        StudyCourseScreenContent(
                            courseViewModel = courseViewModel,
                            studyGroupViewModel = studyGroupViewModel,
                            userViewModel = userViewModel,
                            onStudyLesson = onStudyLesson,
                            onLessonShareToGroup = onLessonShareToGroup
                        )
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalPagerApi::class)
@Composable
private fun StudyCourseScreenContent(
    courseViewModel: CourseViewModel,
    studyGroupViewModel: StudyGroupViewModel,
    userViewModel: UserViewModel,
    onStudyLesson: () -> Unit,
    onLessonShareToGroup: () -> Unit
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = 3)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Divider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                IconButton(onClick = {
                    courseViewModel.likeCourse(
                        courseViewModel.currentStudyCourse.value.courseId
                    )
                }) {
                    if (courseViewModel.isLiked.value)
                        Icon(imageVector = Icons.Default.ThumbUp, contentDescription = "Like")
                    else
                        Icon(imageVector = Icons.Outlined.ThumbUp, contentDescription = "Liked")
                }
                IconButton(onClick = {
                    shareTextContent(
                        context,
                        "Study Micro Course: ${courseViewModel.currentStudyCourse.value.title}"
                    )
                }) {
                    Icon(imageVector = Icons.Outlined.Share, contentDescription = "Share")
                }
            }
            Button(
                onClick = {
                    onStudyLesson()
                    courseViewModel.studyCourse(
                        courseViewModel.currentStudyCourse.value.courseId
                    )
                }
            ) {
                if (!courseViewModel.isStudied.value)
                    Text(text = "Start Learning")
                else
                    Text(text = "Continue")
            }
        }
        // We are in scrollable column, and the following column contains
        // another scrollable columns, so it must has a fixed size
        // val screenSize: Dp = (LocalConfiguration.current.screenHeightDp.dp)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
            //.height(screenSize)
        ) {
            // tabs section
            PagesBar(
                pagerState = pagerState,
                pagesTitles = listOf("OVERVIEW", "LESSONS", "COMMENTS")
            )
            TabsContent(
                pagerState = pagerState,
                courseViewModel = courseViewModel,
                userViewModel = userViewModel,
                studyGroupViewModel = studyGroupViewModel,
                onStudyLesson = onStudyLesson,
                onLessonShareToGroup = onLessonShareToGroup
            )
        }
    }
}

@ExperimentalPagerApi
@Composable
private fun TabsContent(
    pagerState: PagerState,
    courseViewModel: CourseViewModel,
    studyGroupViewModel: StudyGroupViewModel,
    userViewModel: UserViewModel,
    onStudyLesson: () -> Unit,
    onLessonShareToGroup: () -> Unit
) {
    HorizontalPager(state = pagerState) { page ->
        when (page) {
            0 -> CourseOverViewPage(courseViewModel)
            1 -> CourseLessonsPage(
                courseViewModel = courseViewModel,
                studyGroupViewModel = studyGroupViewModel,
                userViewModel = userViewModel,
                onStudyLesson = onStudyLesson,
                onLessonShareToGroup = onLessonShareToGroup
            )
            2 -> CourseCommentsPage(courseViewModel)
        }
    }
}
