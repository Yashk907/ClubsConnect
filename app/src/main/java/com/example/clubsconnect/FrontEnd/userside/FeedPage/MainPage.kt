package com.example.clubsconnect.FrontEnd.userside.FeedPage

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.clubsconnect.Model.Event
import com.example.clubsconnect.Screen
import com.example.clubsconnect.ViewModel.FeedViewModel

@Composable
fun MainFeedScreen(viewModel: FeedViewModel,
                   navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = { TopAppBar() },
        bottomBar = { BottomNavigation(selectedTab) { selectedTab = it } },
        content = { paddingValues ->
            EventsScreen(viewModel,
                paddingValues,navController)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "VIIT Pune",
                fontWeight = FontWeight.Bold
            )
        },
        actions = {
            IconButton(onClick = { /* Handle notifications */ }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications"
                )
            }
        }
    )
}

@Composable
fun EventsScreen(viewModel: FeedViewModel,
                 paddingValues: PaddingValues,
                 navController: NavController) {
    val events = viewModel.events
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Events",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            IconButton(onClick = { /* Handle filter/menu */ }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Filter"
                )
            }
        }

        //scrolling column of events

        LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
           items(events){
               event->
               EventCard(event,navController)
           }
        }
        // Nirman 3.0 Hackathon Card

    }
}
@Composable
fun EventCard(
    event: Event,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // Show image if imageUrl is not null
            if (!event.imageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = event.imageUrl,
                    contentDescription = "Event Poster",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = event.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = event.description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = event.type,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Button(
                        onClick = {navController.navigate("${Screen.DETAILSCREENUSER.name}/${event.id}")},
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1A237E)
                        ),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            text = "See details",
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun BottomNavigation(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Groups, contentDescription = "Clubs") },
            label = { Text("Clubs") },
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) }
        )

        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_menu_today),
                    contentDescription = "Attendance"
                )
            },
            label = { Text("Attendance") },
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VIITPuneAppPreview() {
    MaterialTheme {
        MainFeedScreen(viewModel(),
            rememberNavController()
        )
    }
}