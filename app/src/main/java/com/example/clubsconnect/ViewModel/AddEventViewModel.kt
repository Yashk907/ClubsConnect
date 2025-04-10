package com.example.clubsconnect.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clubsconnect.Model.Event
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher

class AddEventViewModel : ViewModel() {
    fun uploadEvent(event: Event, onsuccess:(Boolean, String?)-> Unit){
        val eventMap = hashMapOf(
            "name" to event.name,
            "description" to event.description,
            "type" to event.type,
            "location" to event.location,
            "startDate" to event.startDate,
            "endDate" to event.endDate,
            "registrationLink" to event.registrationLink,
            "registrationFee" to event.registrationFee,
            "clubName" to event.clubName,
            "clubUid" to event.clubUid,
            "imageUrl" to event.imageUrl,
            "timestamp" to System.currentTimeMillis() // Optional for sorting
        )
        viewModelScope.launch(Dispatchers.IO){
            Firebase.firestore.collection("events")
                .document()
                .set(eventMap)
                .addOnSuccessListener { task ->
                    onsuccess(true,null)
                }
                .addOnFailureListener { task ->
                    onsuccess(false,task.message)
                }
        }
    }
}