package com.mobileapp.micro.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.mobileapp.micro.Screen
import com.mobileapp.micro.ui.screens.authScreens.LoginScreen
import com.mobileapp.micro.ui.screens.authScreens.SignUpScreen
import com.mobileapp.micro.ui.screens.channelScreen.ChannelScreen
import com.mobileapp.micro.ui.screens.courseScreen.StudyCourseScreen
import com.mobileapp.micro.ui.screens.courseScreen.StudyLessonScreen
import com.mobileapp.micro.ui.screens.createScreens.channel.CreateChannelScreen
import com.mobileapp.micro.ui.screens.createScreens.channel.EditChannelLesson
import com.mobileapp.micro.ui.screens.createScreens.course.CreateCourseScreen
import com.mobileapp.micro.ui.screens.createScreens.course.CreateLessonScreen
import com.mobileapp.micro.ui.screens.createScreens.course.LessonsScreen
import com.mobileapp.micro.ui.screens.createScreens.studygroup.CreateStudyGroupScreen
import com.mobileapp.micro.ui.screens.homeScreen.HomeScreen
import com.mobileapp.micro.ui.screens.myLearningScreens.LearningScreen
import com.mobileapp.micro.ui.screens.profileScreen.*
import com.mobileapp.micro.ui.screens.searchScreen.SearchScreen
import com.mobileapp.micro.ui.screens.studygroupScreen.StudyGroupScreen
import kotlinx.coroutines.launch

/**
 * Similar to the Navigation graph!
 *
 * It receives navController to navigate between screens,
 * padding values -> Since BottomNavigation has some heights,
 * to avoid clipping of screen, we set padding provided by scaffold.
 */
