package com.example.clubsconnect.ViewModel.userside


import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class QrScannerViewModel : ViewModel() {
    private val user = FirebaseAuth.getInstance()
    private val registrationUsersCollection = Firebase.firestore.collection("event_registrations")

    fun setAttendance(eventId: String, onResult: (String) -> Unit) {
        val userId = user.currentUser?.uid
        if (userId == null) {
            onResult("User not authenticated")
            return
        }

        val userDocRef = registrationUsersCollection
            .document(eventId)
            .collection("users")
            .document(userId)

        userDocRef.get()
            .addOnSuccessListener { student ->
                if (student.exists()) {
                    val alreadyPresent = student.getBoolean("present") == true
                    if (alreadyPresent) {
                        onResult("You have already marked your attendance")
                    } else {
                        userDocRef.update("present", true)
                            .addOnSuccessListener {
                                onResult("Attendance marked as present")
                            }
                            .addOnFailureListener {
                                onResult("Failed to mark attendance as present")
                            }
                    }
                } else {
                    onResult("You have not registered for this event")
                }
            }
            .addOnFailureListener {
                onResult("Failed to check attendance status")
            }
    }

}
