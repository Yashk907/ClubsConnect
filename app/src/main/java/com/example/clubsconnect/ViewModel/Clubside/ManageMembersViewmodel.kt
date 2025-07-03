package com.example.clubsconnect.ViewModel.Clubside

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ClubMember(
    val id: String="",
    val name: String="",
    val email: String="",
    val role: String="",
    val avatar: String="",
    val joinedAt : Long =0
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

    @SuppressLint("SuspiciousIndentation")
    fun loadMembers(onsuccess : (Boolean)->Unit){
        val clubid = Firebase.auth.currentUser?.uid
        _uistate.value = UiState(isLoading = true)
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

    fun deleteMember(memberid : String,onsuccess: (Boolean) -> Unit){
        val clubid = FirebaseAuth.getInstance().currentUser!!.uid
        Firebase.firestore.collection("clubs")
            .document(clubid)
            .collection("members")
            .document(memberid)
            .delete()
            .addOnSuccessListener {
                loadMembers(onsuccess)
                onsuccess(true)
            }
            .addOnFailureListener {
                onsuccess(false)
            }
    }


}