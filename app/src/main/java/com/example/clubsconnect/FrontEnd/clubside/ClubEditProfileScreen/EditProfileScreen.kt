package com.example.clubsconnect.FrontEnd.clubside.ClubEditProfileScreen


import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter

data class ClubEditProfile(
    val clubName: String = "",
    val clubDescription: String = "",
    val contactEmail: String = "",
    val contactPhone: String = "",
    val website: String = "",
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val zipCode: String = "",
    val category: String = "",
    val establishedYear: String = "",
    val meetingTime: String = "",
    val membershipFee: String = "",
    val socialMediaLinks: SocialMediaLinks = SocialMediaLinks(),
    val clubRules: String = "",
    val profileImageUri: Uri? = null,
    val bannerImageUri: Uri? = null
)

data class SocialMediaLinks(
    val facebook: String = "",
    val instagram: String = "",
    val twitter: String = "",
    val linkedin: String = "",
    val discord: String = ""
)

@Preview(showSystemUi = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditClubProfileScreen(
    navController: NavController= rememberNavController(),
    modifier: Modifier = Modifier
) {
    var clubProfile by remember { mutableStateOf(ClubEditProfile()) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Image picker launchers
    val profileImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            clubProfile = clubProfile.copy(profileImageUri = it)
        }
    }

    val bannerImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            clubProfile = clubProfile.copy(bannerImageUri = it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Club Profile",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            isLoading = true
                            // Save profile logic here
                            Toast.makeText(context, "Profile saved successfully!", Toast.LENGTH_SHORT).show()
                            isLoading = false
                            navController.popBackStack()
                        },
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Save",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Banner Image Section
            item {
                BannerImageSection(
                    bannerImageUri = clubProfile.bannerImageUri,
                    onBannerImageClick = { bannerImageLauncher.launch("image/*") }
                )
            }

            // Profile Image Section
            item {
                ProfileImageSection(
                    profileImageUri = clubProfile.profileImageUri,
                    onProfileImageClick = { profileImageLauncher.launch("image/*") }
                )
            }

            // Basic Information Section
            item {
                SectionCard(title = "Basic Information") {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CustomTextField(
                            value = clubProfile.clubName,
                            onValueChange = { clubProfile = clubProfile.copy(clubName = it) },
                            label = "Club Name",
                            icon = Icons.Default.Business,
                            isRequired = true
                        )

                        CustomTextField(
                            value = clubProfile.clubDescription,
                            onValueChange = { clubProfile = clubProfile.copy(clubDescription = it) },
                            label = "Club Description",
                            icon = Icons.Default.Description,
                            maxLines = 3,
                            isRequired = true
                        )

                        CategorySelector(
                            selectedCategory = clubProfile.category,
                            onCategorySelected = { clubProfile = clubProfile.copy(category = it) }
                        )

                        CustomTextField(
                            value = clubProfile.establishedYear,
                            onValueChange = { clubProfile = clubProfile.copy(establishedYear = it) },
                            label = "Established Year",
                            icon = Icons.Default.DateRange,
                            keyboardType = KeyboardType.Number
                        )
                    }
                }
            }

            // Contact Information Section
            item {
                SectionCard(title = "Contact Information") {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CustomTextField(
                            value = clubProfile.contactEmail,
                            onValueChange = { clubProfile = clubProfile.copy(contactEmail = it) },
                            label = "Contact Email",
                            icon = Icons.Default.Email,
                            keyboardType = KeyboardType.Email,
                            isRequired = true
                        )

                        CustomTextField(
                            value = clubProfile.contactPhone,
                            onValueChange = { clubProfile = clubProfile.copy(contactPhone = it) },
                            label = "Contact Phone",
                            icon = Icons.Default.Phone,
                            keyboardType = KeyboardType.Phone
                        )

                        CustomTextField(
                            value = clubProfile.website,
                            onValueChange = { clubProfile = clubProfile.copy(website = it) },
                            label = "Website URL",
                            icon = Icons.Default.Language,
                            keyboardType = KeyboardType.Uri
                        )
                    }
                }
            }

            // Address Section
            item {
                SectionCard(title = "Address") {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CustomTextField(
                            value = clubProfile.address,
                            onValueChange = { clubProfile = clubProfile.copy(address = it) },
                            label = "Street Address",
                            icon = Icons.Default.LocationOn,
                            maxLines = 2
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CustomTextField(
                                value = clubProfile.city,
                                onValueChange = { clubProfile = clubProfile.copy(city = it) },
                                label = "City",
                                icon = Icons.Default.LocationCity,
                                modifier = Modifier.weight(1f)
                            )

                            CustomTextField(
                                value = clubProfile.state,
                                onValueChange = { clubProfile = clubProfile.copy(state = it) },
                                label = "State",
                                icon = Icons.Default.Map,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        CustomTextField(
                            value = clubProfile.zipCode,
                            onValueChange = { clubProfile = clubProfile.copy(zipCode = it) },
                            label = "ZIP Code",
                            icon = Icons.Default.Pin,
                            keyboardType = KeyboardType.Number
                        )
                    }
                }
            }

            // Club Details Section
            item {
                SectionCard(title = "Club Details") {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CustomTextField(
                            value = clubProfile.meetingTime,
                            onValueChange = { clubProfile = clubProfile.copy(meetingTime = it) },
                            label = "Meeting Schedule",
                            icon = Icons.Default.Schedule,
                            placeholder = "e.g., Every Friday 6:00 PM"
                        )

                        CustomTextField(
                            value = clubProfile.membershipFee,
                            onValueChange = { clubProfile = clubProfile.copy(membershipFee = it) },
                            label = "Membership Fee",
                            icon = Icons.Default.AttachMoney,
                            keyboardType = KeyboardType.Number,
                            placeholder = "e.g., $50/year"
                        )

                        CustomTextField(
                            value = clubProfile.clubRules,
                            onValueChange = { clubProfile = clubProfile.copy(clubRules = it) },
                            label = "Club Rules & Guidelines",
                            icon = Icons.Default.Rule,
                            maxLines = 4,
                            placeholder = "Enter club rules and guidelines..."
                        )
                    }
                }
            }

            // Social Media Section
            item {
                SocialMediaSection(
                    socialMediaLinks = clubProfile.socialMediaLinks,
                    onSocialMediaChange = { clubProfile = clubProfile.copy(socialMediaLinks = it) }
                )
            }
        }
    }
}

