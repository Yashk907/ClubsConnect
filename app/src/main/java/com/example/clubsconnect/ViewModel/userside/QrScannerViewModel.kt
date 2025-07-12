package com.example.clubsconnect.ViewModel.userside


import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.LocationServices
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class QrScannerViewModel : ViewModel() {
    private val user = FirebaseAuth.getInstance()
    private val registrationUsersCollection = Firebase.firestore.collection("event_registrations")

    @androidx.annotation.RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION])
    fun qrcodeActivationCheck(context : Context,qrCodeId: String, onResult: (String) -> Unit){
        Firebase.firestore.collection("qr_codes")
            .document(qrCodeId)
            .get()
            .addOnSuccessListener {
                val valid = it.getBoolean("valid")
                val eventId = it.getString("eventid")
                val clublat = it.getDouble("latitude")
                val clublong = it.getDouble("longitude")
                val range = it.getDouble("range")
                if(valid==true){
                    //checking location now
                    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                        val studentLat = location?.latitude
                        val studentLng = location?.longitude
                        if (studentLat != null && studentLng != null) {
                            if (isWithinRange(studentLat, studentLng, clublat!!, clublong!!,range?.toInt()?:30)) {
                                setAttendance(eventId?:"",onResult)

                            }else{
                                onResult("You are far away from the club event" )
                            }
                        }
                        else{
                            onResult("Location not available")
                        }
                    }
                }else{
                    onResult("QR code is not active")
                }
            }
    }
    fun isWithinRange(studentLat: Double, studentLng: Double, clubLat: Double, clubLng: Double, rangeInMeters: Int = 30): Boolean {
        val result = FloatArray(1)
        Location.distanceBetween(studentLat, studentLng, clubLat, clubLng, result)
        Log.d("range",rangeInMeters.toString())
        return result[0] <= rangeInMeters
    }

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
