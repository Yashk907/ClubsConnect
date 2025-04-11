package com.example.clubsconnect.ViewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

class MembersViewModel(private val clubId: String) : ViewModel() {
    private val _members = mutableStateListOf<Member>()
    val members: List<Member> = _members

    private val db = Firebase.firestore

    init {
        fetchMembers()
    }

    fun fetchMembers() {
        db.collection("clubs")
            .document(clubId)
            .collection("members")
            .get()
            .addOnSuccessListener { result ->
                _members.clear()
                _members.addAll(result.map {
                    Member(
                        name = it.getString("name") ?: "",
                        id = it.id
                    )
                })
            }
    }

    fun addMember(name: String, email : String,onSuccess: () -> Unit) {
        val newMember = hashMapOf(
            "name" to name,
            "email" to email,
            "joinedAt" to FieldValue.serverTimestamp()
        )

        db.collection("clubs")
            .document(clubId)
            .collection("members")
            .add(newMember)
            .addOnSuccessListener {
                fetchMembers()
                onSuccess()
            }
    }
}

data class Member(
    val name: String = "",
    val id: String = "" // Firestore doc ID
)

class MembersViewModelFactory(private val clubId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MembersViewModel(clubId) as T
    }
}
