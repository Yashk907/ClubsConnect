package com.example.clubsconnect.FrontEnd.userside.FeedPage

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.clubsconnect.Model.Event
import com.example.clubsconnect.Screen
import com.example.clubsconnect.ViewModel.userside.Club
import com.example.clubsconnect.ViewModel.userside.FeedViewModel

@Composable
fun MainFeedScreen(
    viewModel: FeedViewModel,
    navController: NavController
) {
    var selectedTab by remember { mutableStateOf(0) }
    var selectedFilter by remember { mutableStateOf("Events") }
    val context = LocalContext.current
    var backPressedTime by remember { mutableStateOf(0L) }

    val events = viewModel.events
    val clubs = viewModel.clubs

    BackHandler {
        val currentTime = System.currentTimeMillis()
        if (currentTime - backPressedTime <= 2000) {
            (context as? Activity)?.finish()
        } else {
            Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT).show()
            backPressedTime = currentTime
        }
    }

    Scaffold(
        topBar = { TopAppBar() },
        bottomBar = {
            BottomNavigation(selectedTab) { selectedTab = it }
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        LazyColumn (
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Welcome Card
            item{
                WelcomeCard()
            }

            // Filter Chips Section
            item{
                FilterChipsSection(
                    selectedFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it }
                )
            }

            // Content based on selected filter

                when (selectedFilter) {
                    "Events" -> items(events){event->
                        EventCard(event, navController,
                            modifier = Modifier.padding(20.dp))
                    }
                    "Clubs" -> items(clubs.value){
                        club->
                        ClubCard(club, modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp))
                    }
                }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar() {
    CenterAlignedTopAppBar(
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "VIIT Pune",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF1A237E)
                )
                Text(
                    text = "ClubConnect",
                    fontSize = 12.sp,
                    color = Color(0xFF6C7B7F),
                    fontWeight = FontWeight.Medium
                )
            }
        },
        actions = {
            IconButton(
                onClick = { /* Handle notifications */ },
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFE3F2FD),
                                Color(0xFFBBDEFB)
                            )
                        ),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color(0xFF1A237E)
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White
        ),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun WelcomeCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF667eea),
                            Color(0xFF764ba2)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "Welcome back! 👋",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Discover amazing events and connect with clubs",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    lineHeight = 22.sp
                )
            }
        }
    }
}

@Composable
fun FilterChipsSection(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    val filters = listOf("Events", "Clubs")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White,
                        Color(0xFFF8F9FA)
                    )
                )
            )
            .padding(16.dp)
    ) {
        Text(
            text = "Discover",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filters) { filter ->
                FilterChip(
                    onClick = { onFilterSelected(filter) },
                    label = {
                        Text(
                            text = filter,
                            fontWeight = if (selectedFilter == filter) FontWeight.SemiBold else FontWeight.Medium
                        )
                    },
                    selected = selectedFilter == filter,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF1A237E),
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFFE8EAF6),
                        labelColor = Color(0xFF1A237E)
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selectedFilter == filter,
                        borderColor = if (selectedFilter == filter) Color.Transparent else Color(0xFFE0E0E0)
                    )
                )
            }
        }
    }
}



@Composable
fun ClubsContent() {
    // Placeholder for clubs content
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🏛️",
                fontSize = 48.sp
            )
            Text(
                text = "Clubs Coming Soon!",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6C7B7F)
            )
        }
    }
}

@Composable
fun EventCard(
    event: Event,
    navController: NavController,
    modifier: Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Event Image with Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                if (!event.imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = event.imageUrl,
                        contentDescription = "Event Poster",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    )
                } else {
                    // Placeholder gradient when no image
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF6366F1),
                                        Color(0xFF8B5CF6)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🎉",
                            fontSize = 48.sp
                        )
                    }
                }

                // Event Type Badge
                Card(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black.copy(alpha = 0.7f)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = event.type,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            // Event Details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = event.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = event.description,
                    fontSize = 14.sp,
                    color = Color(0xFF6C7B7F),
                    lineHeight = 20.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Action Button with Gradient
                Button(
                    onClick = {
                        navController.navigate("${Screen.DETAILSCREENUSER.name}/${event.id}")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF1A237E),
                                    Color(0xFF3F51B5)
                                )
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 0.dp
                    )
                ) {
                    Text(
                        text = "View Details",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigation(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(24.dp)),
        tonalElevation = 16.dp
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    "Home",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF1A237E),
                selectedTextColor = Color(0xFF1A237E),
                indicatorColor = Color(0xFFE8EAF6),
                unselectedIconColor = Color(0xFF6C7B7F),
                unselectedTextColor = Color(0xFF6C7B7F)
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Outlined.QrCode,
                    contentDescription = "QR Attendance",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    "Attendance",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF1A237E),
                selectedTextColor = Color(0xFF1A237E),
                indicatorColor = Color(0xFFE8EAF6),
                unselectedIconColor = Color(0xFF6C7B7F),
                unselectedTextColor = Color(0xFF6C7B7F)
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    "Profile",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF1A237E),
                selectedTextColor = Color(0xFF1A237E),
                indicatorColor = Color(0xFFE8EAF6),
                unselectedIconColor = Color(0xFF6C7B7F),
                unselectedTextColor = Color(0xFF6C7B7F)
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VIITPuneAppPreview() {
    MaterialTheme {
        MainFeedScreen(
            viewModel(),
            rememberNavController()
        )
    }
}




@Composable
fun ClubCard(club: Club,modifier: Modifier= Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Club Logo
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFE3F2FD),
                                Color(0xFFBBDEFB)
                            )
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                AsyncImage(
                    model = club.imageUri,
                    contentDescription = "${club.username} logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Club Details
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = club.username,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    modifier= Modifier.align(Alignment.CenterStart)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Join Button
                Surface(
                    onClick = { /* Handle join club */ },
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF1A237E),
                    modifier= Modifier.align(Alignment.CenterEnd)
                ) {
                    Text(
                        text = "Join",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .padding(horizontal = 25.dp, vertical = 8.dp)

                    )
                }
            }
        }
    }
}