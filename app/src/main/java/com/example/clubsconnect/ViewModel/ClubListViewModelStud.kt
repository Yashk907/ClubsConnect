package com.example.clubsconnect.ViewModel

import android.util.Log
import android.view.View
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.clubsconnect.Model.Club
import com.example.clubsconnect.Model.Event
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore


class ClubListViewModelStud : ViewModel() {
    private val _clubs = mutableStateListOf<Club>()
    val clublist = _clubs

    init {
       fetchEvents()
    }
    private fun fetchEvents() {
        Firebase.firestore.collection("clubs")
            .get()
            .addOnSuccessListener { result ->
                _clubs.clear()
                for (doc in result) {
                    val club = doc.toObject(Club::class.java)
                    _clubs.add(club)
                }
            }
            .addOnFailureListener {
                Log.e("clublistViewModel", "Failed to fetch clubs: ${it.message}")
            }
    }

}