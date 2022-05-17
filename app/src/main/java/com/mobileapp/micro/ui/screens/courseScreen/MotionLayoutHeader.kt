package com.mobileapp.micro.ui.screens.courseScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.MotionLayout
import coil.compose.rememberImagePainter
import com.mobileapp.micro.R
import com.mobileapp.micro.viewmodel.CourseViewModel

@OptIn(
    ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class,
    androidx.constraintlayout.compose.ExperimentalMotionApi::class
)
@Composable
fun MotionLayoutHeader(
    progress: Float,
    courseViewModel: CourseViewModel,
    onNavigateBack: () -> Unit,
    scrollableBody: @Composable () -> Unit
) {
    MotionLayout(
        start = JsonConstraintSetStart(),
        end = JsonConstraintSetEnd(),
        progress = progress,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Image(
            painter = rememberImagePainter(
                courseViewModel.currentStudyCourse.value.onlineImageUri.ifEmpty { R.drawable.image_placeholder }
            ),
            contentDescription = "display image",
            modifier = Modifier
                .layoutId("poster")
                .background(MaterialTheme.colors.primary)
                .height(250.dp),
            contentScale = ContentScale.Crop,
            alpha = 1f - progress
        )
        IconButton(
            modifier = Modifier.padding(start = 8.dp, top = 4.dp),
            onClick = { onNavigateBack() }
        ) {
            Icon(
                tint = Color.White,
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back"
            )
        }
        Text(
            modifier = Modifier
                .layoutId("title")
                .fillMaxWidth()
                .padding(16.dp),
            text = courseViewModel.currentStudyCourse.value.title,
            color = motionColor("title", "textColor"),
            fontSize = motionFontSize("title", "textSize"),
        )
        Box(
            Modifier
                .layoutId("content")
                .fillMaxSize()
        ) {
            scrollableBody()
        }
    }
}

@Composable
private fun JsonConstraintSetStart() = ConstraintSet(
    """ {
	poster: { 
		width: "spread",
		start: ['parent', 'start', 0],
		end: ['parent', 'end', 0],
		top: ['parent', 'top', 0],
	},
	title: {
		top: ['poster', 'bottom', 0],
		start: ['parent', 'start', 0],
		custom: {
			textColor: "#000000", 
			textSize: 20
		}
	},
	content: {
		width: "spread",
		start: ['parent', 'start', 0],
		end: ['parent', 'end', 0],
		top: ['title', 'bottom', 0],
	}
} """
)

@Composable
private fun JsonConstraintSetEnd() = ConstraintSet(
    """ {
	poster: { 
		width: "spread",
		height: 56,
		start: ['parent', 'start', 0],
		end: ['parent', 'end', 0],
		top: ['parent', 'top', 0],
	},
	title: {
		top: ['parent', 'top', 0],
		start: ['parent', 'start', 150],
		end: ['parent', 'end', 30], 
		bottom: ['poster', 'bottom', 0],
		custom: {
			textColor: "#ffffff",
			textSize: 18
        }
	},
	content: {
		width: "spread",
		start: ['parent', 'start', 0],
		end: ['parent', 'end', 0],
		top: ['poster', 'bottom', 0],
	}
                  
} """
)