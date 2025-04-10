package com.example.clubsconnect.Model


data class Event(
    val name: String = "",
    val description: String = "",
    val type: String = "",
    val location: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val registrationLink: String = "",
    val registrationFee: String = "",

    val clubName: String = "",     // fetched from SharedPrefs
    val clubUid: String = "",

    val imageUrl: String = ""      // placeholder for poster image URL
)
