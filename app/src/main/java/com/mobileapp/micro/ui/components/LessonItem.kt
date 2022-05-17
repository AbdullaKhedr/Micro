package com.mobileapp.micro.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobileapp.micro.R
import com.mobileapp.micro.common.getDateFormatted
import com.mobileapp.micro.model.Lesson
import com.mobileapp.micro.model.MediaType
import com.mobileapp.micro.ui.theme.Blue70
import com.mobileapp.micro.ui.theme.Blue90
import com.mobileapp.micro.viewmodel.CourseViewModel


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LessonItem(
    courseViewModel: CourseViewModel,
    lesson: Lesson
) {
    val lessonDialogState = remember { mutableStateOf(false) }
    if (lessonDialogState.value)
        StudyLessonDialog(courseViewModel, lessonDialogState)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Blue70,
            shape = RoundedCornerShape(8.dp),
        ) {
            Column {
                Card(
                    modifier = Modifier.padding(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    backgroundColor = Blue90,
                    onClick = {
                        courseViewModel.lessonById.value = lesson
                        lessonDialogState.value = true
                    }
                ) {
                    Row {
                        if (lesson.mediaType == MediaType.VIDEO)
                            DefaultImage(
                                modifier = Modifier
                                    .size(70.dp)
                                    .background(Color.White),
                                localUri = "",
                                onlineUri = "",
                                placeHolder = R.drawable.video_placeholder,
                                error = R.drawable.video_placeholder,
                                contentScale = ContentScale.Crop
                            )
                        else
                            DefaultImage(
                                modifier = Modifier
                                    .size(70.dp)
                                    .background(Color.White),
                                localUri = lesson.localMediaUri,
                                onlineUri = lesson.onlineMediaUri,
                                placeHolder = R.drawable.image_placeholder,
                                error = R.drawable.image_placeholder,
                                contentScale = ContentScale.Crop
                            )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = lesson.title,
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            )
                        }
                    }
                }
                Text(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp, bottom = 4.dp)
                        .align(Alignment.End),
                    text = getDateFormatted(lesson.createdAt, "H:mm"),
                    fontSize = 12.sp
                )
            }
        }
    }
}