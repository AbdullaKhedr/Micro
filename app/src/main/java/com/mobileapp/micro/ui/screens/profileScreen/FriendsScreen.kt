package com.mobileapp.micro.ui.screens.profileScreen

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobileapp.micro.ui.components.AddFriendDialog
import com.mobileapp.micro.ui.components.MemberItem
import com.mobileapp.micro.ui.components.SimpleTopBar
import com.mobileapp.micro.viewmodel.AuthViewModel
import com.mobileapp.micro.viewmodel.UserViewModel

@Composable
fun FriendsScreen(
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val profileViewModel = viewModel<UserViewModel>(context as ComponentActivity)
    val authViewModel = viewModel<AuthViewModel>(context)
    val userViewModel = viewModel<UserViewModel>(context)

    val showAddFriendDialog = remember { mutableStateOf(false) }
    val friendEmail = remember { mutableStateOf("") }
    if (showAddFriendDialog.value) {
        AddFriendDialog(
            isOpenDialog = showAddFriendDialog,
            email = friendEmail,
            onSubmitEmail = {
                userViewModel.addFriend(friendEmail.value)
            }
        )
    }

    Scaffold(
        topBar = {
            SimpleTopBar(
                onNavigateBack = onNavigateBack,
                title = "Friends"
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddFriendDialog.value = true },
                text = { Text(text = "Add Friend") },
                icon = { Icon(imageVector = Icons.Outlined.Add, contentDescription = null) }
            )
        },
        content = {
            FriendsScreenContent(
                userViewModel = profileViewModel
            )
        }
    )
}

@Composable
private fun FriendsScreenContent(
    userViewModel: UserViewModel
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(userViewModel.userFriendEmails) {
                MemberItem(
                    email = it.friendEmail,
                    onDelete = {
                        userViewModel.removeFriend(it)
                    }
                )
                Divider()
            }
        }
    }
}
