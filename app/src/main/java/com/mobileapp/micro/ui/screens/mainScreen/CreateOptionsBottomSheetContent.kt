package com.mobileapp.micro.ui.screens.mainScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.mobileapp.micro.Screen
import com.mobileapp.micro.ui.theme.Blue90

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CreateOptionsBottomSheetContent(
    navController: NavHostController,
    onCloseSheet: () -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        CreateChoiceBtn(
            optionTitle = "New Course",
            description = "Finite sequential micro lessons. " +
                    "People can see and study this course.",
            onClick = {
                onCloseSheet()
                navController.navigate(Screen.EditCourse.route)
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        CreateChoiceBtn(
            optionTitle = "New Channel",
            description = "Infinite non-sequential micro lessons. " +
                    "People can subscribe and suggest other lessons.",
            onClick = {
                onCloseSheet()
                navController.navigate(Screen.CreateChannel.route)
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        CreateChoiceBtn(
            optionTitle = "New Study Group",
            description = "Create Study Group, add members, collaborate and discuss shared lessons.",
            onClick = {
                onCloseSheet()
                navController.navigate(Screen.CreateStudyGroup.route)
            }
        )
    }
}

@Composable
private fun CreateChoiceBtn(
    optionTitle: String,
    description: String,
    onClick: () -> Unit
) {
    TextButton(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Blue90),
        onClick = {
            onClick()
        }
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = optionTitle,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
                //color = MaterialTheme.colors.onPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                fontWeight = FontWeight.Light,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
@Preview
fun CreateChoiceBtnPreview() {
    Surface {
        Column(modifier = Modifier.padding(16.dp)) {
            CreateChoiceBtn(
                optionTitle = "New Course",
                description = "Finite sequential micro lessons. " +
                        "People can see and study this course.",
                onClick = {}
            )
        }
    }
}
