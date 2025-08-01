package com.example.clubsconnect.FrontEnd.userside.userscreencontrol

import CameraPreviewScreen
import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.clubsconnect.FrontEnd.userside.FeedPage.MainFeedScreen
import com.example.clubsconnect.FrontEnd.userside.PofileScreen.ProfileScreen
import kotlin.compareTo
@Composable
fun UserScreenControl(navController: NavController, modifier: Modifier = Modifier) {
    var selectedTab by remember { mutableStateOf(0) }
    val context = LocalContext.current
    var backPressedTime by remember { mutableStateOf(0L) }

    BackHandler {
        val currentTime = System.currentTimeMillis()
        if (currentTime - backPressedTime <= 2000) {
            (context as? Activity)?.finish()
        } else {
            Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT).show()
            backPressedTime = currentTime
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = { TopAppBar1() },
            containerColor = Color.Transparent,
        ) { padding ->
            val contentPadding = PaddingValues(
                start = padding.calculateStartPadding(LayoutDirection.Ltr),
                top = padding.calculateTopPadding(),
                end = padding.calculateEndPadding(LayoutDirection.Ltr),
                bottom = padding.calculateBottomPadding() + 0.dp
            )
            when (selectedTab) {
                0 -> MainFeedScreen(viewModel = viewModel(), navController = navController, modifier = Modifier.padding(contentPadding))
                1 -> CameraPreviewScreen(modifier = Modifier.padding(contentPadding))
                2 -> ProfileScreen(viewModel(), navController, modifier = Modifier.padding(contentPadding))
            }
        }

        // Floating Bottom Nav
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 10.dp), // how high above screen bottom
            contentAlignment = Alignment.BottomCenter
        ) {
            BottomNavigation(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                modifier = Modifier
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color.White)
                    .size(width = 320.dp, height = 64.dp)
            )
        }
    }
}



@Composable
fun BottomNavigation(selectedTab: Int, onTabSelected: (Int) -> Unit,modifier: Modifier= Modifier) {
    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(24.dp)),
        tonalElevation = 80.dp
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar1() {

    CenterAlignedTopAppBar(
        title = {
            Column {
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

        }
    )

//    CenterAlignedTopAppBar(
//        title = {
//            Column(
//                horizontalAlignment = Alignment.CenterHorizontally
//
//            ) {
//                Text(
//                    text = "VIIT Pune",
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 20.sp,
//                    color = Color(0xFF1A237E)
//                )
//                Text(
//                    text = "ClubConnect",
//                    fontSize = 12.sp,
//                    color = Color(0xFF6C7B7F),
//                    fontWeight = FontWeight.Medium
//                )
//            }
//
//        },
//        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
//
//    )
}
