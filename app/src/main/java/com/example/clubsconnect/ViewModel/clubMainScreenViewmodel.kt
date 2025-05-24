package com.example.clubsconnect.ViewModel

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.clubsconnect.Model.Event
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import kotlin.coroutines.EmptyCoroutineContext.get

data class clubScreenState(
    val isLoading : Boolean = true
)

data class clubState(
    val clubName : String,
    val clubLogo : String?,
)
@RequiresApi(Build.VERSION_CODES.O)
class clubMainScreenViewmodel : ViewModel() {

    private val _state = mutableStateOf(clubScreenState())
    val state : State<clubScreenState> = _state

    private  val _eventsList = mutableStateOf<List<Event>>(emptyList())
    val eventList : State<List<Event>> = _eventsList


    private  val _upcomingEventsList = mutableStateOf<List<Event>>(emptyList())
    val upcomingEventsList : State<List<Event>> = _upcomingEventsList

    private  val _previousEventsList = mutableStateOf<List<Event>>(emptyList())
    val previousEventsList : State<List<Event>> = _previousEventsList

    private val _clubInfo = mutableStateOf<clubState>(clubState("",null))
    val clubInfo : State<clubState> = _clubInfo

    private val clubid = FirebaseAuth.getInstance().currentUser?.uid
    private val db = Firebase.firestore


    @RequiresApi(Build.VERSION_CODES.O)
    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    init {
        fetchClubInfo()
        fetchEvents()
    }

    private fun fetchClubInfo(){
        db.collection("clubs")
            .document(clubid!!)
            .get()
            .addOnSuccessListener { document ->
                _clubInfo.value=_clubInfo.value.copy(
                    clubName = document.getString("username") ?: "",
                    clubLogo = document.getString("imageUri")?: "" )
                Log.d("fetchClubInfo", "Club info fetched successfully ${clubInfo.value.clubLogo}")

            }
            .addOnFailureListener {
                Log.d("fetchClubInfo", "Error fetching club info: ${it.message}")
            }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchEvents(){
        _state.value = clubScreenState(isLoading = true)
        db.collection("events")
            .whereEqualTo("clubUid",clubid)
            .get()
            .addOnSuccessListener { result ->
                _eventsList.value = result.map { document ->
                    Event(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        description = document.getString("description") ?: "",
                        type = document.getString("type") ?: "",
                        location = document.getString("location") ?: "",
                        startDate = document.getString("startDate") ?: "",
                        endDate = document.getString("endDate") ?: "",
                        registrationLink = document.getString("registrationLink") ?: "",
                        registrationFee = document.getString("registrationFee") ?: "",
                        imageUrl = document.getString("imageUrl") ?: ""
                    )
                }
                _upcomingEventsList.value = eventList.value.filter { event ->
                    val eventDate = LocalDate.parse(event.startDate, dateFormatter)
                    eventDate>= LocalDate.now()
                }
                _previousEventsList.value = eventList.value.filter { event ->
                    val eventDate = LocalDate.parse(event.startDate, dateFormatter)
                    eventDate< LocalDate.now()
                }
                _state.value = clubScreenState(isLoading = false)
                Log.d("fetchEvents", eventList.value.toList().toString())
                Log.d("fetchEvents", previousEventsList.value.toList().toString())
                Log.d("fetchEvents", upcomingEventsList.value.toList().toString())
            }
            .addOnFailureListener {
                Log.d("fetchEvents", "Error fetching events: ${it.message}")
            }

    }

}