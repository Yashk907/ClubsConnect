package com.example.clubsconnect

import EditEventScreen
import ManageClubScreen
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.clubconnect.ui.screens.ClubEditProfile
import com.clubconnect.ui.screens.StudentEditProfileScreen
import com.example.clubsconnect.FrontEnd.commonscreen.AuthPage.LoginScreen
import com.example.clubsconnect.FrontEnd.clubside.clubSideScreens.ClubSideControlScreen
import com.example.clubsconnect.FrontEnd.clubside.eventDetailScreen.ClubSideEventDetailScreen
import com.example.clubsconnect.FrontEnd.clubside.membersScreen.AddMembersScreen
import com.example.clubsconnect.FrontEnd.clubside.membersScreen.ManageMembersScreen
import com.example.clubsconnect.FrontEnd.commonscreen.AuthPage.SignupScreen
import com.example.clubsconnect.FrontEnd.userside.ClubListStud.ClubsScreen
import com.example.clubsconnect.FrontEnd.commonscreen.SplashScreen.SplashScreen
import com.example.clubsconnect.FrontEnd.userside.detailscreen.EventDetailsScreen
import com.example.clubsconnect.FrontEnd.userside.userscreencontrol.UserScreenControl
import com.example.clubsconnect.ViewModel.AuthViewModel
import com.example.clubsconnect.ViewModel.Clubside.ClubEventDetailViewModel
import com.example.clubsconnect.ViewModel.Clubside.ClubProfileViewmodel
import com.example.clubsconnect.ViewModel.userside.EventDetailViewModel
import com.example.clubsconnect.ui.theme.ClubsConnectTheme
import com.google.firebase.FirebaseApp

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
                val clubProfileViewmodel : ClubProfileViewmodel  = viewModel()


                NavHost(navController = navController, startDestination = Screen.SPLASHSCREEN.name){
                    //common screens
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

                    //clubs screens
                    composable(route = Screen.CLUBMAINSCREENCLUB.name) {
                        ClubSideControlScreen(navController,
                            clubProfileViewmodel)
                    }
                    composable(route ="${Screen.CLUBEVENTDETAILSCREENCLUB.name}/{clubeventId}"){
                        backStackEntry->
                        val eventId = backStackEntry.arguments?.getString("clubeventId") ?: ""
                        val viewModel = remember {
                           ClubEventDetailViewModel(eventId)
                        }
                        ClubSideEventDetailScreen(navController,viewModel){
                            navController.navigateUp()
                        }
                    }

                    composable(route ="${Screen.CLUBEDITEVENTCLUB.name}/{eventID}",
                        arguments = listOf(navArgument("eventID") { type = NavType.StringType })) {
                            backstackEntry->
                        val eventId = backstackEntry.arguments?.getString("eventID") ?: ""
                        EditEventScreen(eventId = eventId, navController, viewModel())
                    }

                    composable(route = Screen.CLUBMANAGEMEMBERS.name){
                        ManageMembersScreen(viewModel()){
                            navController.navigateUp()
                        }
                    }
                    composable (route = Screen.CLUBADDMEMBERSCREEN.name){
                        AddMembersScreen(viewModel()) {
                            navController.navigateUp()
                        }
                    }
                    composable(route = Screen.CLUBEDITPROFILE.name) {
                        ClubEditProfile(viewmodel = clubProfileViewmodel,
                            onbackclick = {
                            navController.navigateUp()
                        })
                    }


                    //users screens
                    composable(route= Screen.MAINSCREENUSER.name){
                        UserScreenControl(navController)
                    }

                    composable(route ="${Screen.DETAILSCREENUSER.name}/{eventId}") { backStackEntry ->
                        val eventId = backStackEntry.arguments?.getString("eventId") ?: ""

                        val viewModel = remember {
                            EventDetailViewModel(eventId)
                        }

                        EventDetailsScreen(viewModel, onBackPressed = {navController.navigateUp()})
                    }

                    composable(route= Screen.CLUBLISTSCREENUSER.name){
                        ClubsScreen(viewModel(), onBackPressed = {}) { }
                    }
                    composable(route= Screen.EDITPROFILEUSERSCREEN.name) {
                        StudentEditProfileScreen(viewmodel = viewModel()){
                            navController.navigateUp()
                        }
                    }
                    composable(route = "${Screen.USERSIDECLUBPROFILE.name}/{clubId}") {
                        backStackEntry ->
                        val clubId = backStackEntry.arguments?.getString("clubId")?:""
                        ManageClubScreen(onbackclick = {
                            navController.navigateUp()
                        },
                            clubId = clubId)}

                }
            }
        }
    }
}

enum class Screen{
    SPLASHSCREEN,
    LOGIN,
    SIGNUP,
    //clubs
    CLUBMAINSCREENCLUB,
    CLUBEVENTDETAILSCREENCLUB,
    CLUBEDITEVENTCLUB,
    ADDEVENTCLUB,
    CLUBEDITPROFILE,
    CLUBMANAGEMEMBERS,
    CLUBADDMEMBERSCREEN,
    //user
    MAINSCREENUSER,
    DETAILSCREENUSER,
    CLUBLISTSCREENUSER,
    EDITPROFILEUSERSCREEN,
    USERSIDECLUBPROFILE
}