@ExperimentalAnimationApi
@OptIn(ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun AppNavigator(
    navController: NavHostController,
    startDestination: String,
    padding: PaddingValues,
    bottomBarState: MutableState<Boolean>,
    scaffoldState: ScaffoldState
) {
    AnimatedNavHost(
        navController = navController,
        //Set the start destination as home
        startDestination = startDestination,
        //Set the padding provided by scaffold
        modifier = Modifier.padding(paddingValues = padding)
    ) {

        /** Define the app Navigation Graph = All possible routes a user can take through the app */

        /** START: Authentication */
        composable(
            route = Screen.Login.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() }
        ) {
            bottomBarState.value = false
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) // Remove everything in the stack
                    }
                },
                onSignup = {
                    navController.navigate(Screen.SignUp.route)
                }
            )
        }

        composable(
            route = Screen.SignUp.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() }
        ) {
            bottomBarState.value = false
            SignUpScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        /** END: Authentication */

        /** START: Home Screen */
        composable(
            route = Screen.Home.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() }
        ) {
            bottomBarState.value = true
            val coroutineScope = rememberCoroutineScope()
            HomeScreen(
                onSearch = {
                    navController.navigate(Screen.Search.route) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onOpenCourse = {
                    navController.navigate(Screen.Course.route)
                },
                onDrawerOpen = {
                    coroutineScope.launch {
                        scaffoldState.drawerState.open()
                    }
                }
            )
        }
        /** END: Home Screen */

        /** START: Search Screen */
        composable(
            route = Screen.Search.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() }
        ) {
            bottomBarState.value = false
            SearchScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onOpenCourse = {
                    navController.navigate(Screen.Course.route)
                }
            )
        }
        /** END: Search Screen */

        /** START: Course Creation Section */
        composable(
            route = Screen.EditCourse.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() }
        ) {
            bottomBarState.value = false
            CreateCourseScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onContinueToLessons = {
                    navController.navigate(Screen.CourseLessonList.route)
                }
            )
        }

        composable(
            route = Screen.CourseLessonList.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() }
        ) {
            bottomBarState.value = false
            LessonsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onCreateLesson = {
                    navController.navigate(Screen.EditCourseLesson.route)
                },
                onSubmitCourse = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

        composable(
            route = Screen.EditCourseLesson.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() }
        ) {
            bottomBarState.value = false
            CreateLessonScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSubmitLesson = {
                    navController.popBackStack()
                }
            )
        }
        /** END: Course Creation Section */

        /** START: Course Study Section */
        composable(
            route = Screen.Course.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() }
        ) {
            bottomBarState.value = false
            StudyCourseScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onStudyLesson = {
                    navController.navigate(Screen.Lesson.route)
                },
                onLessonShareToGroup = {
                    navController.navigate(Screen.Learning.route)
                }
            )
        }

        composable(
            route = Screen.Lesson.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() }
        ) {
            bottomBarState.value = false
            StudyLessonScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        /** END: Course Study Section */

        /** START: Channel Section */
        composable(
            route = Screen.CreateChannel.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() }
        ) {
            bottomBarState.value = false
            CreateChannelScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onCreateChannel = {
                    navController.navigate(Screen.Channel.route) {
                        popUpTo(Screen.Learning.route)
                    }
                }
            )
        }

        composable(
            route = Screen.Channel.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() }
        ) {
            bottomBarState.value = false
            ChannelScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onCreateLesson = {
                    navController.navigate(Screen.EditChannelLesson.route)
                },
                onChannelSettings = {
                    navController.navigate(Screen.CreateChannel.route)
                }
            )
        }

        composable(
            route = Screen.EditChannelLesson.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() }
        ) {
            bottomBarState.value = false
            EditChannelLesson(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        /** END: Channel Section */

        /** START: Study Group Section */
        composable(
            route = Screen.CreateStudyGroup.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() }
        ) {
            bottomBarState.value = false
            CreateStudyGroupScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onCreateGroup = {
                    navController.navigate(Screen.StudyGroup.route) {
                        popUpTo(Screen.Learning.route)
                    }
                }
            )
        }
        composable(
            route = Screen.StudyGroup.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() }
        ) {
            bottomBarState.value = false
            StudyGroupScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onGroupSettings = {
                    navController.navigate(Screen.CreateStudyGroup.route)
                }
            )
        }
        /** END: Study Group Section */

        /** START: My Learning */
        composable(
            route = Screen.Learning.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() }
        ) {
            bottomBarState.value = false
            LearningScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onOpenCourse = {
                    navController.navigate(Screen.Course.route)
                },
                onOpenStudyGroup = {
                    navController.navigate(Screen.StudyGroup.route)
                },
                onOpenChannel = {
                    navController.navigate(Screen.Channel.route)
                }
            )
        }
        /** END: My Learning */

        /** START: Profile */
        composable(
            route = Screen.Profile.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() }
        ) {
            bottomBarState.value = false
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onFriends = {
                    navController.navigate(Screen.Friends.route)
                },
                onCreatedCoursesList = {
                    navController.navigate(Screen.UserCreatedCoursesScreen.route)
                },
                onLogOut = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                },
                onEditProfile = {
                    navController.navigate(Screen.EditProfile.route)
                },
                onLearningList = {
                    navController.navigate(Screen.LearningList.route)
                }
            )
        }
        composable(
            Screen.LearningList.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() }
        ) {
            bottomBarState.value = false
            LearningListScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            Screen.Friends.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() }
        ) {
            bottomBarState.value = false
            FriendsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            Screen.UserCreatedCoursesScreen.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() }
        ) {
            bottomBarState.value = false
            UserCreatedCoursesScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onEditCourse = {
                    navController.navigate(Screen.EditCourse.route)
                }
            )
        }
        composable(
            Screen.EditProfile.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() }
        ) {
            bottomBarState.value = false
            EditProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        /** END: Profile */
    }
}

private const val offset = 300

fun enterTransition() = slideInHorizontally(
    initialOffsetX = { offset },
    animationSpec = tween(offset, easing = FastOutSlowInEasing)
) + fadeIn(animationSpec = tween(offset))

fun exitTransition() = slideOutHorizontally(
    targetOffsetX = { -offset },
    animationSpec = tween(offset, easing = FastOutSlowInEasing)
) + fadeOut(animationSpec = tween(offset))

fun popEnterTransition() = slideInHorizontally(
    initialOffsetX = { -offset },
    animationSpec = tween(offset, easing = FastOutSlowInEasing)
) + fadeIn(animationSpec = tween(offset))

fun popExitTransition() = slideOutHorizontally(
    targetOffsetX = { -offset },
    animationSpec = tween(offset, easing = FastOutSlowInEasing)
) + fadeOut(animationSpec = tween(offset))
