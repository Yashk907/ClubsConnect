package com.example.clubsconnect.ViewModel.userside

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.clubsconnect.Model.Event
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class Club(
    val username : String="",
    val imageUri : String="",
    val email : String="",
    val uid : String = ""
)


class FeedViewModel : ViewModel() {

    private val eventcollection = FirebaseFirestore.getInstance()
        .collection("events")

    private val clubcollection = FirebaseFirestore.getInstance()
        .collection("clubs")

    private val _events  : MutableStateFlow<List<Event>> = MutableStateFlow(emptyList())
    val events: MutableStateFlow<List<Event>> = _events


    private val _clubs : MutableStateFlow<List<Club>> = MutableStateFlow(emptyList())
    val clubs : MutableStateFlow<List<Club>> = _clubs


//    init {
//        fetchclubs()
//        fetchEvents()
//    }

    fun fetchEvents() {
        eventcollection
            .get()
            .addOnSuccessListener { result ->
                val fetchedEvents = result.documents.mapNotNull {
                    it.toObject(Event::class.java)
                }

                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                val now = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis

                this._events.value = fetchedEvents.filter {
                    try {
                        val eventDate = sdf.parse(it.eventDate)?.time ?: 0L
                        eventDate >= now
                    } catch (e: Exception) {
                        false
                    }
                }

                Log.e("FeedViewModel", "Fetched ${_events.value.size} events (filtered from ${fetchedEvents.size})")
            }
            .addOnFailureListener {
                Log.e("FeedViewModel", "Failed to fetch events: ${it.message}")
            }
    }

    fun fetchclubs(){
        clubcollection.get()
            .addOnSuccessListener {
                result->
                val clubs = result.documents.mapNotNull {
                    it.toObject(Club::class.java)
                }
                _clubs.value = clubs
            }
            .addOnFailureListener {
                Log.e("FeedViewModel", "Failed to fetch clubs: ${it.message}")
            }

    }
    fun getEventById(id : String , onResult : (Event?) -> Unit){
        eventcollection.document(id)
            .get()
            .addOnSuccessListener { doc ->
                val event = doc.toObject(Event::class.java)
                onResult(event)
            }
            .addOnFailureListener{
                    doc->
                onResult(null)
            }
    }
}
