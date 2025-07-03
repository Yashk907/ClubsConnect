@file:OptIn(ExperimentalMaterial3Api::class)

package com.clubconnect.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.clubsconnect.Screen
import com.example.clubsconnect.ViewModel.Clubside.ClubProfile
import com.example.clubsconnect.ViewModel.Clubside.ClubProfileViewmodel
import java.util.*

@Composable
fun ClubEditProfile(viewmodel: ClubProfileViewmodel,
                    onbackclick: ()-> Unit,
                    modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Profile",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onbackclick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
    ) {
        padding->
        EditProfileScreen(viewmodel,modifier=modifier.padding(padding))
    }
}

@Composable
fun EditProfileScreen(
    viewmodel: ClubProfileViewmodel,
    modifier: Modifier= Modifier
) {
    val context = LocalContext.current
    val clubProfile = viewmodel.clubProfile.collectAsStateWithLifecycle()
    var clubName by remember { mutableStateOf(clubProfile.value.clubName) }
    var selectedCategory by remember { mutableStateOf(clubProfile.value.category) }
    var selectedYear by remember { mutableStateOf(clubProfile.value.establishedYear) }
    var clubDescription by remember { mutableStateOf(clubProfile.value.clubDescription) }
    var clubImageUri by remember { mutableStateOf<Uri>(clubProfile.value.clubImage.toUri()) }
    var isUploading by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var yearExpanded by remember { mutableStateOf(false) }

    val categories = listOf(
        "Technology", "Sports", "Arts & Culture", "Business", "Academic",
        "Social Service", "Music", "Photography", "Literature", "Science",
        "Environment", "Health & Wellness", "Gaming", "Adventure", "Other"
    )

    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val years = (currentYear downTo 1900).map { it.toString() }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            isUploading = true
            // Simulate upload delay
            clubImageUri = it
            isUploading = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE3F2FD),
                        Color(0xFFC5CAE9)
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
//            item {
//                Card(
//                    modifier = Modifier.fillMaxWidth(),
//                    colors = CardDefaults.cardColors(containerColor = Color.White),
//                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
//                ) {
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(16.dp),
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text(
//                            text = "Edit Club Profile",
//                            style = MaterialTheme.typography.headlineMedium,
//                            fontWeight = FontWeight.Bold
//                        )
//                        IconButton(onClick = onCancelClick) {
//                            Icon(Icons.Default.Close, contentDescription = "Close")
//                        }
//                    }
//                }
//            }

            // Club Image Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Club Image",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            modifier = Modifier.size(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (clubImageUri != null) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(clubImageUri)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Club image",
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape)
                                        .background(Color.Gray.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Group,
                                        contentDescription = "Default club image",
                                        modifier = Modifier.size(48.dp),
                                        tint = Color.Gray
                                    )
                                }
                            }

                            if (isUploading) {
                                Box(
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.5f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { imagePickerLauncher.launch("image/*") },
                            modifier = Modifier.fillMaxWidth(0.6f)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Change Image")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Recommended: Square image, at least 200x200 pixels",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Club Information Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Group,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Club Information",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Club Name
                        OutlinedTextField(
                            value = clubName,
                            onValueChange = { clubName = it },
                            label = { Text("Club Name *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            leadingIcon = {
                                Icon(Icons.Default.Business, contentDescription = null)
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Category Dropdown
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = selectedCategory,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Category *") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = expanded
                                    )
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Tag, contentDescription = null)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                categories.forEach { category ->
                                    DropdownMenuItem(
                                        text = { Text(category) },
                                        onClick = {
                                            selectedCategory = category
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Establishment Year Dropdown
                        ExposedDropdownMenuBox(
                            expanded = yearExpanded,
                            onExpandedChange = { yearExpanded = !yearExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedYear,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Establishment Year *") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = yearExpanded
                                    )
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.CalendarToday, contentDescription = null)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )

                            ExposedDropdownMenu(
                                expanded = yearExpanded,
                                onDismissRequest = { yearExpanded = false }
                            ) {
                                years.take(50).forEach { year ->
                                    DropdownMenuItem(
                                        text = { Text(year) },
                                        onClick = {
                                            selectedYear = year
                                            yearExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Club Description Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Description,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Club Description",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = clubDescription,
                            onValueChange = {
                                if (it.length <= 500) {
                                    clubDescription = it
                                }
                            },
                            label = { Text("About Your Club *") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 4,
                            maxLines = 6,
                            supportingText = {
                                Text(
                                    text = "${clubDescription.length}/500 characters",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            },
                            placeholder = {
                                Text("Tell us about your club, its mission, activities, and what makes it special...")
                            }
                        )
                    }
                }
            }

            // Action Buttons
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                clubName = viewmodel.clubProfile.value.clubName
                                clubDescription = viewmodel.clubProfile.value.clubDescription
                                clubImageUri = viewmodel.clubProfile.value.clubImage.toUri()
                                selectedCategory = viewmodel.clubProfile.value.category
                                selectedYear = viewmodel.clubProfile.value.establishedYear
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                if(clubImageUri.toString()!=clubProfile.value.clubImage){
                                    val file = viewmodel.uriToFile(context,clubImageUri)
                                    if(file!=null){
                                        viewmodel.saveImageToCloudinary(file){
                                            val profile = ClubProfile(
                                                clubName = clubName,
                                                category = selectedCategory,
                                                establishedYear = selectedYear,
                                                clubDescription =  clubDescription,
                                                clubImage = it?:""
                                            )
                                            viewmodel.SaveChanges(clubProfile=profile) {
                                                Toast.makeText(context,it, Toast.LENGTH_SHORT).show()
                                            }
                                        }

                                    }
                                }else{
                                    val profile = ClubProfile(
                                        clubName = clubName,
                                        category = selectedCategory,
                                        establishedYear = selectedYear,
                                        clubDescription =  clubDescription,
                                        clubImage = clubImageUri.toString()
                                    )
                                    viewmodel.SaveChanges(clubProfile=profile) {
                                        Toast.makeText(context,it, Toast.LENGTH_SHORT).show()
                                    }
                                }

                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            if(viewmodel.isLoading.value){
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier= Modifier
                                    .padding(3.dp))
                            }else{
                                Icon(Icons.Default.Save, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Save Changes")
                            }

                        }
                    }
                }
            }

            // Footer
            item {
                Text(
                    text = "All fields marked with * are required",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}


// Preview
