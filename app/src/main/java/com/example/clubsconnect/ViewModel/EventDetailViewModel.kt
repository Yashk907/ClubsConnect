package com.example.clubsconnect.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.clubsconnect.Model.Event
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlin.jvm.java

class EventDetailViewModel (eventId : String): ViewModel() {
    var eventState by mutableStateOf<Event?>(null)
        private set

    init {
        Firebase.firestore.collection("events")
            .document(eventId)
            .get()
            .addOnSuccessListener { document ->
                eventState = document.toObject(Event::class.java)
            }
            .addOnFailureListener {
                Log.e("EventDetail", "Error fetching event", it)
            }
    }
}