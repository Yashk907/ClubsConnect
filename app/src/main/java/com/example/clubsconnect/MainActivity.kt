package com.example.clubsconnect

import AddEventScreen
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.clubsconnect.FrontEnd.AuthPage.LoginScreen
import com.example.clubsconnect.FrontEnd.AuthPage.SignupScreen
import com.example.clubsconnect.FrontEnd.ClubListStud.ClubsScreen
import com.example.clubsconnect.FrontEnd.FeedPage.MainFeedScreen
import com.example.clubsconnect.FrontEnd.PofileScreen.EditProfileScreen
import com.example.clubsconnect.FrontEnd.detailscreen.EventDetailsScreen
import com.example.clubsconnect.InternalFun.getUserInfoFromFireStore
import com.example.clubsconnect.MembersScreen.ClubMembersScreen
import com.example.clubsconnect.ViewModel.AuthViewModel
import com.example.clubsconnect.ViewModel.EventDetailViewModel
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
                val authViewModel : AuthViewModel =viewModel()
                NavHost(navController = navController, startDestination = Screen.ADDEVENT.name){
                    composable(route = Screen.LOGIN.name){
                        LoginScreen(authViewModel,{
                            navController.navigate(Screen.PROFILESCREEN.name)
                        },navController)
                    }
                    composable(route= Screen.SIGNUP.name){
                        SignupScreen(authViewModel)
                    }
                    composable(route = Screen.ADDEVENT.name){
                        AddEventScreen(viewModel())
                    }

                    composable(route= Screen.MAINSCREEN.name){
                        MainFeedScreen(feedViewModel,navController)
                    }

                    composable(route = "${Screen.DETAILSCREEN.name}/{eventId}") { backStackEntry ->
                        val eventId = backStackEntry.arguments?.getString("eventId") ?: ""

                        val viewModel = remember {
                            EventDetailViewModel(eventId)
                        }

                        EventDetailsScreen(viewModel, onBackPressed = {}, onRegisterClicked = {})
                    }
                    composable(route= Screen.CLUBLISTSCREEN.name){
                        ClubsScreen(viewModel(), onBackPressed = {}) { }
                    }

                    composable(route= Screen.PROFILESCREEN.name){
                        EditProfileScreen()
                    }
                    composable(route= Screen.CLUBMEMBERSSCREEN.name){
                        val context = LocalContext.current
                        val uid = getUserInfoFromFireStore(
                            onError = {
                                Toast.makeText(context,"Problem in retrieving uid",Toast.LENGTH_SHORT).show()
                            }
                        ).first
                        ClubMembersScreen(clubId = uid!!){}
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
    DETAILSCREEN,
    PROFILESCREEN,
    CLUBMEMBERSSCREEN,
    CLUBLISTSCREEN
}