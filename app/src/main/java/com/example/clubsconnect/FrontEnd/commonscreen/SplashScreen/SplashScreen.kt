package com.example.clubsconnect.FrontEnd.commonscreen.SplashScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.clubsconnect.R
import com.example.clubsconnect.Screen
import com.google.firebase.auth.FirebaseAuth
import getUserInfoFromFireStore
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        delay(2000) // 2-second splash delay

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            getUserInfoFromFireStore(onResult = {
                (_, _, type), _ ->
                when(type){
                    "Student" -> navController.navigate(Screen.MAINSCREENUSER.name){
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                    "Club" -> navController.navigate(Screen.CLUBMAINSCREENCLUB.name){
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                    else -> navController.navigate(Screen.LOGIN.name){
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            })
        } else {
            navController.navigate(Screen.LOGIN.name) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF3F51B5)), // Indigo background
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = R.drawable.applogo),
                contentDescription = "Logo",
                tint = Color.White,
                modifier = Modifier.size(200.dp)
            )
//            Spacer(modifier = Modifier.height(16.dp))
//            Text("My App", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}
