package com.example.clubsconnect.ViewModel.userside

import androidx.lifecycle.ViewModel
import com.example.clubsconnect.ViewModel.Clubside.ClubMember
import com.example.clubsconnect.ViewModel.Clubside.ClubProfile
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await


class UserSideClubProfileViewModel :  ViewModel(){
    private val _clubProfile = MutableStateFlow(ClubProfile())
    val clubProfile = _clubProfile

    private val _membersList = MutableStateFlow<List<ClubMember>>(emptyList())
    val memberList = _membersList

    val isLoading = MutableStateFlow(false)

    suspend fun fetchClubInfo(clubId: String,onError: (String) -> Unit) {
        try {
            isLoading.value = true

            val clubDoc = Firebase.firestore.collection("clubs").document(clubId).get().await()

            if (!clubDoc.exists()) {
                onError("Club document not found")
                return
            }

            val clubname = clubDoc.getString("username") ?: ""
            val clubdescription = clubDoc.getString("clubdescription") ?: ""
            val establishedyear = clubDoc.getString("establishyear") ?: ""
            val category = clubDoc.getString("category") ?: ""
            val clubimage = clubDoc.getString("imageUri") ?: ""

            _clubProfile.value = _clubProfile.value.copy(
                clubName = clubname,
                clubDescription = clubdescription,
                establishedYear = establishedyear,
                category = category,
                clubImage = clubimage
            )

            val membersSnapshot = Firebase.firestore.collection("clubs")
                .document(clubId)
                .collection("members")
                .get()
                .await()

            val members = membersSnapshot.documents.mapNotNull { it.toObject(ClubMember::class.java) }
            _membersList.value = members

            _clubProfile.value = _clubProfile.value.copy(
                memberCount = membersSnapshot.documents.size
            )

            val eventSnapshot = Firebase.firestore.collection("events")
                .whereEqualTo("clubUid", clubId)
                .get()
                .await()

            _clubProfile.value = _clubProfile.value.copy(
                eventCount = eventSnapshot.documents.size
            )
        } catch (e: Exception) {
            onError(e.message ?: "Failed to fetch club info")
        } finally {
            isLoading.value = false
        }
    }
}