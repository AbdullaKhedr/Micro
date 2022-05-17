package com.mobileapp.micro.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobileapp.micro.R
import com.mobileapp.micro.model.Course

@Composable
fun CourseItem(
    course: Course,
    onClick: () -> Unit,
    optionMenu: @Composable() ((showCourseOptionMenu: MutableState<Boolean>) -> Unit)? = {}
) {
    val showCourseOptionMenu = remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DefaultImage(
                modifier = Modifier
                    .height(70.dp)
                    .width(110.dp)
                    .clip(RoundedCornerShape(percent = 10))
                    .border(1.dp, Color.LightGray, RoundedCornerShape(percent = 10)),
                localUri = course.localImageUri,
                onlineUri = course.onlineImageUri,
                placeHolder = R.drawable.image_placeholder,
                error = R.drawable.image_placeholder,
                contentScale = ContentScale.Crop,
                loadingIndicator = true
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = course.title,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
                Text(text = "${course.lessonsIds.size} Lessons in ${course.category}",style = TextStyle(
                    fontWeight = FontWeight.Light,
                    fontSize = 14.sp
                ))
                Text(text = "By: ${course.authorName}",style = TextStyle(
                    fontWeight = FontWeight.Light,
                    fontSize = 14.sp
                ))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        if (optionMenu != null) {
            Box {
                IconButton(onClick = { showCourseOptionMenu.value = !showCourseOptionMenu.value }) {
                    Icon(imageVector = Icons.Outlined.MoreVert, contentDescription = null)
                }
                DropdownMenu(
                    expanded = showCourseOptionMenu.value,
                    onDismissRequest = { showCourseOptionMenu.value = false }
                ) {
                    optionMenu(showCourseOptionMenu)
                }
            }
        }
    }
}