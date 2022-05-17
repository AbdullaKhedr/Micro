package com.mobileapp.micro.ui.screens.searchScreen

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobileapp.micro.ui.components.CourseItem
import com.mobileapp.micro.viewmodel.CourseViewModel
import com.mobileapp.micro.viewmodel.SearchViewModel

@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
    onOpenCourse: () -> Unit
) {
    val context = LocalContext.current
    val courseViewModel = viewModel<CourseViewModel>(context as ComponentActivity)
    val searchViewModel = viewModel<SearchViewModel>(context)
    Scaffold(
        topBar = {
            SearchAppBar(
                text = searchViewModel.searchText,
                onTextChange = {
                    searchViewModel.search()
                },
                onCloseClicked = {
                    searchViewModel.searchText.value = ""
                },
                onNavigateBack = onNavigateBack
            )
        }
    ) {
        SearchScreenContent(
            searchViewModel = searchViewModel,
            courseViewModel = courseViewModel,
            onOpenCourse = onOpenCourse
        )
    }
}

@Composable
fun SearchScreenContent(
    searchViewModel: SearchViewModel,
    courseViewModel: CourseViewModel,
    onOpenCourse: () -> Unit
) {
    val courses = searchViewModel.filteredCourses
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(modifier = Modifier.padding(16.dp), text = "Filter Courses by:")
        Divider()
        LazyRow(
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            items(searchViewModel.filters) { filter ->
                Spacer(modifier = Modifier.width(8.dp))
                Row(
                    modifier = Modifier
                        .border(1.dp, Color.LightGray, RoundedCornerShape(percent = 50))
                        .background(
                            if (filter.isSelected.value)
                                Color.LightGray
                            else
                                Color.White, shape = RoundedCornerShape(50)
                        )
                        .clickable {
                            filter.isSelected.value = !filter.isSelected.value
                        }
                ) {
                    Text(
                        modifier = Modifier
                            .padding(8.dp),
                        text = filter.filterName
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
        Divider()
        Spacer(modifier = Modifier.width(16.dp))
        LazyColumn {
            items(courses) { course ->
                CourseItem(
                    course = course,
                    onClick = {
                        courseViewModel.getCourseToStudy(course)
                        onOpenCourse()
                    },
                    optionMenu = null
                )
                Divider()
            }
        }
    }
}

@Composable
fun SearchAppBar(
    text: MutableState<String>,
    onTextChange: () -> Unit,
    onCloseClicked: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        elevation = AppBarDefaults.TopAppBarElevation,
        color = MaterialTheme.colors.primary
    ) {
        val focusRequester = FocusRequester()
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            value = text.value,
            onValueChange = {
                text.value = it
                onTextChange()
            },
            placeholder = {
                Text(
                    modifier = Modifier
                        .alpha(ContentAlpha.medium),
                    text = "Search here...",
                    color = Color.White
                )
            },
            textStyle = TextStyle(
                fontSize = MaterialTheme.typography.subtitle1.fontSize
            ),
            singleLine = true,
            leadingIcon = {
                IconButton(onClick = { onNavigateBack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            },
            trailingIcon = {
                IconButton(
                    onClick = { onCloseClicked() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Icon",
                        tint = Color.White
                    )
                }
            },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                cursorColor = Color.White.copy(alpha = ContentAlpha.medium)
            )
        )
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
fun SearchAppBarPreview() {
    SearchAppBar(
        text = mutableStateOf("Some random text"),
        onTextChange = {},
        onCloseClicked = {},
        onNavigateBack = {}
    )
}