package com.mobileapp.micro.ui.screens.mainScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavHostController
import com.mobileapp.micro.Screen
import com.mobileapp.micro.common.getCurrentRoute

/**
 * It receives navController to navigate between screens.
 */

private val navItems = listOf(Screen.Home, Screen.Search, null, Screen.Learning, Screen.Profile)

@ExperimentalAnimationApi
@Composable
fun BottomNavBar(
    navController: NavHostController,
    bottomBarState: MutableState<Boolean>
) {
    val currentRoute = getCurrentRoute(navController)
    AnimatedVisibility(
        visible = bottomBarState.value,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        content = {
            BottomAppBar(
                cutoutShape = RoundedCornerShape(50),
                content = {
                    BottomNavigation {
                        navItems.forEach { navItem ->
                            if (navItem != null) {
                                BottomNavigationItem(
                                    selected = navItem.route == currentRoute,
                                    onClick = {
                                        navController.navigate(navItem.route) {
                                            popUpTo(Screen.Home.route)
                                        }
                                    },
                                    icon = {
                                        val icon = navItem.icon
                                            ?: ImageVector.vectorResource(navItem.iconResourceId!!)
                                        Icon(imageVector = icon, contentDescription = navItem.title)
                                    },
                                    label = {
                                        Text(text = navItem.title)
                                    },
                                    alwaysShowLabel = true
                                )
                            } else {
                                // Empty entry to place the FAB btn on it's place
                                BottomNavigationItem(
                                    selected = false,
                                    enabled = false,
                                    onClick = {},
                                    icon = {},
                                    label = {},
                                    alwaysShowLabel = false
                                )
                            }
                        }
                    }
                }
            )
        }
    )
}