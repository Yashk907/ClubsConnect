package com.example.clubsconnect.ViewModel.userside

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.clubsconnect.Model.Event
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

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
    fun rsvpToEventIfNotAlready(
        eventId: String,
        userId: String,
        name: String,
        email: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val db = Firebase.firestore
        val userDocRef = db.collection("event_registrations")
            .document(eventId)
            .collection("users")
            .document(userId)

        userDocRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Already registered
                    onResult(false, "You have already RSVPed.")
                } else {
                    // Not registered, add the RSVP
                    val data = mapOf(
                        "name" to name,
                        "email" to email,
                        "timestamp" to System.currentTimeMillis(),
                        "present" to false
                    )
                    userDocRef.set(data)
                        .addOnSuccessListener {
                            onResult(true, null)
                            addEventToMemberRegisteredEvent(eventId,userId,onResult)
                        }
                        .addOnFailureListener {
                            onResult(false, it.message)
                        }
                }
            }
            .addOnFailureListener {
                onResult(false, it.message)
            }
    }

    //for saving event to the student->id->registered_events
    private fun addEventToMemberRegisteredEvent(eventId: String,
                                                userId : String,
                                                onResult: (Boolean, String?) -> Unit){
        Firebase.firestore.collection("events")
            .document(eventId)
            .get()
            .addOnSuccessListener {
                val eventRef= mapOf("eventId" to eventId,
                    "registeredOn" to System.currentTimeMillis())
                Firebase.firestore.collection("students")
                    .document(userId)
                    .collection("registered_events")
                    .document(eventId)
                    .set(eventRef)
                    .addOnFailureListener {
                        onResult(false,it.message)
                    }
            }
            .addOnFailureListener {
                onResult(false,it.message)
            }
    }

}