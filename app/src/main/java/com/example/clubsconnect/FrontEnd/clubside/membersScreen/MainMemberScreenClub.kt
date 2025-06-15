package com.example.clubsconnect.FrontEnd.clubside.membersScreen

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.clubsconnect.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageMembersScreenClub(navController: NavController,
                            modifier: Modifier= Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // App Header
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.align(Alignment.CenterHorizontally)
                    .clip(CircleShape)
                    .size(70.dp)
                    .background(color =  Color(0xFF3f5ce7))){
                    Icon(
                        imageVector = Icons.Outlined.People,
                        contentDescription = null,
                        modifier = Modifier.padding(10.dp)
                            .fillMaxSize(),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "ClubConnect",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Manage your club members",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }

        // Manage Members Option
        MenuCard(
            onclick = {navController.navigate(Screen.CLUBMANAGEMEMBERS.name)},
            color = Color(0xFF3f5ce7),
            title = "Manage Members",
            description = "View and edit member roles",
            icon = Icons.Default.Settings,
            memberCount = "4"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Add Members Option
        MenuCard(
            onclick = {navController.navigate(Screen.CLUBADDMEMBERSCREEN.name)},
            color = Color(0xFF24ab66),
            title = "Add Members",
            description = "Send invitations to new users",
            icon = Icons.Default.PersonAdd
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuCard(
    onclick : ()-> Unit,
    color: Color,
    title: String,
    description: String,
    icon: ImageVector,
    memberCount: String? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth()
            .clickable(onClick = onclick),
        colors = CardDefaults.cardColors(
            containerColor = color
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = Color.White
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }

            memberCount?.let {
                Card(
                    colors = CardDefaults.cardColors(
                        Color.White.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = it,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun MainScreenPreview() {
//    MaterialTheme {
//        ManageMembersScreenClub()
//    }
//}
