package com.example.clubsconnect

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
import com.example.clubsconnect.FrontEnd.AuthPage.LoginScreen
import com.example.clubsconnect.FrontEnd.AuthPage.SignupScreen
import com.example.clubsconnect.ui.theme.ClubsConnectTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClubsConnectTheme {
                LoginScreen(viewModel())
//                SignupScreen(viewModel())
            }
        }
    }
}

