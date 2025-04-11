package com.example.clubsconnect.ViewModel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.clubsconnect.Model.Event
import com.google.firebase.firestore.FirebaseFirestore

class FeedViewModel : ViewModel() {

    private val _events = mutableStateListOf<Event>()
    val events: List<Event> = _events

    init {
        fetchEvents()
    }

    private fun fetchEvents() {
        FirebaseFirestore.getInstance()
            .collection("events")
            .get()
            .addOnSuccessListener { result ->
                _events.clear()
                for (doc in result) {
                    val event = doc.toObject(Event::class.java)
                    _events.add(event)
                }
            }
            .addOnFailureListener {
                Log.e("FeedViewModel", "Failed to fetch events: ${it.message}")
            }
    }
}
