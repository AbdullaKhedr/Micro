package com.mobileapp.micro.ui.screens.studygroupScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobileapp.micro.common.getDateFormatted
import com.mobileapp.micro.model.GroupMessage
import com.mobileapp.micro.model.MessageType
import com.mobileapp.micro.ui.components.StudyLessonDialog
import com.mobileapp.micro.viewmodel.CourseViewModel
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MessageCard(
    messageItem: GroupMessage,
    courseViewModel: CourseViewModel?
) {
    val lessonDialogState = remember { mutableStateOf(false) }
    if (lessonDialogState.value)
        courseViewModel?.let { StudyLessonDialog(it, lessonDialogState) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = when {
            messageItem.isMine -> Alignment.End
            else -> Alignment.Start
        },
    ) {
        Card(
            modifier = Modifier.widthIn(min = 50.dp, max = 340.dp),
            shape = cardShapeFor(messageItem),
            backgroundColor = when {
                messageItem.isMine -> MaterialTheme.colors.primary
                else -> MaterialTheme.colors.secondary
            },
        ) {
            Column {
                if (!messageItem.isMine) {
                    Text(
                        modifier = Modifier.padding(top = 4.dp, start = 8.dp),
                        text = messageItem.authorName,
                        fontSize = 12.sp,
                        color = textColor(messageItem),
                    )
                }
                if (messageItem.type == MessageType.TEXT) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = messageItem.text,
                        color = textColor(messageItem),
                    )
                } else {
                    Card(
                        modifier = Modifier.padding(8.dp),
                        shape = RoundedCornerShape(8.dp),
                        backgroundColor = when {
                            messageItem.isMine -> MaterialTheme.colors.primaryVariant
                            else -> MaterialTheme.colors.secondaryVariant
                        },
                        onClick = {
                            // get the lesson from db, and set it to current
                            courseViewModel?.getLessonById(messageItem.lessonId)
                            lessonDialogState.value = true
                        }
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "Shared Lesson",
                                color = textColor(messageItem),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = messageItem.lessonTitle,
                                color = textColor(messageItem)
                            )
                        }
                    }
                }
                Text(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp, bottom = 4.dp)
                        .align(
                            when {
                                messageItem.isMine -> Alignment.End
                                else -> Alignment.Start
                            }
                        ),
                    text = getDateFormatted(messageItem.createdAt, "H:mm"),
                    fontSize = 12.sp,
                    color = textColor(messageItem)
                )
            }
        }
    }
}

@Composable
private fun cardShapeFor(message: GroupMessage): Shape {
    val roundedCorners = RoundedCornerShape(16.dp)
    return when {
        message.isMine -> roundedCorners.copy(bottomEnd = CornerSize(0))
        else -> roundedCorners.copy(bottomStart = CornerSize(0))
    }
}

@Composable
private fun textColor(message: GroupMessage): Color {
    return when {
        message.isMine -> MaterialTheme.colors.onPrimary
        else -> MaterialTheme.colors.onSecondary
    }
}

@Composable
@Preview
fun MessageCardsPreview() {
    Surface {
        Column {
            MessageCard(
                GroupMessage(
                    authorName = "Abdullah",
                    createdAt = Date(),
                    text = "Hi, What is your name?",
                    isMine = true,
                ),
                null
            )
            MessageCard(
                GroupMessage(
                    authorName = "Ali",
                    createdAt = Date(),
                    text = "Hi, My name is Ali",
                    isMine = false,
                ),
                null
            )
            MessageCard(
                GroupMessage(
                    authorName = "Abdullah",
                    createdAt = Date(),
                    text = "",
                    isMine = true,
                    type = MessageType.LESSON,
                    lessonTitle = "Lesson Title",
                ),
                null
            )
            MessageCard(
                GroupMessage(
                    authorName = "Abdullah",
                    createdAt = Date(),
                    text = "Check this wonderful lesson!",
                    isMine = true,
                ),
                null
            )
            MessageCard(
                GroupMessage(
                    authorName = "Ali",
                    createdAt = Date(),
                    text = "Sure!",
                    isMine = false,
                    type = MessageType.TEXT,
                    lessonTitle = ""
                ),
                null
            )
            MessageCard(
                GroupMessage(
                    authorName = "Abdullah",
                    createdAt = Date(),
                    text = "Hi, What is your name?",
                    isMine = false,
                    type = MessageType.LESSON,
                    lessonTitle = "Lesson Title",
                ),
                null
            )
            MessageCard(
                GroupMessage(
                    authorName = "Abdullah",
                    createdAt = Date(),
                    text = "Guys, Have a look on this one!",
                    isMine = false,
                ),
                null
            )
        }
    }
}