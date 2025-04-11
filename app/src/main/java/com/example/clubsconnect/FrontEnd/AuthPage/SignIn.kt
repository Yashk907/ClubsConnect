package com.example.clubsconnect.FrontEnd.AuthPage

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.clubsconnect.InternalFun.saveUserToPref
import com.example.clubsconnect.Screen
import com.example.clubsconnect.ViewModel.AuthViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

@Composable
fun LoginScreen(viewModel: AuthViewModel,
                onlogin : ()-> Unit,
                navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFECEFF1))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Logo Circle
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "LOGO",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(60.dp))

            // Login Text
            Text(
                text = "Login",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Email Field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
//                colors = TextFieldDefaults.outlinedTextFieldColors(
//                    containerColor = Color.White,
//                    unfocusedBorderColor = Color.Transparent,
//                    focusedBorderColor = Color.Transparent
//                )
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(24.dp))
            // Sign In Button
            Button(
                onClick = {
                    viewModel.SignIn(email,password){
                        success,error->
                        if(success){
                            val uid = FirebaseAuth.getInstance().uid
                            Firebase.firestore.collection("users")
                                .document(uid?: return@SignIn)
                                .get()
                                .addOnSuccessListener { doc ->
                                    if(doc!=null && doc.exists()){
                                        val name = doc.getString("username") ?: "Unknown"
                                        val userType = doc.getString("role") ?: "Student" // if needed

                                        // Save UID and name to SharedPreferences
                                        saveUserToPref(context, uid, name, userType)

                                        Toast.makeText(context, "LogIn successful", Toast.LENGTH_SHORT).show()

                                    } else {
                                        Toast.makeText(context, "User data not found", Toast.LENGTH_SHORT).show()
                                    }

                                }
//                            Toast.makeText(context,"LogIn successful", Toast.LENGTH_SHORT).show()
                            navController.navigate(Screen.CLUBMEMBERSSCREEN.name)
                        }else{
                            Toast.makeText(context,error, Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3F51B5) // Indigo color, you can change to your preferred color
                )
            ) {
                Text(
                    text = "Sign In",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Sign Up Text
            Text(
                text = "Don't have an account? Signup",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color.DarkGray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
//        LoginScreen(viewModel())
    }
}
