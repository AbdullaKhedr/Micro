package com.mobileapp.micro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import com.google.firebase.database.FirebaseDatabase
import com.mobileapp.micro.ui.screens.mainScreen.MainScreen
import com.mobileapp.micro.ui.theme.MicroTheme

@ExperimentalComposeUiApi
@ExperimentalAnimationApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        setContent {
            MicroTheme {
                MainScreen()
            }
        }
    }
}
