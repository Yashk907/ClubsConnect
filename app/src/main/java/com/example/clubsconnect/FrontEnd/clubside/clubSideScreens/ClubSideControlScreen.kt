package com.example.clubsconnect.FrontEnd.clubside.clubSideScreens

import AddEventScreen
import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.clubsconnect.FrontEnd.clubside.ClubDashBoard.ClubConnectMainScreen
import com.example.clubsconnect.FrontEnd.clubside.membersScreen.ManageMembersScreenClub
import com.example.clubsconnect.Screen
import com.example.clubsconnect.ViewModel.Clubside.ClubProfile
import com.example.clubsconnect.ViewModel.Clubside.ClubProfileViewmodel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch



data class DrawerMenuItem(
    val icon: ImageVector,
    val title: String,
    val onClick: () -> Unit
)

@SuppressLint("StateFlowValueCalledInComposition")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubSideControlScreen(navController: NavController,
                          viewmodel: ClubProfileViewmodel,
                          modifier: Modifier = Modifier) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val titlelist = listOf("Home","Add Event","Members")
    val context = LocalContext.current
    var backPressedTime by remember { mutableStateOf(0L) }

    // Drawer state
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val clubProfile = viewmodel.clubProfile.collectAsStateWithLifecycle()
    val isLoading = viewmodel.isLoading.value

    LaunchedEffect(viewmodel.clubid.value) {
        if (viewmodel.clubid.value!="") {
            viewmodel.fetchClubInfo {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                Log.d("checkViewmodelClubId",it)
            }
        } else {
            viewmodel.loadId()
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { drawerState.isOpen }
            .collect { isOpen ->
                if (isOpen) {
                    viewmodel.fetchClubInfo { errorMsg ->
                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    // Menu items for the drawer
    val menuItems = listOf(
        DrawerMenuItem(Icons.Default.Edit, "Edit Profile") {
            // Handle edit profile
            navController.navigate(Screen.CLUBEDITPROFILE.name)
        },
        DrawerMenuItem(Icons.Default.Event, "Add Events") {
            // Handle manage events
            selectedTab = 1 // Switch to Add Event tab
        },
        DrawerMenuItem(Icons.Default.Group, "Manage Members") {
            // Handle manage members
            selectedTab = 2 // Switch to Members tab
        },
        DrawerMenuItem(Icons.Default.Settings, "Settings") {
            // Handle settings
        },
        DrawerMenuItem(Icons.Default.Help, "Help & Support") {
            // Handle help
        },
        DrawerMenuItem(Icons.AutoMirrored.Filled.ExitToApp, "Logout") {
            FirebaseAuth.getInstance().signOut()
            navController.navigate(Screen.SPLASHSCREEN.name){
                popUpTo(0){
                    inclusive =true }
                launchSingleTop=true
            }
            // Handle logout
            // You can add actual logout logic here
        }
    )

    BackHandler {
        if (drawerState.isOpen) {
            scope.launch {
                drawerState.close()
            }
        } else {
            val currentTime = System.currentTimeMillis()
            if (currentTime - backPressedTime <= 2000) {
                // Exit app
                (context as? Activity)?.finish()
            } else {
                Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT).show()
                backPressedTime = currentTime
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ClubProfileDrawer(
                isloading = isLoading,
                clubProfile = clubProfile.value,
                menuItems = menuItems,
                onCloseDrawer = {
                    scope.launch {
                        drawerState.close()
                    }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(titlelist[selectedTab])
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                BottomNavigationBar(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }
        ) { paddingValues ->
            when(selectedTab){
                0 -> ClubConnectMainScreen(
                    viewModel = viewModel(),
                    navController = navController,
                    modifier = modifier.padding(paddingValues)
                )
                1 -> AddEventScreen(
                    viewModel(),
                    modifier = Modifier.padding(paddingValues)
                )
                2 -> ManageMembersScreenClub(
                    navController,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
fun ClubProfileDrawer(
    isloading : Boolean,
    clubProfile: ClubProfile,
    menuItems: List<DrawerMenuItem>,
    onCloseDrawer: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(320.dp)
    ) {
        LazyColumn {
            // Profile Header Section
            item {
                ProfileHeader(clubProfile = clubProfile)
            }

            // Stats Section
            item {
                if(isloading){
                    CircularProgressIndicator()
                }
                StatsSection(isloading=isloading,
                    clubProfile = clubProfile)
            }

            // Divider
            item {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            }

            // Menu Items
            items(menuItems){
                DrawerMenuItemRow(
                    menuItem = it,
                    onCloseDrawer = onCloseDrawer
                )
            }
        }
    }
}

@Composable
fun ProfileHeader(clubProfile: ClubProfile) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primaryContainer
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Club Logo/Avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Business,
                    contentDescription = "Club Logo",
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Club Name
            Text(
                text = clubProfile.clubName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Club Description
            Text(
                text = clubProfile.clubDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun StatsSection(isloading: Boolean,
                 clubProfile: ClubProfile) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        if(isloading){
            CircularProgressIndicator()
        }else{
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Club Statistics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatItem(
                        label = "Members",
                        value = clubProfile.memberCount.toString(),
                        icon = Icons.Default.Group
                    )
                    StatItem(
                        label = "Events",
                        value = clubProfile.eventCount.toString(),
                        icon = Icons.Default.Event
                    )
                    StatItem(
                        label = "Since",
                        value = clubProfile.establishedYear,
                        icon = Icons.Default.DateRange
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Category,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Category: ${clubProfile.category}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

    }
}

@Composable
fun StatItem(
    label: String,
    value: String,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun DrawerMenuItemRow(
    menuItem: DrawerMenuItem,
    onCloseDrawer: () -> Unit
) {
    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = menuItem.icon,
                contentDescription = null
            )
        },
        label = {
            Text(
                text = menuItem.title,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        selected = false,
        onClick = {
            menuItem.onClick()
            onCloseDrawer()
        },
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
        )
    )
}

@Composable
fun BottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val items = listOf(
        BottomNavItem("Home", Icons.Outlined.Home, Icons.Filled.Home, 0),
        BottomNavItem("Add Event", Icons.Outlined.AddCircle, Icons.Outlined.AddCircle,1),
        BottomNavItem("Members", Icons.Outlined.Groups, Icons.Filled.Groups, 2)
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if(selectedTab==item.index)item.selecticon else item.nonselecticon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                selected = selectedTab == item.index,
                onClick = { onTabSelected(item.index) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

data class BottomNavItem(
    val title: String,
    val nonselecticon: ImageVector,
    val selecticon : ImageVector,
    val index: Int
)