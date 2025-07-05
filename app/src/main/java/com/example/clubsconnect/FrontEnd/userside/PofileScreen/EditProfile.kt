@file:OptIn(ExperimentalMaterial3Api::class)

package com.clubconnect.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clubsconnect.ViewModel.userside.StudentProfile
import com.example.clubsconnect.ViewModel.userside.UserEditProfileViewmodel
import java.util.*


@Composable
fun  StudentEditProfileScreen(viewmodel: UserEditProfileViewmodel,
                              modifier: Modifier = Modifier ,
                              onbackclick: () -> Unit,) {

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
        }
    ) {
        padding->
        StudentEditProfile(viewmodel, modifier = modifier.padding(padding))
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentEditProfile(
    viewmodel : UserEditProfileViewmodel,
    modifier: Modifier= Modifier
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewmodel.fetchUserProfile {
            Toast.makeText(context,it,Toast.LENGTH_SHORT).show()
        }
    }
    val userProfile = viewmodel.userProfile.collectAsStateWithLifecycle()
    val isLoading = viewmodel.isLoading.value

    var username by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }
    var selectedYear by remember { mutableStateOf("") }
    var selectedDepartment by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(userProfile.value) {
        username = userProfile.value.username
        studentId = userProfile.value.prnno
        selectedYear = userProfile.value.academicyear
        selectedDepartment = userProfile.value.department
        bio = userProfile.value.bio
        profileImageUri = userProfile.value.imageUri.toUri()
    }
    var isUploading by remember { mutableStateOf(false) }

    // Dropdown states
    var yearExpanded by remember { mutableStateOf(false) }
    var departmentExpanded by remember { mutableStateOf(false) }

    val academicYears = listOf("1st Year", "2nd Year", "3rd Year", "4th Year")
    val departments = listOf(
        "Computer Science", "Information Technology", "Electronics Engineering",
        "Mechanical Engineering", "Civil Engineering", "Other"
    )




    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            isUploading = true
            profileImageUri = it
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
        if(isLoading){
            CircularProgressIndicator(modifier= Modifier.fillMaxSize()
                .padding(80.dp))
        }else{
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Profile Picture Section
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
                                    Icons.Default.PhotoCamera,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Profile Picture",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Box(
                                modifier = Modifier.size(120.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (profileImageUri != null) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(profileImageUri)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "Profile picture",
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
                                            Icons.Default.Person,
                                            contentDescription = "Default profile picture",
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
                                Icon(Icons.Default.PhotoCamera, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Change Picture")
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

                // Personal Information Section
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
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Personal Information",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // First Name and Last Name Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = username,
                                    onValueChange = { username = it },
                                    label = { Text("Full Name *") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )

                            }

                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }

                // Academic Information Section
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
                                    Icons.Default.School,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Academic Information",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Student ID
                            OutlinedTextField(
                                value = studentId,
                                onValueChange = { studentId = it },
                                label = { Text("PRN No. *") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                leadingIcon = {
                                    Icon(Icons.Default.Badge, contentDescription = null)
                                }
                            )

                            Spacer(modifier = Modifier.height(12.dp))


                            // Department Dropdown
                            ExposedDropdownMenuBox(
                                expanded = departmentExpanded,
                                onExpandedChange = { departmentExpanded = !departmentExpanded }
                            ) {
                                OutlinedTextField(
                                    value = selectedDepartment,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Department *") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = departmentExpanded)
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Business, contentDescription = null)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor()
                                )

                                ExposedDropdownMenu(
                                    expanded = departmentExpanded,
                                    onDismissRequest = { departmentExpanded = false }
                                ) {
                                    departments.forEach { department ->
                                        DropdownMenuItem(
                                            text = { Text(department) },
                                            onClick = {
                                                selectedDepartment = department
                                                departmentExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Academic Year Dropdown
                            ExposedDropdownMenuBox(
                                expanded = yearExpanded,
                                onExpandedChange = { yearExpanded = !yearExpanded }
                            ) {
                                OutlinedTextField(
                                    value = selectedYear,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Academic Year *") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = yearExpanded)
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
                                    academicYears.forEach { year ->
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

                // Bio Section
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
                                    text = "About Me",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = bio,
                                onValueChange = {
                                    if (it.length <= 300) {
                                        bio = it
                                    }
                                },
                                label = { Text("Tell us about yourself") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 4,
                                maxLines = 6,
                                supportingText = {
                                    Text(
                                        text = "${bio.length}/300 characters",
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                },
                                placeholder = {
                                    Text("Share your interests, hobbies, goals, and what makes you unique...")
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
                                    username=userProfile.value.username
                                    studentId=userProfile.value.prnno
                                    selectedYear=userProfile.value.academicyear
                                    selectedDepartment=userProfile.value.department
                                    bio=userProfile.value.bio
                                    profileImageUri=userProfile.value.imageUri.toUri()
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancel")
                            }

                            Button(
                                onClick = {
                                    if(profileImageUri!=userProfile.value.imageUri.toUri()){
                                        //image is updated
                                        profileImageUri?.let {
                                            val file = viewmodel.uriToFile(context,
                                                it
                                            )
                                            if(file!=null)
                                            viewmodel.saveImageToCloudinary(file){
                                                val profile = StudentProfile(
                                                    username = username,
                                                    prnno = studentId,
                                                    academicyear = selectedYear,
                                                    department = selectedDepartment,
                                                    bio = bio,
                                                    imageUri = it?:""
                                                )
                                                viewmodel.saveChanges(profile){
                                                    Toast.makeText(context,it,Toast.LENGTH_SHORT).show()
                                                }

                                            }
                                        }

                                    }else{
                                        val profile = StudentProfile(
                                            username = username,
                                            prnno = studentId,
                                            academicyear = selectedYear,
                                            department = selectedDepartment,
                                            bio = bio,
                                            imageUri = profileImageUri.toString()
                                        )

                                        viewmodel.saveChanges(profile){
                                            Toast.makeText(context,it,Toast.LENGTH_SHORT).show()
                                        }

                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Save Changes")
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
}

