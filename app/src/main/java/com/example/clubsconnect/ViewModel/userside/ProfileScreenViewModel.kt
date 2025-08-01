package com.example.clubsconnect.ViewModel.userside

import android.util.Log
import android.view.View
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clubsconnect.ViewModel.Clubside.Invitation
import com.example.clubsconnect.ViewModel.Clubside.Student
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar

data class JoinedClubs(
    val clubname : String ="",
    val clubid : String = "",
    val role : String = "",
    val joinedAt : Long = 0
)

data class RegisteredEvent(
    val eventName : String = "",
    val clubName : String = "",
    val status : String = "",
    val date : String = "",
    val registeredOn : Long= 0
)

data class EventSmallInfo(
    val eventId : String = "",
    val registeredOn : Long = 0
)
class ProfileScreenViewModel : ViewModel() {
    private val _student = MutableStateFlow<Student?>(null)
    val student = _student

    private val _invitations = MutableStateFlow<List<Invitation>>(emptyList())
    val invitations = _invitations

    private val _joinedClub = MutableStateFlow<List<JoinedClubs>>(emptyList())
    val joinedClubs = _joinedClub

    private val _registeredEvent = MutableStateFlow<List<RegisteredEvent>>(emptyList())
    val registeredEvent = _registeredEvent

    val isLoading = MutableStateFlow(false)
    private val studentID = FirebaseAuth.getInstance().currentUser!!.uid
    private val email = FirebaseAuth.getInstance().currentUser!!.email

    fun fetchStudentInfo(onError : (String)-> Unit){
        isLoading.value=true
        Firebase.firestore.collection("students").document(studentID).get()
            .addOnSuccessListener {
                val student = it.toObject(Student::class.java)
                _student.value = student
                Firebase.firestore.collection("students").document(studentID).collection("invitations").get()
                    .addOnSuccessListener {
                        val invitations = it.documents.mapNotNull { it.toObject(Invitation::class.java) }
                        _invitations.value = invitations
                        fetchJoinedClubs(onError)
                    }
                    .addOnFailureListener {
                        onError("Error fetching invitations ${it.message}")
                        Log.d("ProfileScreenViewModel","Error fetching invitations ${it.message}")
                    }
            }
            .addOnFailureListener {
                onError("Error fetching student info ${it.message}")
                Log.d("ProfileScreenViewModel","Error fetching student info ${it.message}")
            }



    }

