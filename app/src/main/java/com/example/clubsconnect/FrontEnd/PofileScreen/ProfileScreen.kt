package com.example.clubsconnect.FrontEnd.PofileScreen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clubsconnect.InternalFun.getUserInfoFromPrefs
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

@Preview(showSystemUi = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen() {
    val context = LocalContext.current
      val currentUser = FirebaseAuth.getInstance().currentUser
   
    var name = remember { mutableStateOf(getUserInfoFromPrefs(context).second)}

    val firestore = Firebase.firestore
  
    var imagerUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imagerUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F4F5))
    ) {
        // Top Bar
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                        contentDescription = "Back",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Edit Profile",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            modifier = Modifier.background(Color.White)
        )

        // Profile Picture Section
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                ) {
                    // Profile picture placeholder or actual image
                    Image(
                        painter = painterResource(id = android.R.drawable.ic_menu_camera),
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "change profile picture",
                    color = Color(0xFF2196F3),
                    fontSize = 14.sp,
                    modifier = Modifier.clickable {
                        imagePickerLauncher.launch("profile_img/*")
                    }
                )
            }
        }

        // Name Field
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Text(
                text = "Name",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = name.value,
                onValueChange = { name.value= it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray
                )
            )
        }

        // Save Button
        Spacer(modifier = Modifier.weight(1f))



        Button(
            onClick = {
                if(name.value!=getUserInfoFromPrefs(context).second){
                currentUser?.uid?.let { uid ->
                    val updatedData = mapOf("username" to name)

                    firestore.collection("users")
                        .document(uid)
                        .update(updatedData)
                        .addOnSuccessListener {
                            firestore.collection("students")
                                .document(uid)
                                .update(updatedData)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener{
                                      Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show()
                                }
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show()
                        }
                } }
                if(imagerUri!=null){
                      currentUser?.uid?.let { uid ->
                          val updatedData = mapOf("imageUri" to imagerUri)

                          firestore.collection("students")
                              .document(uid)
                              .update(updatedData)
                              .addOnSuccessListener {
                                  Toast.makeText(context,"Image is also updated", Toast.LENGTH_SHORT).show()
                              }
                              .addOnFailureListener{
                                  Toast.makeText(context,"Image update failed", Toast.LENGTH_SHORT).show()
                              }
                      }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0A2540)
            ),
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = "Save",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

    }
}