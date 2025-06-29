package com.example.clubsconnect.FrontEnd.userside.PofileScreen
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.clubsconnect.Screen
import com.example.clubsconnect.ViewModel.Clubside.Invitation
import com.example.clubsconnect.ViewModel.Clubside.Student
import com.example.clubsconnect.ViewModel.userside.JoinedClubs
import com.example.clubsconnect.ViewModel.userside.ProfileScreenViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: ProfileScreenViewModel,
                  navcontroller : NavController,
                  modifier: Modifier= Modifier) {
    val student = viewModel.student.collectAsStateWithLifecycle()
    val invitations = viewModel.invitations.collectAsStateWithLifecycle()
    val joinedClubs = viewModel.joinedClubs.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val isLoading = viewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.fetchStudentInfo {
            Toast.makeText(context,it,Toast.LENGTH_SHORT).show()
        }
    }

        if(isLoading.value || student.value==null){
            Box (modifier.fillMaxSize()){
            CircularProgressIndicator(modifier= Modifier.fillMaxSize()
                .padding(100.dp)
                .align(Alignment.Center))}
        }else{
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Profile Header Section
                ProfileHeaderSection(student = student.value!!)

                // Club Invitations Section
                ClubInvitationsSection(viewModel,
                    invitations.value)

                // Joined Clubs Section
                JoinedClubsSection(joinedClubs.value)

                // Account Settings Section
                AccountSettingsSection(navController = navcontroller)

                // Bottom spacing
                Spacer(modifier = Modifier.height(80.dp))
            }
        }



}

@Composable
private fun ProfileHeaderSection(student: Student) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Image Placeholder
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    student.imageUri,
                    contentDescription = "Profile Picture"
                )
            }

            // User Information
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = student.username,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = student.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Role Chip
                AssistChip(
                    onClick = { },
                    label = {
                        Text(
                            text = "🎓 Student",
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }

            // Edit Profile Button
            OutlinedButton(
                onClick = { },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Edit Profile")
            }
        }
    }
}

@Composable
private fun ClubInvitationsSection(viewModel: ProfileScreenViewModel,
                                   invitations : List<Invitation>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Club Invitations",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Sample invitations
        if(invitations.isEmpty()){
            Text(
                text = "No Invitations",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }else{
            invitations.forEach {
                InvitationCard(
                    invitation = it,
                    viewModel=viewModel,
                    clubName = it.clubName,
                    position = it.role)
            }
        }

    }
}

@Composable
private fun InvitationCard(
    viewModel: ProfileScreenViewModel,
    invitation: Invitation,
    clubName: String,
    position: String
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = clubName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Position: $position",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {viewModel.onClubRequestAgreed(invitation = invitation){
                        Toast.makeText(context,it,Toast.LENGTH_SHORT).show()
                    } },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Accept")
                }

                OutlinedButton(
                    onClick = {viewModel.onRejectRequest(invitation) {
                        Toast.makeText(context,it,Toast.LENGTH_SHORT).show()
                    } },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reject")
                }
            }
        }
    }
}

@Composable
private fun JoinedClubsSection(joinedClubs: List<JoinedClubs>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "My Clubs",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Sample joined clubs
        if (joinedClubs.isEmpty()){
            Text(
                text = "No Clubs Joined",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        joinedClubs.forEach {
            JoinedClubCard(clubName = it.clubname, role = it.role)
        }
    }
}

@Composable
private fun JoinedClubCard(
    clubName: String,
    role: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = clubName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = role,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun AccountSettingsSection(navController: NavController) {
    var ChangingPassword = rememberSaveable { mutableStateOf(false) }
    val currentPassword = rememberSaveable { mutableStateOf("") }
    val newPassword = rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val focusRequester1 = remember { FocusRequester() }
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Account Settings",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Change Password Button
                    if(ChangingPassword.value){
                        Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                            Text(
                                text = "Change Password",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            OutlinedTextField(value = currentPassword.value,
                                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                                keyboardActions = KeyboardActions(
                                    onNext = {
                                        focusRequester1.requestFocus()
                                    }
                                ),
                                onValueChange = {currentPassword.value=it},
                                label = {Text("Enter current password",
                                    modifier = Modifier.padding(horizontal = 5.dp))},
                                shape = RoundedCornerShape(30.dp),

                            )
                            OutlinedTextField(value = newPassword.value,
                                onValueChange = {newPassword.value=it},
                                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                                keyboardActions = KeyboardActions(
                                    onNext = {
                                        focusManager.clearFocus()

                                    }
                                ),
                                label = {Text("Enter new password",
                                    modifier = Modifier.padding(horizontal = 5.dp))},
                                shape = RoundedCornerShape(30.dp),
                                modifier = Modifier.focusRequester(focusRequester1)
                            )
                            Box(modifier = Modifier.fillMaxWidth()
                                .padding(top = 15.dp, end = 15.dp)) {
                                OutlinedButton(onClick = {
                                    ChangingPassword.value=false
                                    currentPassword.value=""
                                    newPassword.value=""
                                },
//                                    colors = ButtonDefaults.buttonColors(
//                                        MaterialTheme.colorScheme.error
//                                    ),
                                    modifier = Modifier.align(Alignment.CenterStart)

                                   ) {
                                    Text("Cancel",
                                        color = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.padding(horizontal = 10.dp))
                                }
                                OutlinedButton(onClick = {
                                    val user = FirebaseAuth.getInstance().currentUser
                                    val email = user?.email?:return@OutlinedButton
                                    val credential = EmailAuthProvider.getCredential(email,currentPassword.value)
                                    user.reauthenticate(credential)
                                        .addOnSuccessListener {
                                            user.updatePassword(newPassword.value)
                                                .addOnSuccessListener {
                                                    currentPassword.value=""
                                                    newPassword.value=""
                                                    Toast.makeText(context,
                                                        "Password Updated",
                                                        Toast.LENGTH_SHORT).show()
                                                    ChangingPassword.value=false
                                                }
                                                .addOnFailureListener {
                                                    currentPassword.value=""
                                                    newPassword.value=""
                                                    Toast.makeText(context,
                                                        "Password Update Failed",
                                                        Toast.LENGTH_SHORT).show()
                                                }
                                        }
                                        .addOnFailureListener {
                                            currentPassword.value=""
                                            newPassword.value=""
                                            Toast.makeText(context,
                                                "Incorrect Password",
                                                Toast.LENGTH_SHORT).show()
                                        }

                                },
//                                    colors = ButtonDefaults.buttonColors(
//                                        Color(0xFF6366F1)
//                                    ),
                                    modifier = Modifier.align(alignment = Alignment.CenterEnd)

                                        ) {
                                    Text("Update",
                                        color = Color(0xFF6366F1),
                                        modifier = Modifier.padding(horizontal = 10.dp))
                                }

                            }
                        }

                    }else{
                        OutlinedButton(
                            onClick = {
                                ChangingPassword.value=true
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Change Password")
                        }
                    }


                    // Logout Button
                    Button(
                        onClick = { FirebaseAuth.getInstance().signOut()
                            navController.navigate(Screen.LOGIN.name){
                                popUpTo(0){
                                    inclusive=true
                                }
                                launchSingleTop = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(
                            text = "Logout",
                            color = MaterialTheme.colorScheme.onError
                        )
                    }
                }
            }
        }


}

