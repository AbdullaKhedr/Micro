package com.mobileapp.micro.ui.screens.createScreens.course

import androidx.activity.ComponentActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobileapp.micro.R
import com.mobileapp.micro.common.getMediaPicker
import com.mobileapp.micro.model.ContentConstrains
import com.mobileapp.micro.model.CourseCategory
import com.mobileapp.micro.ui.components.*
import com.mobileapp.micro.viewmodel.create.CreateCourseViewModel

@Composable
fun CreateCourseScreen(
    onNavigateBack: () -> Unit,
    onContinueToLessons: () -> Unit
) {
    val context = LocalContext.current
    val createCourseViewModel = viewModel<CreateCourseViewModel>(context as ComponentActivity)

    val showBackConfirmDialog = remember { mutableStateOf(false) }
    if (showBackConfirmDialog.value) {
        SimpleAlertDialog(
            isOpenDialog = showBackConfirmDialog,
            title = "Go Back",
            text = { Text(text = "Are you sure you want to ignore changes you made?") },
            onSubmit = {
                onNavigateBack()
                createCourseViewModel.clearCourseCreationUiData()
            }
        )
    }

    Scaffold(
        topBar = {
            SimpleTopBar(
                onNavigateBack = {
                    showBackConfirmDialog.value = true
                },
                backArrow = true,
                title = "Course Editor"
            )
        },
        content = {
            CreateCourseScreenContent(
                createCourseViewModel = createCourseViewModel,
                onCreateLessons = onContinueToLessons
            )
        }
    )
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
private fun CreateCourseScreenContent(
    createCourseViewModel: CreateCourseViewModel,
    onCreateLessons: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current
        val imagePicker = getMediaPicker { uri ->
            createCourseViewModel.courseImageLocalUri.value = uri.toString()
        }
        val validTitle = remember { mutableStateOf(true) }
        val validDescription = remember { mutableStateOf(true) }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            DefaultImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Gray, RoundedCornerShape(percent = 5))
                    .clip(RoundedCornerShape(percent = 5))
                    .height(250.dp)
                    .clickable { imagePicker.launch("image/*") },
                localUri = createCourseViewModel.courseImageLocalUri.value,
                onlineUri = createCourseViewModel.currentCourse.onlineImageUri,
                placeHolder = R.drawable.empty_image,
                error = R.drawable.empty_image,
                contentScale = ContentScale.Crop,
            )
            Spacer(modifier = Modifier.height(8.dp))
            ConstraintTF(
                ContentConstrains.COURSE_TITLE_MAX_LENGTH,
                createCourseViewModel.courseTitle,
                validTitle,
                "Reached max limit",
                "Title",
                "",
                true,
                keyboardController,
                focusManager,
                ImeAction.Default
            )
            Spacer(modifier = Modifier.height(8.dp))
            ConstraintTF(
                ContentConstrains.COURSE_DESCRIPTION_MAX_LENGTH,
                createCourseViewModel.courseDescription,
                validDescription,
                "Reached max limit",
                "Description",
                "",
                false,
                keyboardController,
                focusManager,
                ImeAction.Default
            )
            Spacer(modifier = Modifier.height(8.dp))
            CourseCategoryDDM(
                createCourseViewModel.categories,
                "Category",
                createCourseViewModel.courseCategory,
            )
            Spacer(modifier = Modifier.height(8.dp))
            CourseTagsList(
                createCourseViewModel.tagsList,
                "Tags",
                ImeAction.Done
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        DefaultButton(
            text = "Continue",
            enable = createCourseViewModel.courseTitle.value.isNotEmpty()
                    && validTitle.value
                    && createCourseViewModel.courseDescription.value.isNotEmpty()
                    && validDescription.value
                    && createCourseViewModel.courseImageLocalUri.value.isNotEmpty()
                    && createCourseViewModel.courseCategory.value.isNotEmpty(),
            onClick = {
                // all data we got from this screen is inside the VM now
                // we will just navigate to other screen to continue
                onCreateLessons()
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CourseCategoryDDM(
    courseCategories: List<CourseCategory>,
    label: String,
    selectedCategory: MutableState<String>
) {
    val expandedDDM = remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        modifier = Modifier.fillMaxWidth(),
        expanded = expandedDDM.value,
        onExpandedChange = {
            expandedDDM.value = !expandedDDM.value
        }
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            value = selectedCategory.value,
            onValueChange = { },
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expandedDDM.value
                )
            }
        )
        ExposedDropdownMenu(
            modifier = Modifier.fillMaxWidth(),
            expanded = expandedDDM.value,
            onDismissRequest = {
                expandedDDM.value = false
            }
        ) {
            courseCategories.forEach { category ->
                DropdownMenuItem(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        selectedCategory.value = category.toString()
                        expandedDDM.value = false
                    }
                ) {
                    Text(modifier = Modifier.fillMaxWidth(), text = category.toString())
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun CourseTagsList(
    tagsList: SnapshotStateList<String>,
    label: String,
    onDoneAction: ImeAction
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    Column {
        val courseTag = remember { mutableStateOf("") }
        fun addTag() {
            if (courseTag.value.isNotBlank() && courseTag.value.isNotEmpty()) {
                tagsList.add(courseTag.value)
                courseTag.value = ""
            }
        }
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 0.dp),
            value = courseTag.value,
            onValueChange = {
                courseTag.value = it
            },
            label = { Text(label) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = onDoneAction
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    addTag()
                },
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            trailingIcon = {
                IconButton(
                    onClick = { addTag() }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add"
                    )
                }
            },
        )
        LazyRow(
            modifier = Modifier
                .height(70.dp)
                .padding(top = 8.dp, bottom = 8.dp),
        ) {
            items(tagsList) { tag ->
                CourseTag(
                    tagName = tag,
                    onDelete = {
                        tagsList.remove(tag)
                    }
                )
            }
        }
    }
}

@Composable
private fun CourseTag(
    tagName: String,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.padding(8.dp),
        shape = CircleShape,
        elevation = 2.dp,
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Row(
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onDelete() },
                imageVector = Icons.Default.Cancel,
                contentDescription = "delete"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = tagName)
        }
    }
}