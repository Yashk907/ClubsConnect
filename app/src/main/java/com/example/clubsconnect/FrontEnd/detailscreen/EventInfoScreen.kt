package com.example.clubsconnect.FrontEnd.detailscreen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.clubsconnect.ViewModel.EventDetailViewModel
import getUserInfoFromFireStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreen(
//    event: Event,
//    eventId : String,
//    viewModel: FeedViewModel,
    viewModel: EventDetailViewModel,
    onBackPressed: () -> Unit
) {
    val context : Context = LocalContext.current
    val event = viewModel.eventState
    var userId by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event Details") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            if(event!=null){
                // Event Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.LightGray)
                ) {
                    if (event.imageUrl.isNotEmpty()) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                ImageRequest.Builder(LocalContext.current)
                                    .data(data = event.imageUrl)
                                    .build()
                            ),
                            contentDescription = "Event poster",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Placeholder if no image
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFFEEEEEE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No Image Available", color = Color.Gray)
                        }
                    }
                }

                // Event Details
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Event Name
                    Text(
                        text = event.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Club name
                    Text(
                        text = "Organized by: ${event.clubName}",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Event Type Badge
                    if (event.type.isNotEmpty()) {
                        Surface(
                            modifier = Modifier.wrapContentSize(),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = event.type,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Location
                    if (event.location.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(event.location)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Date
                    if (event.startDate.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = "Date",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (event.endDate.isNotEmpty()) {
                                    "${event.startDate} to ${event.endDate}"
                                } else {
                                    event.startDate
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Section Title
                    Text(
                        text = "About Event",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Description
                    Text(
                        text = event.description.ifEmpty { "No description available" },
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Registration Fee
                    if (event.registrationFee.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Registration Fee")
                                Text(
                                    text = event.registrationFee,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }
                }


                Spacer(modifier = Modifier.height(32.dp))

                // Register Button
                Button(
                    onClick = {
                        getUserInfoFromFireStore(
                            onResult = {
                                    (uid,name,role),emaill->
                                userId=uid?:"Missing"
                                username = name?:"Missing"
                                email = emaill?:"Missing"
                                viewModel.rsvpToEventIfNotAlready(eventId = event.id,
                                    userId = userId,
                                    name = username,
                                    email = email
                                ){
                                        success,error->
                                    if(success){
                                        Toast.makeText(context,"You have successfully RSVPed", Toast.LENGTH_SHORT).show()
                                    }else{
                                        Toast.makeText(context,error,Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )
                        },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("RSVP Now", fontSize = 16.sp)
                }
            }else{
                CircularProgressIndicator()
            }
        }
    }
}

// Preview function
//@Preview(showSystemUi = true)
//@Composable
//fun EventDetailsScreenPreview() {
//    val sampleEvent = Event(
//        name = "Nirman 3.0",
//        description = "A hackathon event focused on innovation and technology solutions.",
//        type = "Hackathon",
//        location = "Engineering Building, Room 201",
//        startDate = "April 15, 2025",
//        endDate = "April 17, 2025",
//        registrationFee = "₹500",
//        clubName = "Tech Innovators Club"
//    )
//
//    EventDetailsScreen(
//
//    )
//}