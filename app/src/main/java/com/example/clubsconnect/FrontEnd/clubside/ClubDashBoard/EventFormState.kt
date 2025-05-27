package com.example.clubsconnect.FrontEnd.clubside.ClubDashBoard

import android.net.Uri

data class EventFormState(
    val name: String = "",
    val description: String = "",
    val type: String = "",
    val location: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val registrationLink: String = "",
//    val contactDetails: String = "",
    val registrationFee: String = "",
    val posterImageUri: Uri? = null,

    // Validation errors
    val nameError: String? = null,
    val descriptionError: String? = null,
    val typeError: String? = null,
    val locationError: String? = null,
    val startDateError: String? = null,
    val endDateError: String? = null
)

internal fun validateForm(state: EventFormState): EventFormState {
    return state.copy(
        nameError = if (state.name.isBlank()) "Event name is required" else null,
        descriptionError = if (state.description.isBlank()) "Description is required" else null,
        typeError = if (state.type.isBlank()) "Event type is required" else null,
        locationError = if (state.location.isBlank()) "Location is required" else null,
        startDateError = if (state.startDate.isBlank()) "Start date is required" else null,
        endDateError = if (state.endDate.isBlank()) "End date is required" else null
    )
}
