package com.mobileapp.micro.ui.screens.courseScreen.subTabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.Forward
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.mobileapp.micro.model.Lesson
import com.mobileapp.micro.ui.screens.myLearningScreens.pagerState
import com.mobileapp.micro.viewmodel.CourseViewModel
import com.mobileapp.micro.viewmodel.StudyGroupViewModel
import com.mobileapp.micro.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun CourseLessonsPage(
    courseViewModel: CourseViewModel,
    studyGroupViewModel: StudyGroupViewModel,
    userViewModel: UserViewModel,
    onStudyLesson: () -> Unit,
    onLessonShareToGroup: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        courseViewModel.currentStudyCourseLessons.forEachIndexed { index, lesson ->
            LessonCard(
                modifier = Modifier,
                courseViewModel = courseViewModel,
                studyGroupViewModel = studyGroupViewModel,
                userViewModel = userViewModel,
                lesson = lesson,
                lessonIndex = index,
                onStudyLesson = onStudyLesson,
                onLessonShareToGroup = onLessonShareToGroup
            )
            Divider()
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun LessonCard(
    modifier: Modifier,
    courseViewModel: CourseViewModel,
    studyGroupViewModel: StudyGroupViewModel,
    userViewModel: UserViewModel,
    lesson: Lesson,
    lessonIndex: Int,
    onStudyLesson: () -> Unit,
    onLessonShareToGroup: () -> Unit,
) {
    val coroutine = rememberCoroutineScope()
    val optionsMenu = remember { mutableStateOf(false) }
    TextButton(
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp),
        onClick = {
            // Shared Index for current opened lesson
            // whenever a lesson is opened, it will be the current one in the VM
            // so when the lesson dialog onNext / onBack is clicked,
            // he can know which is the next, and which id the prev
            courseViewModel.currentLessonIndex.value = lessonIndex
            onStudyLesson()
        }
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 5.dp),
                maxLines = 1,
                text = "${lesson.lessonIndex + 1}. ${lesson.title}",
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box {
                IconButton(onClick = { optionsMenu.value = !optionsMenu.value }) {
                    Icon(imageVector = Icons.Outlined.MoreVert, contentDescription = null)
                }
                DropdownMenu(
                    expanded = optionsMenu.value,
                    onDismissRequest = { optionsMenu.value = false }
                ) {
                    DropdownMenuItem(
                        onClick = {
                            studyGroupViewModel.isSharingLesson.value = true
                            studyGroupViewModel.lessonIdToShar = lesson.lessonId
                            studyGroupViewModel.lessonTitleToShar = lesson.title
                            onLessonShareToGroup()
                            optionsMenu.value = false
                        }
                    ) {
                        Icon(imageVector = Icons.Outlined.Forward, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Share to study group")
                    }
                    DropdownMenuItem(
                        onClick = {
                            studyGroupViewModel.isSharingLesson.value = true
                            studyGroupViewModel.lessonIdToShar = lesson.lessonId
                            studyGroupViewModel.lessonTitleToShar = lesson.title
                            onLessonShareToGroup()
                            coroutine.launch {
                                pagerState.animateScrollToPage(2)
                            }
                            optionsMenu.value = false
                        }
                    ) {
                        Icon(imageVector = Icons.Outlined.Forward, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Suggest to channel")
                    }
                    DropdownMenuItem(
                        onClick = {
                            userViewModel.addLessonToLearningList(lesson = lesson)
                            optionsMenu.value = false
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.BookmarkAdd,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Save to learning list")
                    }
                    DropdownMenuItem(
                        onClick = {
                            /*TODO*/
                            optionsMenu.value = false
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Timer,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Schedule a spaced repetition")
                    }
                }
            }
        }
    }
}