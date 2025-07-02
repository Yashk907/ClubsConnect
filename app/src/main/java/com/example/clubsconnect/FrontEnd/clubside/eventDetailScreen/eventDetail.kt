package com.example.clubsconnect.FrontEnd.clubside.eventDetailScreen

import android.content.Intent
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.clubsconnect.Screen
import com.example.clubsconnect.ViewModel.ClubEventDetailViewModel
import com.example.clubsconnect.ViewModel.RegisteredAttendes
import com.example.clubsconnect.ViewModel.Clubside.clubEvent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubSideEventDetailScreen(
    navController: NavController,
    viewModel: ClubEventDetailViewModel,
    onBackClick: () -> Unit = {}
) {
    val event by viewModel.event
    val registeredMembers = viewModel.registeredAttendes
    val attendanceList = viewModel.presentAttendes
    val qrcodebitmap by viewModel.QrCodeBitmap
    val visibleDeleteDialog = remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Event Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Share event */ }) {//writing logic later
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share"
                        )
                    }
                    IconButton(onClick = {navController.navigate("${Screen.CLUBEDITEVENTCLUB.name}/${event.id}")}) {
                        Icon(imageVector = Icons.Default.Edit,
                            contentDescription = "Edit")
                    }
                    IconButton(onClick = {
                        visibleDeleteDialog.value=true
                    },
                        modifier = Modifier.clickable(onClick = onBackClick)
                        ) {
                        Icon(imageVector = Icons.Default.Delete,
                            contentDescription = "Delete")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if(viewModel.state.value.loading){
                CircularProgressIndicator(modifier = Modifier
                    .fillMaxSize()
                    .padding(150.dp))
            }else{
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Event Banner/Image Section
                    item {
                        EventBannerSection(event)
                    }

                    // Event Info Section
                    item {
                        EventInfoSection(event)
                    }

                    // Event Details Section
                    item {
                        EventDetailsSection(event)
                    }

                    // QR Code Section
                    item {
                        if(qrcodebitmap!=null){
                            QRCodeSection(event,viewModel,qrcodebitmap)
                        }else{
                            CircularProgressIndicator(modifier = Modifier
                                .fillMaxSize()
                                .padding(150.dp))
                        }

                    }

                    // Toggle Buttons for Lists
                    item {
                        MemberListToggle(
                            selectedTab = selectedTab,
                            onTabSelected = { selectedTab = it },
                            registeredCount = registeredMembers.size,
                            attendanceCount = attendanceList.size
                        )
                    }

                    // Member Lists
                    val currentList = if (selectedTab == 0) registeredMembers else attendanceList
                    items(currentList) { member ->
                        MemberCard(
                            member = member,
                            showAttendanceStatus = selectedTab == 0
                        )
                    }

                    // Bottom spacing
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            //deleteDialogbox
            if (visibleDeleteDialog.value) {
                // Optional: dim the background slightly
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                )

                BasicAlertDialog(
                    onDismissRequest = { visibleDeleteDialog.value = false }
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.large,
                        tonalElevation = AlertDialogDefaults.TonalElevation,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .wrapContentSize()
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 25.dp)) {
                            Text(text = "Are you sure you want to cancel?")
                            Spacer(modifier = Modifier.height(15.dp))
                            Box(modifier = Modifier.fillMaxWidth()) {
                                TextButton(onClick = {
                                    visibleDeleteDialog.value = false
                                },
                                    modifier = Modifier.align(Alignment.CenterStart)
                                        .padding(start = 0.dp)) {
                                    Text("Cancel")
                                }
                                TextButton(onClick = {
                                    // Confirm delete logic
                                    viewModel.deleteEvent(event.id)
                                    navController.navigate(Screen.CLUBMAINSCREENCLUB.name)
                                },
                                    modifier = Modifier.align(Alignment.CenterEnd)
                                        .padding(end = 30.dp)) {
                                    Text("Confirm")
                                }
                            }
                        }
                    }
                }
            }

        }
        }




}

@Composable
fun EventBannerSection(event: clubEvent) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
//            .height(400.dp)
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Placeholder for event image
            AsyncImage(
                model = event.imageUrl,
                contentDescription = "Event Image",
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
fun EventInfoSection(event: clubEvent) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = event.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = event.type,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoChip(
                    icon = Icons.Default.People,
                    text = "${event.attendes} Attendees",
                    color = MaterialTheme.colorScheme.primary
                )

                InfoChip(
                    icon = Icons.Default.CurrencyRupee,
                    text = event.registrationFee,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

@Composable
fun EventDetailsSection(event: clubEvent) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Event Details",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            DetailRow(
                icon = Icons.Default.DateRange,
                label = "Registration Start Date",
                value = event.startDate
            )
            DetailRow(
                icon = Icons.Default.DateRange,
                label = "Event Date",
                value = event.eventDate
            )

            DetailRow(
                icon = Icons.Default.Schedule,
                label = "Registration End Date",
                value = event.endDate
            )

            DetailRow(
                icon = Icons.Default.LocationOn,
                label = "Location",
                value = event.location
            )

            DetailRow(
                icon = Icons.Default.Business,
                label = "Organized by",
                value = event.clubName
            )

            if (event.registrationLink.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, "https://${event.registrationLink}".toUri())
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Registration Link")
                }
            }
        }
    }
}

@Composable
fun QRCodeSection(event: clubEvent,
                  viewModel: ClubEventDetailViewModel,
                  qrcodebitmap : Bitmap?) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "QR Code for Attendance",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // QR Code Placeholder
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(200.dp)
                    .background(
                        Color.White,
                        RoundedCornerShape(12.dp)
                    )
                    .border(
                        2.dp,
                        MaterialTheme.colorScheme.outline,
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                    Image(
                        bitmap = qrcodebitmap!!.asImageBitmap(),
                        contentDescription = "QR Code",
                        modifier = Modifier.size(190.dp),
                    )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Scan this QR code for event attendance",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if(qrcodebitmap!=null){
                        val file = viewModel.saveQrCodeToFile(context,event, qrcodebitmap)
                        val uri = FileProvider.getUriForFile(context,"${context.packageName}.fileprovider",
                            file)
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type =  "image/png"
                            putExtra(Intent.EXTRA_STREAM,uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }

                        context.startActivity(Intent.createChooser(shareIntent,"Share QR Code"))
                    }else{
                        Toast.makeText(context,"qrcode is empty", Toast.LENGTH_SHORT).show()
                    }

                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Share this QR")
            }

        }
    }
}

@Composable
fun MemberListToggle(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    registeredCount: Int,
    attendanceCount: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            ToggleButton(
                text = "Registered ($registeredCount)",
                isSelected = selectedTab == 0,
                onClick = { onTabSelected(0) },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            ToggleButton(
                text = "Attendance ($attendanceCount)",
                isSelected = selectedTab == 1,
                onClick = { onTabSelected(1) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ToggleButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clickable { onClick() },
        color = if (isSelected)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MemberCard(
    member: RegisteredAttendes,
    showAttendanceStatus: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Picture Placeholder
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = member.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = member.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (member.timestamp!=0L) {
                    val date = Date(member.timestamp)
                    val formattedDate = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(date)
                    Text(
                        text = "Registered: ${formattedDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (showAttendanceStatus) {
                Surface(
                    color = if (member.present == true)
                        Color(0xFF4CAF50)
                    else
                        Color(0xFFF44336),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = if(member.present) "Present" else "Absent" ,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun InfoChip(
    icon: ImageVector,
    text: String,
    color: Color
) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = color
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = color,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


