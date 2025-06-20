package com.example.clubsconnect.FrontEnd.clubside.membersScreen

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.clubsconnect.ViewModel.Clubside.AddMemberViewModel
import com.example.clubsconnect.ViewModel.Clubside.Student
import com.example.clubsconnect.ViewModel.Clubside.studentWithStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMembersScreen(
    viewModel: AddMemberViewModel,
    onback : ()-> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val users by viewModel.students.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.loadStudents()
    }

    val filteredUsers = users.filter {(student,_)->

        student.username.contains(searchQuery, ignoreCase = true) ||
                student.email.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Members") },
                navigationIcon = {
                    IconButton(onClick = onback) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        padding->
        // Search Bar
        Column(modifier = Modifier.padding(padding)
            .fillMaxSize()) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search users by name or email...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                shape = RoundedCornerShape(12.dp)
            )
            if(isLoading){
                CircularProgressIndicator(modifier = Modifier.padding(padding)
                    .align(alignment = Alignment.CenterHorizontally))
            }else{
                // Users List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredUsers) { user ->
                        UserCard(
                            user = user,
                            onInvite = { selectedUser, role ->
                                viewModel.onInvite(selectedUser,role) {
                                    Toast.makeText(context,it, Toast.LENGTH_SHORT).show()
                                }
                            },
                            onResendInvite = { selectedUser, role ->
                                viewModel.onResend(selectedUser)
                            },
                            onCancelInvite = { selectedUser ->
                                // Cancel pending invitation
                                viewModel.onCancelInvite(selectedUser)
                            }
                        )
                    }

                    if (filteredUsers.isEmpty() && searchQuery.isNotEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "No users found matching \"$searchQuery\"",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }

            }
        }


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserCard(
    user: studentWithStatus,
    onInvite: (studentWithStatus, String) -> Unit,
    onResendInvite: (studentWithStatus, String) -> Unit,
    onCancelInvite: (studentWithStatus) -> Unit
) {
    var selectedRole by remember { mutableStateOf("Member") }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (user.status) {
                "pending" -> Color(0xFFFFF3CD) // Light yellow for pending
                "declined" -> Color(0xFFF8D7DA) // Light red for declined
                else -> Color(0xFFf4fafd) // Default light blue
            }
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // User Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Card(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF24ab66)
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
//                        Text(
//                            text = user.student.imageUri,
//                            color = MaterialTheme.colorScheme.onTertiary,
//                            fontWeight = FontWeight.Bold
//                        )
                        AsyncImage(
                            model = user.student.imageUri,
                            contentDescription = "imageuri",
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // User Info
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = user.student.username,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Status Badge
                        if (user.status != "none") {
                            Spacer(modifier = Modifier.width(8.dp))
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = when (user.status) {
                                        "pending" -> Color(0xFFffc107)
                                        "declined" -> Color(0xFFdc3545)
                                        else -> Color.Transparent
                                    }
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = user.status.uppercase(),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                    Text(
                        text = user.student.email,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Row based on status
            when (user.status) {
                "none" -> {
                    // Show role selection and invite button for users with no status
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Role Insert
                        OutlinedTextField(
                            shape = RoundedCornerShape(20.dp),
                            value = selectedRole,
                            onValueChange = { selectedRole = it },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done
                            )
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        // Invite Button
                        Button(
                            onClick = { onInvite(user, selectedRole) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF24ab66)
                            )
                        ) {
                            Icon(
                                Icons.Default.Send,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Invite")
                        }
                    }
                }

                "pending" -> {
                    // Show pending status with cancel option
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Invitation sent and pending response",
                            fontSize = 14.sp,
                            color = Color(0xFF856404),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedButton(
                            onClick = { onCancelInvite(user) },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF6c757d)
                            ),
                            border = BorderStroke(1.dp, Color(0xFF6c757d))
                        ) {
                            Text("Cancel")
                        }
                    }
                }

                "declined" -> {
                    // Show declined status with resend option
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Invitation declined",
                                fontSize = 14.sp,
                                color = Color(0xFF721c24),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Button(
                            onClick = { onResendInvite(user, selectedRole) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF24ab66)
                            )
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Resend")
                        }
                    }
                }
            }
        }
    }
}

