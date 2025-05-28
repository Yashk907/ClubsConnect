package com.example.clubsconnect

import AddEventScreen
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.clubsconnect.FrontEnd.commonscreen.AuthPage.LoginScreen
import com.example.clubsconnect.FrontEnd.commonscreen.AuthPage.SignupScreen
import com.example.clubsconnect.FrontEnd.clubside.ClubDashBoard.ClubConnectMainScreen
import com.example.clubsconnect.FrontEnd.clubside.clubSideScreens.ClubSideControlScreen
import com.example.clubsconnect.FrontEnd.clubside.eventDetailScreen.ClubSideEventDetailScreen
import com.example.clubsconnect.FrontEnd.clubside.eventDetailScreen.EventInfoSection
import com.example.clubsconnect.FrontEnd.userside.ClubListStud.ClubsScreen
import com.example.clubsconnect.FrontEnd.userside.FeedPage.MainFeedScreen
import com.example.clubsconnect.FrontEnd.userside.PofileScreen.EditProfileScreen
import com.example.clubsconnect.FrontEnd.commonscreen.SplashScreen.SplashScreen
import com.example.clubsconnect.FrontEnd.userside.detailscreen.EventDetailsScreen
import com.example.clubsconnect.MembersScreen.ClubMembersScreen
import com.example.clubsconnect.ViewModel.AuthViewModel
import com.example.clubsconnect.ViewModel.ClubEventDetailViewModel
import com.example.clubsconnect.ViewModel.EventDetailViewModel
import com.example.clubsconnect.ui.theme.ClubsConnectTheme
import com.google.firebase.FirebaseApp
import getUserInfoFromFireStore

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClubsConnectTheme {
                val navController = rememberNavController()
                val authViewModel : AuthViewModel =viewModel()
                NavHost(navController = navController, startDestination = Screen.SPLASHSCREEN.name){
                    composable(route = Screen.SPLASHSCREEN.name) {
                        SplashScreen(navController)
                    }
                    composable(route = Screen.LOGIN.name){
                        LoginScreen(authViewModel,navController)
                    }
                    composable(route= Screen.SIGNUP.name){
                        SignupScreen(authViewModel,navController){
                            navController.navigate(Screen.LOGIN.name)
                        }
                    }
                    composable(route = Screen.CLUBMAINSCREEN.name) {
                        ClubSideControlScreen(navController)
                    }
                    composable(route ="${Screen.CLUBEVENTDETAILSCREEN.name}/{clubeventId}"){
                        backStackEntry->
                        val eventId = backStackEntry.arguments?.getString("clubeventId") ?: ""
                        val viewModel = remember {
                           ClubEventDetailViewModel(eventId)
                        }
                        ClubSideEventDetailScreen(navController,viewModel){
                            navController.navigateUp()
                        }
                    }


                    composable(route= Screen.MAINSCREEN.name){
                        MainFeedScreen(viewModel(),navController)
                    }

                    composable(route = "${Screen.DETAILSCREEN.name}/{eventId}") { backStackEntry ->
                        val eventId = backStackEntry.arguments?.getString("eventId") ?: ""

                        val viewModel = remember {
                            EventDetailViewModel(eventId)
                        }

                        EventDetailsScreen(viewModel, onBackPressed = {navController})
                    }
                    composable(route= Screen.CLUBLISTSCREEN.name){
                        ClubsScreen(viewModel(), onBackPressed = {}) { }
                    }

                    composable(route= Screen.PROFILESCREEN.name){
                        EditProfileScreen()
                    }
                    composable(route= Screen.CLUBMEMBERSSCREEN.name){
                        val context = LocalContext.current
                        val uid  = remember { mutableStateOf<String?>(null) }

                        getUserInfoFromFireStore(
                            onResult = {
                                (uidd,name,type),email->
                                uid.value=uidd
                            },
                            onError = {
                                Toast.makeText(context,"Problem in retriving uid",Toast.LENGTH_SHORT).show()
                            }
                        )

                        if(uid.value!=null){
                            ClubMembersScreen(uid.value!!) { }
                        }

                    }
                }
            }
        }
    }
}

enum class Screen{
    SPLASHSCREEN,
    LOGIN,
    SIGNUP,
    CLUBMAINSCREEN,
    CLUBEVENTDETAILSCREEN,
    ADDEVENT,
    MAINSCREEN,
    DETAILSCREEN,
    PROFILESCREEN,
    CLUBMEMBERSSCREEN,
    CLUBLISTSCREEN
}