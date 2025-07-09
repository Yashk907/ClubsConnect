import android.app.DatePickerDialog
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.clubsconnect.FrontEnd.clubside.AddEvent.EventFormState
import com.example.clubsconnect.FrontEnd.clubside.AddEvent.validateForm
import com.example.clubsconnect.Model.Event
import com.example.clubsconnect.ViewModel.Clubside.EditEventViewmodelClub
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import io.reactivex.plugins.RxJavaPlugins.onError
import kotlinx.coroutines.tasks.await
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventScreen(eventId : String,
                    navController: NavController,
                    viewmodel : EditEventViewmodelClub,
                    modifier: Modifier = Modifier) {
    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Edit Event")
                },
                navigationIcon = { IconButton(onClick = {navController.navigateUp()}){
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                }
            )
        }
    ){ padding->
        EditEvent(eventId,navController,viewmodel,modifier.padding(padding))
    }

}
@Composable
fun EditEvent(eventId : String,
                    navController: NavController,
                    viewmodel : EditEventViewmodelClub,
                    modifier: Modifier = Modifier) {
    var event by remember { mutableStateOf<Event?>(null) }

    LaunchedEffect(eventId) {
        try {
            val snapshot = Firebase.firestore.collection("events")
                .document(eventId)
                .get()
                .await()

            event = snapshot.toObject(Event::class.java)
        } catch (e: Exception) {
            Log.e("EditEventScreen", "Error fetching event: ${e.message} / ${eventId}")
        }
    }

    if(event!=null){
        var eventDate by remember { mutableStateOf(event!!.eventDate) }
        var closeDate by remember { mutableStateOf(event!!.endDate) }
        var openDate by remember { mutableStateOf(event!!.startDate) }

        var clubname by remember { mutableStateOf(event!!.clubName) }
        var clubid by remember { mutableStateOf(event!!.clubUid) }

        val context = LocalContext.current
        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Event Date Picker
        val eventDatePickerDialog = remember {
            DatePickerDialog(
                context,
                { _, selectedYear, selectedMonth, selectedDay ->
                    eventDate = "${selectedDay.toString().padStart(2, '0')}/" +
                            "${(selectedMonth + 1).toString().padStart(2, '0')}/$selectedYear"
                },
                year,
                month,
                day
            )
        }
        // registration open date Picker
        val OpenDatePickerDialog = remember {
            DatePickerDialog(
                context,
                { _, selectedYear, selectedMonth, selectedDay ->
                    openDate = "${selectedDay.toString().padStart(2, '0')}/" +
                            "${(selectedMonth + 1).toString().padStart(2, '0')}/$selectedYear"
                },
                year,
                month,
                day
            )
        }


// Close Date Picker
        val closeDatePickerDialog = remember {
            DatePickerDialog(
                context,
                { _, selectedYear, selectedMonth, selectedDay ->
                    closeDate = "${selectedDay.toString().padStart(2, '0')}/" +
                            "${(selectedMonth + 1).toString().padStart(2, '0')}/$selectedYear"
                },
                year,
                month,
                day
            )
        }
        var eventTitle by remember { mutableStateOf(event!!.name) }
        var eventDescription by remember { mutableStateOf(event!!.description) }
        var registrationLink by remember { mutableStateOf(event!!.registrationLink) }
        var selectedTag by remember { mutableStateOf(event!!.type) }
        var location by remember{mutableStateOf(event!!.location)}
        var RegistrationFee by remember{mutableStateOf(event!!.registrationFee)}
        var isLoading by remember { mutableStateOf(false) }

        var cloudinaryImageLink by remember { mutableStateOf(event!!.imageUrl) }


        var formState by remember { mutableStateOf(EventFormState()) }
        val validatedState = validateForm(formState)



        //logic for image upload
        // Image URI
        var posterUri by remember { mutableStateOf<Uri?>(null) }

// Image Picker Launcher
        val imagePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            posterUri = uri
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
                .verticalScroll(rememberScrollState())
        ) {

            // Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Event Poster
                Text(
                    text = "Event Poster",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            BorderStroke(1.dp, Color(0xFFCED4DA)),
                            RoundedCornerShape(12.dp)
                        )
                        .background(Color.White)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (posterUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(posterUri),
                            contentDescription = "Event Poster",
                            contentScale = ContentScale.None,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Upload,
                                contentDescription = "Upload",
                                tint = Color(0xFF4F6A92)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Click here to upload your file",
                                color = Color(0xFF4F6A92),
                                fontSize = 14.sp
                            )
                        }
                    }
                }


                Spacer(modifier = Modifier.height(16.dp))

                // Event title
                Text(
                    text = "Event title",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = eventTitle,
                    onValueChange = { eventTitle = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),

                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        unfocusedBorderColor = Color(0xFFCED4DA)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Event Type/Tag
                Text(
                    text = "Event Type",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Tags Row
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState(),true),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    EventTagChip(
                        tag = "Hackathon",
                        isSelected = selectedTag == "Hackathon",
                        onTagSelected = { selectedTag = "Hackathon" }
                    )

                    EventTagChip(
                        tag = "Seminar",
                        isSelected = selectedTag == "Seminar",
                        onTagSelected = { selectedTag = "Seminar" }
                    )

                    EventTagChip(
                        tag = "Workshop",
                        isSelected = selectedTag == "Workshop",
                        onTagSelected = { selectedTag = "Workshop" }
                    )

                    EventTagChip(
                        tag = "Other",
                        isSelected = selectedTag == "Other",
                        onTagSelected = { selectedTag = "Other" }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Event Description
                Text(
                    text = "Event Description",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = eventDescription,
                    onValueChange = { eventDescription = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        unfocusedBorderColor = Color(0xFFCED4DA)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Event Date
                Text(
                    text = "Registration open date",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = openDate,
                    onValueChange = {}, // Disable manual editing
                    label = {Text("Open Date")},
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { OpenDatePickerDialog.show() }, // Open dialog on click
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        unfocusedBorderColor = Color(0xFFCED4DA)
                    ),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Select Date",
                            modifier = Modifier.clickable { OpenDatePickerDialog.show() }
                        )
                    },
                    readOnly = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Event Date
                Text(
                    text = "Event Date",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = eventDate,
                    onValueChange = {}, // Disable manual editing
                    label = {Text("Event Date")},
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { eventDatePickerDialog.show() }, // Open dialog on click
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        unfocusedBorderColor = Color(0xFFCED4DA)
                    ),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Select Date",
                            modifier = Modifier.clickable { eventDatePickerDialog.show() }
                        )
                    },
                    readOnly = true
                )

                Spacer(modifier = Modifier.height(16.dp))


                // Registration close date
                Text(
                    text = "Registration close date",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))


