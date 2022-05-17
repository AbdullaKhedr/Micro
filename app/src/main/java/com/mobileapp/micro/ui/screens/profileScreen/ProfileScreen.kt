package com.mobileapp.micro.ui.screens.profileScreen

import androidx.activity.ComponentActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.mobileapp.micro.R
import com.mobileapp.micro.ui.components.DefaultImage
import com.mobileapp.micro.ui.components.SimpleAlertDialog
import com.mobileapp.micro.ui.components.SimpleTopBar
import com.mobileapp.micro.viewmodel.AuthViewModel
import com.mobileapp.micro.viewmodel.CourseViewModel
import com.mobileapp.micro.viewmodel.UserViewModel
import com.mobileapp.micro.viewmodel.create.CreateCourseViewModel

@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onFriends: () -> Unit,
    onLearningList: () -> Unit,
    onCreatedCoursesList: () -> Unit,
    onLogOut: () -> Unit,
    onEditProfile: () -> Unit
) {
    val context = LocalContext.current
    val profileViewModel = viewModel<UserViewModel>(context as ComponentActivity)
    val authViewModel = viewModel<AuthViewModel>(context)
    val userViewModel = viewModel<UserViewModel>(context)
    val createCourseViewModel = viewModel<CreateCourseViewModel>(context)
    val courseViewModel = viewModel<CourseViewModel>(context)
    Scaffold(
        topBar = {
            SimpleTopBar(
                onNavigateBack = onNavigateBack,
                backArrow = true,
                title = "Profile"
            )
        },
        content = {
            ProfileScreenContent(
                authViewModel = authViewModel,
                userViewModel = profileViewModel,
                createCourseViewModel = createCourseViewModel,
                courseViewModel = courseViewModel,
                onLearningList = onLearningList,
                onFriends = onFriends,
                onCreatedCoursesList = onCreatedCoursesList,
                onEditProfile = {
                    authViewModel.currentUser.value?.let {
                        userViewModel.fillEditUserUiData(it)
                    }
                    onEditProfile()
                },
                onLogOut = onLogOut
            )
        }
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalPagerApi::class)
@Composable
fun ProfileScreenContent(
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    createCourseViewModel: CreateCourseViewModel,
    courseViewModel: CourseViewModel,
    onFriends: () -> Unit,
    onLearningList: () -> Unit,
    onCreatedCoursesList: () -> Unit,
    onEditProfile: () -> Unit,
    onLogOut: () -> Unit
) {
    val showLogoutDialog = remember { mutableStateOf(false) }
    if (showLogoutDialog.value) {
        SimpleAlertDialog(
            isOpenDialog = showLogoutDialog,
            title = "Log out",
            text = { Text(text = "Are you sure you want to log out?") },
            onSubmit = {
                authViewModel.signOut()
                onLogOut()
            }
        )
    }
    val showReachUsDialog = remember { mutableStateOf(false) }
    val message = remember { mutableStateOf("") }
    if (showReachUsDialog.value) {
        SimpleAlertDialog(
            isOpenDialog = showReachUsDialog,
            title = "Send us a message",
            text = {
                Column {
                    TextField(value = message.value, onValueChange = { message.value = it })
                }
            },
            onSubmit = {
                userViewModel.sendMessageToApp(message.value)
                showReachUsDialog.value = false
                message.value = ""
            },
            onDismiss = {
                message.value = ""
            }
        )
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        authViewModel.currentUser.value?.let {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.padding(0.dp)
                ) {
                    DefaultImage(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color.LightGray, CircleShape),
                        localUri = it.localPhotoUri,
                        onlineUri = it.onlinePhotoUri,
                        placeHolder = R.drawable.sample_avatar,
                        error = R.drawable.sample_avatar,
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.width(24.dp))
                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = it.displayName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Bio",
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = it.bio,
                        style = MaterialTheme.typography.body2,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Email " + if (it.isVerified) "(Verified)" else "(unverified)",
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = it.email,
                        style = MaterialTheme.typography.body2
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                ProfileMenuItem(
                    onClick = { onFriends() },
                    imageVector = Icons.Filled.People,
                    title = "My Friends"
                )
                ProfileMenuItem(
                    onClick = { onLearningList() },
                    imageVector = Icons.Filled.BookmarkAdded,
                    title = "My Learning List"
                )
                ProfileMenuItem(
                    onClick = { onCreatedCoursesList() },
                    imageVector = Icons.Filled.Class,
                    title = "Created Courses"
                )
                Spacer(modifier = Modifier.height(12.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))
                ProfileMenuItem(
                    onClick = { onEditProfile() },
                    imageVector = Icons.Filled.Edit,
                    title = "Edit Profile"
                )
                ProfileMenuItem(
                    onClick = { showReachUsDialog.value = true },
                    imageVector = Icons.Filled.ContactSupport,
                    title = "Reach Us"
                )
                ProfileMenuItem(
                    onClick = { showLogoutDialog.value = true },
                    imageVector = Icons.Filled.Login,
                    title = "Log out"
                )
            }
        }
    }
}

@Composable
private fun ProfileMenuItem(
    onClick: () -> Unit,
    imageVector: ImageVector,
    title: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(imageVector = imageVector, contentDescription = null)
            Spacer(modifier = Modifier.width(24.dp))
            Text(
                text = title,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            )
        }
        Row {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}