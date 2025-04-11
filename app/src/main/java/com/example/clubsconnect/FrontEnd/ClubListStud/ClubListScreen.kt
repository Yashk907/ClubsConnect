package com.example.clubsconnect.FrontEnd.ClubListStud

import com.example.clubsconnect.R


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.clubsconnect.Model.Club
import com.example.clubsconnect.ViewModel.ClubListViewModelStud

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubsScreen(
    viewModelStud: ClubListViewModelStud,
    onBackPressed: () -> Unit,
    onClubClicked: (String) -> Unit
) {
    val backgroundColor = Color(0xFFF2F3F5)
    val clubs = viewModelStud.clublist
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Clubs", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(backgroundColor)
        ) {
            // Filters Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filters",
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Filters",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Club Items
            LazyColumn (
                Modifier
                .fillMaxWidth()
                    .weight(1f)
            ){
                items(clubs){
                    club->
                    ClubItem(club)
                }
            }



        }
    }
}

@Composable
fun ClubItem(
    club: Club
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = {}),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Club Logo
            AsyncImage(
                model = club.imageUri,
                contentDescription = "club logo",
                modifier = Modifier.size(40.dp)
            )

            // Club Name
            Text(
                text = club.username,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodyLarge
            )

            // Arrow Icon
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "View details",
                tint = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ClubsScreenPreview() {
    MaterialTheme {
        ClubsScreen(
            viewModel(),
            onBackPressed = {},
            onClubClicked = {}
        )
    }
}