// Close Date Field
                OutlinedTextField(
                    value = closeDate,
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { closeDatePickerDialog.show() },
                    shape = RoundedCornerShape(12.dp),
                    label = { Text("Close Date") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        unfocusedBorderColor = Color(0xFFCED4DA)
                    ),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Select Close Date",
                            modifier = Modifier.clickable { closeDatePickerDialog.show() }
                        )
                    },
                    readOnly = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Registration Link
                Text(
                    text = "Registration Link",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = registrationLink,
                    onValueChange = { registrationLink = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        unfocusedBorderColor = Color(0xFFCED4DA)
                    ),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = "Registration Link"
                        )
                    },
                    placeholder = { Text("https://...") }
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Location",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        unfocusedBorderColor = Color(0xFFCED4DA)
                    ),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "location"
                        )
                    },
                    placeholder = { Text("location") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Event Date
                Text(
                    text = "Registration Fee",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = RegistrationFee,
                    onValueChange = {RegistrationFee= it}, // Disable manual editing
                    label = {Text("Registration Fee")},
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { eventDatePickerDialog.show() }, // Open dialog on click
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        unfocusedBorderColor = Color(0xFFCED4DA)
                    ),
                )


                Spacer(modifier = Modifier.height(32.dp))

                // Add Event Button
                Button(
                    onClick =  {
                        isLoading =true
                        formState = EventFormState(
                            name = eventTitle,
                            description = eventDescription,
                            type = selectedTag,
                            location = location,
                            eventDate= eventDate,
                            startDate = openDate,
                            endDate = closeDate,
                            registrationLink = registrationLink,
                            registrationFee = RegistrationFee,
                            posterImageUri = posterUri
                        )

                        val updatedState = validateForm(formState)
                        if (listOf(
                                updatedState.nameError,
                                updatedState.descriptionError,
                                updatedState.typeError,
                                updatedState.locationError,
                                updatedState.startDateError,
                                updatedState.endDateError
                            ).all { it == null }
                        ) {
                            getUserInfoFromFireStore(onResult = {
                                    (uid,name,type),email ->
                                clubname = name?:"MISSING"
                                clubid = uid?:"MISSING"

                                if(posterUri!=null){
                                    val file = viewmodel.uriToFile(context, formState.posterImageUri!!)
                                    if (file != null) {
                                        viewmodel.uploadToCloudinary(file, context) { cloudinaryImageUrl ->
                                            if (cloudinaryImageUrl != null) {
                                                // Proceed after image is uploaded
                                                val event1 = Event(
                                                    name = formState.name,
                                                    description = formState.description,
                                                    type = formState.type,
                                                    location = formState.location,
                                                    eventDate = formState.eventDate,
                                                    startDate = formState.startDate,
                                                    endDate = formState.endDate,
                                                    registrationLink = formState.registrationLink,
                                                    registrationFee = formState.registrationFee,
                                                    clubName = clubname?:"Unknown",
                                                    clubUid = clubid?:"Missing",
                                                    imageUrl = cloudinaryImageUrl
                                                )

                                                viewmodel.updateEvent(event1,event!!.id) { success, error ->
                                                    if (success) {
                                                        Toast.makeText(context, "Event uploaded successfully", Toast.LENGTH_SHORT).show()

                                                        isLoading=false
                                                    } else {
                                                        isLoading=false
                                                        Toast.makeText(context, error ?: "Upload failed", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            } else {
                                                isLoading=false
                                                Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } else {
                                        isLoading=false
                                        Toast.makeText(context, "Invalid image file", Toast.LENGTH_SHORT).show()
                                    }
                                }else{
                                    val event1 = Event(
                                        name = formState.name,
                                        description = formState.description,
                                        type = formState.type,
                                        location = formState.location,
                                        eventDate = formState.eventDate,
                                        startDate = formState.startDate,
                                        endDate = formState.endDate,
                                        registrationLink = formState.registrationLink,
                                        registrationFee = formState.registrationFee,
                                        clubName = clubname?:"Unknown",
                                        clubUid = clubid?:"Missing",
                                        imageUrl = cloudinaryImageLink
                                    )
                                    viewmodel.updateEvent(event1,event!!.id) { success, error ->
                                        if (success) {
                                            Toast.makeText(context, "Event uploaded successfully", Toast.LENGTH_SHORT).show()
                                            navController.navigateUp()

                                        } else {
                                            isLoading=false
                                            Toast.makeText(context, error ?: "Upload failed", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }

                            },onError = {
                                isLoading=false
                                Toast.makeText(context,"Failed to get club details ", Toast.LENGTH_SHORT).show()
                            })
                        } else {
                            // Show validation errors
                            formState = updatedState
                            Toast.makeText(context, "Fill all required fields", Toast.LENGTH_SHORT).show()
                            isLoading=false
                        }



                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0A2647)
                    )
                ) {
                    if(isLoading){
                        CircularProgressIndicator()
                    }else{
                        Text(
                            text = "Update Event",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                }

                Spacer(modifier = Modifier.height(32.dp))

            }
        }

    }else{ CircularProgressIndicator()

    }
}


