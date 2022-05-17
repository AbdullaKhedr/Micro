package com.mobileapp.micro.ui.screens.courseScreen.subTabs

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.ThumbUpAlt
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobileapp.micro.R
import com.mobileapp.micro.ui.components.DefaultImage
import com.mobileapp.micro.viewmodel.CourseViewModel
import java.util.*

@Composable
fun CourseOverViewPage(
    courseViewModel: CourseViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            val date = courseViewModel.currentStudyCourse.value
                .createdAt.toLocaleString()?.split(' ')
            Text(
                text = date?.get(0) + " " + date?.get(1) + " " + date?.get(2),
                fontSize = 15.sp,
                color = Color.Gray
            )
        }
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DefaultImage(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.LightGray, CircleShape),
                localUri = "",
                onlineUri = courseViewModel.currentStudyCourse.value.authorImageUri,
                placeHolder = R.drawable.sample_avatar,
                error = R.drawable.sample_avatar,
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = courseViewModel.currentStudyCourse.value.authorName,
                fontWeight = FontWeight.Bold
            )
        }
        Divider()
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                text = courseViewModel.currentStudyCourse.value.description
            )
            LazyRow {
                items(courseViewModel.currentStudyCourse.value.tags) { tag ->
                    Text(
                        modifier = Modifier.padding(end = 16.dp),
                        text = "#${tag.uppercase(Locale.getDefault())}",
                        style = TextStyle(textDecoration = TextDecoration.Underline)
                    )
                }
            }
        }
        Divider()
        Row(
            modifier = Modifier
                .padding(16.dp)
                .height(30.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.padding(end = 8.dp),
                imageVector = Icons.Outlined.ThumbUpAlt,
                contentDescription = "Likes"
            )
            Text(
                text = "${courseViewModel.currentStudyCourse.value.likes}  Liked this course",
                fontWeight = FontWeight.Bold
            )
        }
        Divider()
        Row(
            modifier = Modifier
                .padding(16.dp)
                .height(30.dp)
        ) {
            Icon(
                modifier = Modifier.padding(end = 8.dp),
                imageVector = Icons.Outlined.People,
                contentDescription = "People"
            )
            Text(
                text = "${courseViewModel.currentStudyCourse.value.studied}  Studied this course",
                fontWeight = FontWeight.Bold
            )
        }
        Divider()
    }
}