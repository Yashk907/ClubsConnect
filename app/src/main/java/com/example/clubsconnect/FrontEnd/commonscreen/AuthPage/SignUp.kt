package com.example.clubsconnect.FrontEnd.commonscreen.AuthPage



import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.clubsconnect.Screen
import com.example.clubsconnect.ViewModel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import getUserInfoFromFireStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(viewModel: AuthViewModel ,
                 navController : NavController,
                 movetoSignIn : ()->Unit) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
//    var prnNo by remember { mutableStateOf("") }
    var selectedOption by remember { mutableStateOf("Student/Club") }
    val focusManager = LocalFocusManager.current
    val focusRequester0 = remember { FocusRequester() }
    val focusRequester1 = remember { FocusRequester() }
    val focusRequester2 = remember { FocusRequester() }
    val focusRequester3 = remember { FocusRequester() }
    val focusRequester4 = remember { FocusRequester() }
    val focusRequesterButton = remember { FocusRequester() }
    val scrollState = rememberScrollState()
    Scaffold {
        padding->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFECEFF1))

        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
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

                // Signup Text
                Text(
                    text = "SignUp",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Username Field
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusRequester1.requestFocus()
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                        .focusRequester(focusRequester0),
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent
                    )
                )

//                Spacer(modifier = Modifier.height(12.dp))
//
//                // PRN No. Field
//                OutlinedTextField(
//                    value = prnNo,
//                    onValueChange = { prnNo = it },
//                    label = { Text("PRN No.") },
//                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
//                    keyboardActions = KeyboardActions(
//                        onNext = {
//                            focusRequester2.requestFocus()
//                        }
//                    ),
//                    modifier = Modifier.fillMaxWidth()
//                        .focusRequester(focusRequester1),
//                    shape = RoundedCornerShape(24.dp),
//                    colors = TextFieldDefaults.colors(
//                        unfocusedContainerColor = Color.Transparent,
//                        focusedContainerColor = Color.Transparent
//                    )
//                )

                Spacer(modifier = Modifier.height(12.dp))


                // Email Field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = {
                        focusRequester3.requestFocus()
                    }),
                    modifier = Modifier.fillMaxWidth()
                        .focusRequester(focusRequester2),
                    shape = RoundedCornerShape(24.dp),
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
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = {
                        focusRequester4.requestFocus()
                    }),
                    modifier = Modifier.fillMaxWidth()
                        .focusRequester(focusRequester3),
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Dropdown Field
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedOption,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = "Dropdown Arrow"
                            )
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = {
                            focusRequesterButton.requestFocus()
                        }),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester4)
                            .menuAnchor(),
                        shape = RoundedCornerShape(24.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.exposedDropdownSize()
                    ) {
                        DropdownMenuItem(
                            text = { Text("Student") },
                            onClick = {
                                selectedOption = "Student"
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Club") },
                            onClick = {
                                selectedOption = "Club"
                                expanded = false
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Sign Up Button
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.SignUp(email,password,selectedOption,username){
                                success,error->
                            if(success){
                                Toast.makeText(context,"Sign Up success", Toast.LENGTH_SHORT).show()
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
                                        popUpTo(Screen.LOGIN.name) { inclusive = true }
                                    }
                                }
                            }else{
                                Toast.makeText(context,"$error", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequesterButton)
                        .height(50.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3F51B5) // Indigo color, you can change to your preferred color
                    )
                ) {
                    Text(
                        text = "Sign Up",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }


                Spacer(modifier = Modifier.height(24.dp))

                // Login Text
                Text(
                    text = "Already have an account? Login",
                    modifier = Modifier.fillMaxWidth()
                        .padding(bottom = 40.dp)
                        .clickable { movetoSignIn()},
                    textAlign = TextAlign.Center,
                    color = Color.DarkGray
                )
            }
        }
    }


}

//@Preview(showBackground = true)
//@Composable
//fun SignupScreenPreview() {
//    MaterialTheme {
//        SignupScreen(viewModel())
//    }
//}