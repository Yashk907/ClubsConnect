package com.example.clubsconnect.ViewModel.Clubside

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ClubMember(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    val avatar: String,
    val joinedAt : String
)

data class UiState(
    val isLoading : Boolean = false,
    val errormessage : String? = null
)
class ManageMembersViewmodel : ViewModel(){
    private val _uistate = MutableStateFlow<UiState>(UiState())
    val uistate : StateFlow<UiState> = _uistate


    private val _club_members = MutableStateFlow<List<ClubMember>>(emptyList())
    val clubMembers: StateFlow<List<ClubMember>> = _club_members

    fun loadMembers(onsuccess : (Boolean)->Unit){
        val clubid = Firebase.auth.currentUser?.uid
        _uistate.value = UiState(isLoading = true)
        viewModelScope.launch {
            Firebase.firestore.collection("clubs")
                .document(clubid.toString())
                .collection("members")
                .get()
                .addOnSuccessListener { result ->
                    val members = result.documents.mapNotNull { it.toObject(ClubMember::class.java) }
                    _club_members.value = members
                    onsuccess(true)
                    _uistate.value = UiState(isLoading = false)
                }
                .addOnFailureListener {
                    _uistate.value = UiState(errormessage = it.message,
                        isLoading = false)
                    onsuccess(false)
                }
        }
    }


}