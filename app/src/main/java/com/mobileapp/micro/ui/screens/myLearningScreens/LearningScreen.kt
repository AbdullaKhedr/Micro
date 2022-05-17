package com.mobileapp.micro.ui.screens.myLearningScreens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.mobileapp.micro.ui.components.PagesBar
import com.mobileapp.micro.ui.components.SimpleTopBar
import com.mobileapp.micro.ui.screens.myLearningScreens.subPages.ChannelsPage
import com.mobileapp.micro.ui.screens.myLearningScreens.subPages.CoursesPage
import com.mobileapp.micro.ui.screens.myLearningScreens.subPages.GroupsListPage
import com.mobileapp.micro.viewmodel.ChannelViewModel
import com.mobileapp.micro.viewmodel.CourseViewModel
import com.mobileapp.micro.viewmodel.StudyGroupViewModel

@OptIn(ExperimentalPagerApi::class)
val pagerState = PagerState(pageCount = 3)

@Composable
fun LearningScreen(
    onNavigateBack: () -> Unit,
    onOpenCourse: () -> Unit,
    onOpenStudyGroup: () -> Unit,
    onOpenChannel: () -> Unit,
) {
    val context = LocalContext.current
    val courseViewModel = viewModel<CourseViewModel>(context as ComponentActivity)
    val studyGroupViewModel = viewModel<StudyGroupViewModel>(context)
    val channelViewModel = viewModel<ChannelViewModel>(context)

    Scaffold(
        topBar = {
            SimpleTopBar(
                title = "My Learning",
                onNavigateBack = onNavigateBack
            )
        },
    ) {
        LearningScreenContent(
            courseViewModel = courseViewModel,
            studyGroupViewModel = studyGroupViewModel,
            channelViewModel = channelViewModel,
            pagerState = pagerState,
            onOpenCourse = onOpenCourse,
            onOpenStudyGroup = onOpenStudyGroup,
            onOpenChannel = onOpenChannel
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun LearningScreenContent(
    courseViewModel: CourseViewModel,
    studyGroupViewModel: StudyGroupViewModel,
    channelViewModel: ChannelViewModel,
    pagerState: PagerState,
    onOpenCourse: () -> Unit,
    onOpenStudyGroup: () -> Unit,
    onOpenChannel: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        PagesBar(
            pagerState = pagerState,
            pagesTitles = listOf("Study Groups", "Courses", "Channels")
        )
        TabsContent(
            pagerState = pagerState,
            courseViewModel = courseViewModel,
            studyGroupViewModel = studyGroupViewModel,
            channelViewModel = channelViewModel,
            onOpenCourse = onOpenCourse,
            onOpenStudyGroup = onOpenStudyGroup,
            onOpenChannel = onOpenChannel
        )
    }
}

@ExperimentalPagerApi
@Composable
private fun TabsContent(
    pagerState: PagerState,
    courseViewModel: CourseViewModel,
    studyGroupViewModel: StudyGroupViewModel,
    channelViewModel: ChannelViewModel,
    onOpenCourse: () -> Unit,
    onOpenStudyGroup: () -> Unit,
    onOpenChannel: () -> Unit,
) {
    HorizontalPager(state = pagerState) { page ->
        when (page) {
            0 -> GroupsListPage(
                studyGroupViewModel = studyGroupViewModel,
                onOpenStudyGroup = onOpenStudyGroup
            )
            1 -> CoursesPage(
                courseViewModel = courseViewModel,
                onOpenCourse = onOpenCourse
            )
            2 -> ChannelsPage(
                channelViewModel = channelViewModel,
                onOpenChannel = onOpenChannel
            )
        }
    }
}