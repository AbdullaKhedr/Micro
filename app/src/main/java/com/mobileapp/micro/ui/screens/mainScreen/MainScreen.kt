package com.mobileapp.micro.ui.screens.mainScreen

import androidx.activity.ComponentActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.mobileapp.micro.Screen
import com.mobileapp.micro.ui.screens.AppNavigator
import com.mobileapp.micro.viewmodel.AuthViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun MainScreen() {
    // Create a scaffold state
    val scaffoldState = rememberScaffoldState()
    // Remember navController so it does not get recreated on recomposition
    val navController = rememberAnimatedNavController()
    // State of bottomBar, set state to false if screen doesn't need them
    val bottomBarState = rememberSaveable { (mutableStateOf(true)) }
    // Bottom sheet for create options
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    // Determine the start destination based on auth state
    val context = LocalContext.current
    val authViewModel = viewModel<AuthViewModel>(context as ComponentActivity)
    val startDestination = remember { mutableStateOf(Screen.Home.route) }
    if (authViewModel.getAuthState() == null) {
        startDestination.value = Screen.Login.route
        bottomBarState.value = false
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetContent = {
            CreateOptionsBottomSheetContent(
                navController = navController,
                onCloseSheet = {
                    coroutineScope.launch {
                        sheetState.hide()
                    }
                }
            )
        }
    ) {
        Scaffold(
            scaffoldState = scaffoldState,
            // drawerContent = { NavDrawer() },
            bottomBar = { BottomNavBar(navController, bottomBarState) },
            floatingActionButton = {
                FABtn(
                    fabState = bottomBarState,
                    onClick = {
                        coroutineScope.launch {
                            sheetState.show()
                        }
                    }
                )
            },
            floatingActionButtonPosition = FabPosition.Center,
            isFloatingActionButtonDocked = true
        ) {
            AppNavigator(
                padding = it,
                navController = navController,
                startDestination = startDestination.value,
                bottomBarState = bottomBarState,
                scaffoldState = scaffoldState
            )
        }
    }
}
