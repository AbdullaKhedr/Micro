package com.mobileapp.micro.ui.screens.homeScreen

import androidx.activity.ComponentActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.LocalOverScrollConfiguration
import androidx.compose.foundation.gestures.OverScrollConfiguration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.mobileapp.micro.R
import com.mobileapp.micro.model.Course
import com.mobileapp.micro.ui.components.DefaultImage
import com.mobileapp.micro.viewmodel.CourseViewModel
import kotlinx.coroutines.flow.collect

@Composable
fun HomeScreen(
    onSearch: () -> Unit,
    onDrawerOpen: () -> Unit,
    onOpenCourse: () -> Unit
) {
    val context = LocalContext.current
    val courseViewModel = viewModel<CourseViewModel>(context as ComponentActivity)
    Scaffold(
        topBar = {
            HomeScreenTopBar(
                onSearch = onSearch,
                onDrawerOpen = onDrawerOpen
            )
        },
    ) {
        HomeScreenContent(
            courseViewModel,
            onOpenCourse
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HomeScreenContent(
    courseViewModel: CourseViewModel,
    onOpenCourse: () -> Unit
) {
    val isRefreshing = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        courseViewModel.coursesUiState.collect {
            when (it) {
                is CourseViewModel.CoursesUiState.Loading -> {
                    isRefreshing.value = true
                }
                else -> {
                    isRefreshing.value = false
                }
            }
        }
    }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing.value),
        onRefresh = { courseViewModel.loadCourses() },
    ) {
        CompositionLocalProvider(
            LocalOverScrollConfiguration provides OverScrollConfiguration(
                drawPadding = PaddingValues(vertical = 56.dp)
            ),
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                ) {
                    CoursesSection(
                        courseViewModel = courseViewModel,
                        courses = courseViewModel.recentCourses,
                        sectionTitle = "Recent Courses",
                        onOpenCourse = onOpenCourse
                    )
                    Spacer(Modifier.size(16.dp))
                    Divider()
                    CoursesSection(
                        courseViewModel = courseViewModel,
                        courses = courseViewModel.topCoursesLiked,
                        sectionTitle = "Top Liked Courses",
                        onOpenCourse = onOpenCourse
                    )
                    Spacer(Modifier.size(16.dp))
                    Divider()
                    CoursesSection(
                        courseViewModel = courseViewModel,
                        courses = courseViewModel.topCoursesVisited,
                        sectionTitle = "Most Visited Courses",
                        onOpenCourse = onOpenCourse
                    )
                }
            }
        )
    }
}

@Composable
private fun CoursesSection(
    courses: List<Course>,
    sectionTitle: String = "",
    onOpenCourse: () -> Unit,
    courseViewModel: CourseViewModel
) {
    Column {
        Text(
            modifier = Modifier.padding(10.dp),
            text = sectionTitle,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
        ) {
            courses.forEach {
                CourseCard(
                    course = it,
                    onClick = {
                        courseViewModel.getCourseToStudy(it)
                        onOpenCourse()
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CourseCard(
    course: Course,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(10.dp)
            .width(250.dp)
            .height(250.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = 10.dp,
        onClick = { onClick() }
    ) {
        Column {
            Box {
                DefaultImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp),
                    localUri = course.localImageUri,
                    onlineUri = course.onlineImageUri,
                    placeHolder = R.drawable.image_placeholder,
                    error = R.drawable.image_placeholder,
                    contentScale = ContentScale.Crop,
                    loadingIndicator = true
                )
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.White,
                                    Color.Transparent
                                )
                            )
                        )
                ) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = course.category,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Column {
                Column(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
                        .fillMaxWidth()
                        .height(40.dp)
                ) {
                    Text(
                        text = course.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    // add anything after the course title
                }
                Divider()
                Row(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .fillMaxWidth()
                        .height(30.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        DefaultImage(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .border(1.dp, Color.LightGray, CircleShape),
                            localUri = "",
                            onlineUri = course.authorImageUri,
                            placeHolder = R.drawable.sample_avatar,
                            error = R.drawable.sample_avatar,
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = course.authorName,
                            fontWeight = FontWeight.Light,
                            fontSize = 14.sp
                        )
                    }
                    Card(
                        shape = RoundedCornerShape(5.dp),
                        border = BorderStroke(1.dp, Color.DarkGray),
                        onClick = {},
                        enabled = false
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(
                                    start = 8.dp,
                                    end = 8.dp,
                                    top = 2.dp,
                                    bottom = 2.dp
                                ),
                            text = "${course.lessonsIds.size} Lessons",
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeScreenTopBar(
    onSearch: () -> Unit,
    onDrawerOpen: () -> Unit
) {
    TopAppBar(
        title = {
            Text(text = "Micro")
        },
        actions = {
            IconButton(onClick = { onSearch() }) {
                Icon(Icons.Default.Search, "Search")
            }
        },
//        navigationIcon = {
//            IconButton(onClick = { onDrawerOpen() }) {
//                Icon(
//                    modifier = Modifier,
//                    imageVector = Icons.Default.Menu,
//                    contentDescription = null
//                )
//            }
//        },
    )
}