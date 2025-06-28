package com.example.clubsconnect.FrontEnd.clubside.membersScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.PeopleOutline
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.clubsconnect.ViewModel.Clubside.ClubMember
import com.example.clubsconnect.ViewModel.Clubside.ManageMembersViewmodel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageMembersScreen(viewmodel: ManageMembersViewmodel,
                        onback : ()-> Unit) {
    val clubmembers by viewmodel.clubMembers.collectAsStateWithLifecycle()
    val uiState by viewmodel.uistate.collectAsState()
    LaunchedEffect(Unit) {
        viewmodel.loadMembers() {}
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Members") },
                navigationIcon = {
                    IconButton(onClick = onback) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.padding(padding))
            }

            uiState.errormessage != null -> {
                Text("Error : ${uiState.errormessage}", color = Color.Red,
                    fontSize = 30.sp,
                    modifier = Modifier.padding(padding)
                        )
            }

            clubmembers.isEmpty()->{
                Box (modifier = Modifier.padding(padding)
                    .fillMaxSize()){
                    Column (modifier = Modifier.fillMaxWidth()
                        .align(Alignment.Center)
                        .padding(bottom = 100.dp)){
                        Icon(imageVector = Icons.Default.People,
                            contentDescription = "people photo",
                            modifier = Modifier.size(200.dp)
                                .align(Alignment.CenterHorizontally),
                            tint = Color.LightGray
                        )
                        Text("There are no members Associated with club!!",
                            color = Color.LightGray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 50.dp))
                    }


                }

            }
            else ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(clubmembers) { member ->
                        MemberCard(member = member)
                    }
                }

        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberCard(member: ClubMember) {


    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFf4fafd)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Card(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF3f5ce7)
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
                            model = member.avatar,
                            contentDescription = "imageuri",
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Member Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = member.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = member.email,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }

                // Delete Button
                IconButton(onClick = { /* Delete member */ }) {
                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Role Dropdown
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Role:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.width(12.dp))

                OutlinedTextField(
                    value = member.role,
                    onValueChange = {},
                    readOnly = true
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ManageMembersScreenPreview() {
    MaterialTheme {
        ManageMembersScreen(viewModel(), {})
    }
}