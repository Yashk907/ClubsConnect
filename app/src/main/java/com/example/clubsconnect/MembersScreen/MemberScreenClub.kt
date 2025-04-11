package com.example.clubsconnect.MembersScreen



import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.clubsconnect.ViewModel.Member
import com.example.clubsconnect.ViewModel.MembersViewModel
import com.example.clubsconnect.ViewModel.MembersViewModelFactory
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@Composable
fun ClubMembersScreen(
    clubId: String,
    onNavigateBack: () -> Unit
) {
    val viewModel: MembersViewModel = viewModel(factory = MembersViewModelFactory(clubId))
    var showDialog by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf("") }
    var clubName by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        Firebase.firestore.collection("clubs")
            .document(clubId)
            .get()
            .addOnSuccessListener { result->
                imageUri = result.getString("imageUri") ?: ""
                clubName= result.getString("username")?:""
            }
    }
    Log.d("yash",imageUri)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F4F5))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header with logo
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            ) {
                // Club Logo
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    // Replace with your actual club logo
                    if(imageUri!=""){
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Club Logo",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentScale = ContentScale.Fit
                        )
                    }else{
                        Image(
                            painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                            contentDescription = "Club Logo",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                }

                Spacer(modifier = Modifier.height(16.dp))

                // Club Name
                Text(
                    text = clubName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Members section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            ) {
                // Members header with back button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(
                        text = "Members",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Members list
                LazyColumn {
                    items(viewModel.members) { member ->
                        MemberItem(member = member)
                    }
                }
            }
        }

        // Add Member button with improved styling
        Button(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(8.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 2.dp
            )
        ) {
            Text(
                text = "Add Member",
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        // Display add member dialog
        if (showDialog) {
            AddMemberDialog(
                onDismiss = { showDialog = false },
                onAdd = { name ,email->
                    viewModel.addMember(name,email) {
                        showDialog = false
                    }
                }
            )
        }
    }
}

@Composable
fun MemberItem(member: Member) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = member.name,
                fontSize = 16.sp,
                color = Color(0xFF5F7285),
                fontWeight = FontWeight.Medium
            )

            IconButton(onClick = { /* Handle more options click */ }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = Color(0xFF5F7285)
                )
            }
        }
    }
}

@Composable
fun AddMemberDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var emaiId by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                if (name.isNotBlank()) onAdd(name,emaiId)
            }) {
                Text("Add", color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Add New Member", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Member Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = emaiId,
                    onValueChange = { emaiId = it },
                    label = { Text("email Id") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
            }


        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}