package com.mobileapp.micro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mobileapp.micro.ui.screens.courseScreen.LessonContent
import com.mobileapp.micro.viewmodel.CourseViewModel

@Composable
fun StudyLessonDialog(
    courseViewModel: CourseViewModel,
    dialogState: MutableState<Boolean>
) {
    Dialog(
        onDismissRequest = { dialogState.value = false },
        DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        val screenSize: Dp = (LocalConfiguration.current.screenHeightDp.dp)
        Box(
            modifier = Modifier
                .height((screenSize / 4) * 3) // take 4/3 the screen
                .background(Color.White, shape = RoundedCornerShape(10.dp))
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(16.dp)
                                .weight(1f)
                                .align(Alignment.CenterVertically),
                            text = courseViewModel.lessonById.value.title,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { dialogState.value = false }
                        ) {
                            Icon(imageVector = Icons.Default.Cancel, contentDescription = "Close")
                        }
                    }
                }
                LessonContent(courseViewModel.lessonById.value)
            }
        }
    }
}