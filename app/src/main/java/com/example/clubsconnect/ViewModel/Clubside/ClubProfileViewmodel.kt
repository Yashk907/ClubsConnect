package com.example.clubsconnect.ViewModel.Clubside

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await

data class ClubProfile(
    val clubName: String = "",//firebae
    val clubDescription: String = "Fostering innovation and technology excellence",//manual
    val memberCount: Int = 0,//firebase
    val eventCount: Int = 0,//firebase
    val establishedYear: String = "2025",//manual
    val category: String = "Technology"//manual

)


class ClubProfileViewmodel : ViewModel() {
    private val _clubProfile = MutableStateFlow(ClubProfile())
    val clubProfile = _clubProfile

    val isLoading = mutableStateOf(false)

    private val clubid = FirebaseAuth.getInstance().currentUser!!.uid

    suspend fun fetchClubInfo(onError : (String)-> Unit){
        isLoading.value = true
        Firebase.firestore.collection("clubs")
            .document(clubid)
            .addSnapshotListener {
                snapshot,error->
                if(error!=null || snapshot==null || !snapshot.exists()) {
                    onError(error?.message.toString())
                    return@addSnapshotListener
                }
                val clubname = snapshot.getString("username")?:return@addSnapshotListener
                val clubdescription = snapshot.getString("clubdescription")?:return@addSnapshotListener
                val establishedyear = snapshot.getString("establishyear")?:return@addSnapshotListener
                val category = snapshot.getString("category")?:return@addSnapshotListener

                _clubProfile.value = _clubProfile.value.copy(
                    clubName = clubname,
                    clubDescription = clubdescription,
                    establishedYear = establishedyear,
                    category = category)

            }

        val memberssnapshot = Firebase.firestore.collection("clubs")
            .document(clubid)
            .collection("members")
            .get()
            .await()

        _clubProfile.value=_clubProfile.value.copy(
            memberCount = memberssnapshot.documents.size
        )

        val eventSnapshot = Firebase.firestore.collection("events")
            .whereEqualTo("clubUid",clubid)
            .get()
            .await()

        _clubProfile.value=_clubProfile.value.copy(
            eventCount = eventSnapshot.documents.size
        )
        isLoading.value = false

    }
}