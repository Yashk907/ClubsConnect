package com.example.clubsconnect.ViewModel.userside

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.clubsconnect.Model.Club
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