package com.example.clubsconnect

import AddEventScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.Navigation
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.clubsconnect.FrontEnd.AuthPage.LoginScreen
import com.example.clubsconnect.FrontEnd.AuthPage.SignupScreen
import com.example.clubsconnect.FrontEnd.FeedPage.MainFeedScreen
import com.example.clubsconnect.FrontEnd.detailscreen.EventDetailsScreen
import com.example.clubsconnect.Model.Event
import com.example.clubsconnect.ViewModel.FeedViewModel
import com.example.clubsconnect.ui.theme.ClubsConnectTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClubsConnectTheme {
                val navController = rememberNavController()
                val feedViewModel : FeedViewModel = viewModel()
                NavHost(navController = navController, startDestination = Screen.MAINSCREEN.name){
                    composable(route = Screen.LOGIN.name){
                        LoginScreen(viewModel(),{
                            navController.navigate(Screen.ADDEVENT.name)
                        },navController)
                    }
                    composable(route = Screen.ADDEVENT.name){
                        AddEventScreen(viewModel())
                    }

                    composable(route= Screen.MAINSCREEN.name){
                        MainFeedScreen(feedViewModel,navController)
                    }

                    composable(route = Screen.DETAILSCREEN.name,){
                        EventDetailsScreen(feedViewModel, onBackPressed = {},) { }
                    }

                }
            }
        }
    }
}

enum class Screen{
    LOGIN,
    SIGNUP,
    ADDEVENT,
    MAINSCREEN,
    DETAILSCREEN
}