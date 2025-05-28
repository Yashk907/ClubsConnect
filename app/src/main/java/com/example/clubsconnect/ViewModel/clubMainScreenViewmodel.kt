package com.example.clubsconnect.ViewModel

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clubsconnect.Model.Event
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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

data class clubEvent(
    val id : String = "",
    val name: String = "",
    val description: String = "",
    val type: String = "",
    val location: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val registrationLink: String = "",
    val registrationFee: String = "",
    val attendes : Int = 0,
    val clubName: String = "",     // fetched from SharedPrefs
    val clubUid: String = "",

    val imageUrl: String = "" ,     // placeholder for poster image URL

)

@RequiresApi(Build.VERSION_CODES.O)
class clubMainScreenViewmodel : ViewModel() {

    private val _state = mutableStateOf(clubScreenState())
    val state : State<clubScreenState> = _state

    private  val _eventsList = mutableStateOf<List<clubEvent>>(emptyList())
    val eventList : State<List<clubEvent>> = _eventsList


    private  val _upcomingEventsList = mutableStateOf<List<clubEvent>>(emptyList())
    val upcomingEventsList : State<List<clubEvent>> = _upcomingEventsList

    private  val _previousEventsList = mutableStateOf<List<clubEvent>>(emptyList())
    val previousEventsList : State<List<clubEvent>> = _previousEventsList

    private val _clubInfo = mutableStateOf<clubState>(clubState("",null))
    val clubInfo : State<clubState> = _clubInfo

    private val clubid = FirebaseAuth.getInstance().currentUser?.uid
    private val db = Firebase.firestore


    @RequiresApi(Build.VERSION_CODES.O)
    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    init {
        fetchClubInfo()
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
    suspend fun fetchEvents(){
        _state.value = clubScreenState(isLoading = true)

        try {
            val eventDoc =   db.collection("events")
                .whereEqualTo("clubUid",clubid)
                .get()
                .await()


           val events= eventDoc.map { document->
                val id = document.id
                val attendes = fetchattendes(id)
               clubEvent(
                   id = document.id,
                   name = document.getString("name") ?: "",
                   description = document.getString("description") ?: "",
                   type = document.getString("type") ?: "",
                   location = document.getString("location") ?: "",
                   startDate = document.getString("startDate") ?: "",
                   endDate = document.getString("endDate") ?: "",
                   registrationLink = document.getString("registrationLink") ?: "",
                   registrationFee = document.getString("registrationFee") ?: "",
                   imageUrl = document.getString("imageUrl") ?: "",
                   attendes = attendes
               )

            }

            _eventsList.value=events
            _upcomingEventsList.value = events.filter { event ->
                val eventDate = LocalDate.parse(event.startDate, dateFormatter)
                eventDate>= LocalDate.now()
            }
            _previousEventsList.value= events.filter { event ->
                val eventDate = LocalDate.parse(event.startDate, dateFormatter)
                eventDate< LocalDate.now()
            }
            _state.value = clubScreenState(isLoading = false)
        }
        catch (e : Exception){
            Log.d("fetchEvents", "Error fetching events: ${e.message}")
                _state.value = clubScreenState(isLoading = false)
        }
    }

    suspend fun fetchattendes(id : String) : Int{
        return try {
            val result = db.collection("event_registrations")
                .document(id)
                .collection("users")
                .get()
                .await()
            result.size()
        }catch (e : Exception){
            Log.d("fetchattendes", "Error fetching attendes: ${e.message}")
            0
        }

    }

}