@Composable
fun BannerImageSection(
    bannerImageUri: Uri?,
    onBannerImageClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onBannerImageClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (bannerImageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(bannerImageUri),
                    contentDescription = "Banner Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Add Banner Image",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Edit overlay
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(32.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Banner",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun ProfileImageSection(
    profileImageUri: Uri?,
    onProfileImageClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clickable { onProfileImageClick() }
        ) {
            if (profileImageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(profileImageUri),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .border(
                            BorderStroke(3.dp, MaterialTheme.colorScheme.primary),
                            CircleShape
                        ),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(
                            BorderStroke(3.dp, MaterialTheme.colorScheme.primary),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Edit button
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(36.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Change Photo",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text,
    isRequired: Boolean = false,
    placeholder: String = ""
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = label)
                if (isRequired) {
                    Text(
                        text = " *",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        placeholder = if (placeholder.isNotEmpty()) {
            { Text(text = placeholder) }
        } else null,
        modifier = modifier.fillMaxWidth(),
        maxLines = maxLines,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelector(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val categories = listOf(
        "Technology", "Sports", "Arts & Culture", "Academic",
        "Social", "Business", "Health & Fitness", "Music",
        "Photography", "Gaming", "Volunteer", "Religious",
        "Environmental", "Other"
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedCategory,
            onValueChange = { },
            readOnly = true,
            label = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Category")
                    Text(
                        text = " *",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Category,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun SocialMediaSection(
    socialMediaLinks: SocialMediaLinks,
    onSocialMediaChange: (SocialMediaLinks) -> Unit
) {
    SectionCard(title = "Social Media Links") {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CustomTextField(
                value = socialMediaLinks.facebook,
                onValueChange = {
                    onSocialMediaChange(socialMediaLinks.copy(facebook = it))
                },
                label = "Facebook",
                icon = Icons.Default.Share,
                placeholder = "https://facebook.com/yourclub"
            )

            CustomTextField(
                value = socialMediaLinks.instagram,
                onValueChange = {
                    onSocialMediaChange(socialMediaLinks.copy(instagram = it))
                },
                label = "Instagram",
                icon = Icons.Default.PhotoCamera,
                placeholder = "https://instagram.com/yourclub"
            )

            CustomTextField(
                value = socialMediaLinks.twitter,
                onValueChange = {
                    onSocialMediaChange(socialMediaLinks.copy(twitter = it))
                },
                label = "Twitter/X",
                icon = Icons.Default.AlternateEmail,
                placeholder = "https://twitter.com/yourclub"
            )

            CustomTextField(
                value = socialMediaLinks.linkedin,
                onValueChange = {
                    onSocialMediaChange(socialMediaLinks.copy(linkedin = it))
                },
                label = "LinkedIn",
                icon = Icons.Default.Work,
                placeholder = "https://linkedin.com/company/yourclub"
            )

            CustomTextField(
                value = socialMediaLinks.discord,
                onValueChange = {
                    onSocialMediaChange(socialMediaLinks.copy(discord = it))
                },
                label = "Discord",
                icon = Icons.Default.Chat,
                placeholder = "https://discord.gg/yourserver"
            )
        }
    }
}