    fun fetchJoinedClubs(onError: (String) -> Unit){
        Firebase.firestore.collection("students").document(studentID).collection("JoinedClubs").get()
            .addOnSuccessListener {
                val joinedClubs = it.documents.mapNotNull { it.toObject(JoinedClubs::class.java) }
                _joinedClub.value = joinedClubs
                fetchEventRegistered(onError)
            }
            .addOnFailureListener {
                onError("Error fetching joined clubs ${it.message}")
                isLoading.value=false
                Log.d("ProfileScreenViewModel","Error fetching joined clubs ${it.message}")
            }
    }
    fun fetchEventRegistered(onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val eventRefs = Firebase.firestore
                    .collection("students")
                    .document(studentID)
                    .collection("registered_events")
                    .get()
                    .await()

                val eventList = eventRefs.documents.mapNotNull {
                    it.toObject(EventSmallInfo::class.java)
                }

                val sdf = SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                val fetchedEvents = mutableListOf<RegisteredEvent>()

                for (event in eventList) {
                    val eventSnapshot = Firebase.firestore
                        .collection("events")
                        .document(event.eventId)
                        .get()
                        .await()

                    val eventName = eventSnapshot.getString("name") ?: continue
                    val clubName = eventSnapshot.getString("clubName") ?: "Unknown"
                    val date = eventSnapshot.getString("eventDate") ?: continue
                    val registeredOn = event.registeredOn

                    val today = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.time

                    val eventDate = sdf.parse(date) ?: today

                    val status = when {
                        eventDate.after(today) -> "upcoming"
                        eventDate.before(today) -> "completed"
                        else -> "ongoing"
                    }


                    fetchedEvents.add(
                        RegisteredEvent(
                            eventName = eventName,
                            clubName = clubName,
                            status = status,
                            date = date,
                            registeredOn = registeredOn
                        )
                    )
                }
                _registeredEvent.value = fetchedEvents
                isLoading.value=false

            } catch (e: Exception) {
                isLoading.value=false
                onError("Error fetching registered events: ${e.message}")
            }
        }
    }

    fun onClubRequestAgreed(invitation: Invitation,onError: (String) -> Unit){
        var avatar=""
        viewModelScope.launch {
            Firebase.firestore.collection("students")
                .document(studentID)
                .get()
                .addOnSuccessListener {
                    avatar = it.get("imageUri").toString()
                    val member = hashMapOf(
                        "id" to invitation.userId,
                        "name" to invitation.username,
                        "email" to email,
                        "role" to invitation.role,
                        "avatar" to avatar,
                         "joinedAt" to System.currentTimeMillis()
                    )
                    Firebase.firestore.collection("clubs")
                        .document(invitation.clubId)
                        .collection("members")
                        .document(invitation.userId)
                        .set(member)
                        .addOnSuccessListener {
                            val clubJoined = hashMapOf(
                                "clubname" to invitation.clubName,
                                "clubid" to invitation.clubId,
                                "role" to invitation.role,
                                "joinedAt" to member["joinedAt"]
                            )
                            Firebase.firestore.collection("students")
                                .document(studentID)
                                .collection("JoinedClubs")
                                .document(invitation.clubId)
                                .set(clubJoined)
                                .addOnSuccessListener {
                                    deleteInvitation(invitation, onError)
                                }
                        }
                        .addOnFailureListener {
                            onError("Error adding member ${it.message}")
                        }
                        .addOnFailureListener {
                            onError("Error adding member ${it.message}")
                        }

                }
                }

    }

    fun onRejectRequest(invitation: Invitation,onError: (String) -> Unit){
        Firebase.firestore.collection("students")
            .document(studentID)
            .collection("invitations")
            .whereEqualTo("clubId",invitation.clubId)
            .whereEqualTo("userId",invitation.userId)
            .get()
            .addOnSuccessListener {
                querySnapshot ->
                querySnapshot.documents.forEach {
                    it.reference.delete()
                }
                Firebase.firestore.collection("invitations")
                    .whereEqualTo("clubId",invitation.clubId)
                    .whereEqualTo("userId",invitation.userId)
                    .get()
                    .addOnSuccessListener {
                        it.documents.forEach {
                            it.reference.update("status","declined")
                        }
                        fetchStudentInfo(onError)
                    }
                    .addOnFailureListener {
                        onError("Error deleting invitation ${it.message}")
                        Log.d("ProfileScreenViewModel","Error deleting invitation ${it.message}")
                    }

            }
            .addOnFailureListener {
                onError("Error deleting invitation ${it.message}")
                Log.d("ProfileScreenViewModel","Error deleting invitation ${it.message}")
            }
    }
    fun deleteInvitation(invitation: Invitation,onError: (String) -> Unit){
        Firebase.firestore.collection("students")
            .document(studentID)
            .collection("invitations")
            .whereEqualTo("clubId",invitation.clubId)
            .whereEqualTo("userId",invitation.userId)
            .get()
            .addOnSuccessListener {
                    querySnapshot ->
                querySnapshot.documents.forEach {
                    it.reference.delete()
                }
                Firebase.firestore.collection("invitations")
                    .whereEqualTo("clubId",invitation.clubId)
                    .whereEqualTo("userId",invitation.userId)
                    .get()
                    .addOnSuccessListener {
                        querySnapshot ->
                        querySnapshot.documents.forEach {
                            it.reference.delete()
                        }
                        fetchStudentInfo(onError)
                    }
                    .addOnFailureListener {
                        onError("Error deleting invitation ${it.message}")
                        Log.d("ProfileScreenViewModel","Error deleting invitation ${it.message}")
                    }

            }
            .addOnFailureListener {
                onError("Error deleting invitation ${it.message}")
                Log.d("ProfileScreenViewModel","Error deleting invitation ${it.message}")
            }
    }
}