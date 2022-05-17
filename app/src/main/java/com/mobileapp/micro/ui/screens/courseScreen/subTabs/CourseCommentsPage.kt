package com.mobileapp.micro.ui.screens.courseScreen.subTabs

import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobileapp.micro.model.CourseComment
import com.mobileapp.micro.ui.components.chatViews.MessageTF
import com.mobileapp.micro.viewmodel.CourseViewModel
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun CourseCommentsPage(
    courseViewModel: CourseViewModel,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        MessageTF(
            modifier = Modifier,
            text = courseViewModel.currentCommentText,
            onSendMessage = {
                courseViewModel.addCourseComment()
            }
        )
        CommentsList(
            modifier = Modifier.weight(1F),
            courseViewModel.currentCourseComments
        )
    }
}


@Composable
fun CommentsList(
    modifier: Modifier = Modifier,
    comments: SnapshotStateList<CourseComment>
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val messageItems = comments
            .filter { it.text.isNotBlank() }
            .asReversed()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            //reverseLayout = true,
        ) {
            items(messageItems) { message ->
                CommentCard(message)
            }
        }
    }
}

@Composable
fun CommentCard(
    commentItem: CourseComment
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = cardShapeFor(commentItem),
            backgroundColor = when {
                commentItem.isMine -> MaterialTheme.colors.secondary
                else -> MaterialTheme.colors.primary
            },
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.padding(top = 4.dp, start = 8.dp),
                        text = commentItem.authorName,
                        fontSize = 12.sp,
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val localDate = commentItem.createdAt.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()
                        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
                        val time = localDate.format(formatter)
                        Text(
                            modifier = Modifier
                                .padding(end = 14.dp, top = 4.dp),
                            text = time.toString(),
                            fontSize = 12.sp,
                            color = when {
                                commentItem.isMine -> MaterialTheme.colors.onSecondary
                                else -> MaterialTheme.colors.onPrimary
                            }
                        )
                    } else {
                        TODO("VERSION.SDK_INT < O")
                    }
                }
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    text = commentItem.text,
                    fontSize = 16.sp,
                    color = when {
                        commentItem.isMine -> MaterialTheme.colors.onSecondary
                        else -> MaterialTheme.colors.onPrimary
                    },
                )
            }
        }
    }
}

@Composable
private fun cardShapeFor(comment: CourseComment): Shape {
    val roundedCorners = RoundedCornerShape(16.dp)
    return when {
        comment.isMine -> roundedCorners.copy(bottomEnd = CornerSize(0))
        else -> roundedCorners.copy(bottomStart = CornerSize(0))
    }
}

@Composable
@Preview
fun CommentCardPreview() {
    Surface {
        Column {
            CommentCard(
                CourseComment(
                    authorName = "Abdullah Khedr",
                    createdAt = Date(),
                    text = "Hi, What is your name?",
                    isMine = true,
                )
            )
            CommentCard(
                CourseComment(
                    authorName = "Ali Mohammed",
                    createdAt = Date(),
                    text = "I am Ali, what is your name?",
                    isMine = false,
                )
            )
        }
    }
}