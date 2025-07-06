package com.example.clubsconnect.ViewModel.Clubside

import android.util.Log
import androidx.compose.ui.semantics.Role
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

data class Student(
    val email : String = "",
    val imageUri : String = "",
    val username: String = "",
    val id : String = "",
)
data class studentWithStatus(
    val student: Student,
    val status : String
)
data class Invitation(
    val invitaionId : String = "",
    val clubId : String = "",
    val clubName : String = "",
    val username: String = "",
    val userId : String = "",
    val role: String = "",
    val status : String = ""
)

class AddMemberViewModel : ViewModel(){
    private val _students : MutableStateFlow<List<studentWithStatus>> = MutableStateFlow(emptyList())
    val students : StateFlow<List<studentWithStatus>> = _students.asStateFlow()

    val isLoading : MutableStateFlow<Boolean> = MutableStateFlow(false)
    private  val clubId = Firebase.auth.currentUser!!.uid

    private val _alreadyMember : MutableStateFlow<List<Student>> = MutableStateFlow(emptyList())
    val alreadyMember = _alreadyMember

    fun loadMembers(onError : (String)-> Unit){
        Firebase.firestore.collection("clubs")
            .document(clubId)
            .collection("members")
            .get()
            .addOnSuccessListener {
               _alreadyMember.value= it.documents.mapNotNull {
                    it.toObject(Student::class.java)
                }
            }
            .addOnFailureListener {
                onError("error at fetching members : ${it.message}")
            }
    }

    fun loadStudents(){
        val studentRef = Firebase.firestore.collection("students")
        val invitationref = Firebase.firestore.collection("invitations")
        isLoading.value=true

        studentRef.get()
            .addOnSuccessListener { result ->
                val students = result.documents.mapNotNull { doc->
                    val student=doc.toObject(Student::class.java)
                student?.copy(id = doc.id)
                }
                Log.d("AddMemberLogs",students.toList().toString())
                if(students.isEmpty()){
                    Log.d("AddMemberLogs","student is empty")
                }
                invitationref.whereEqualTo("clubId",clubId)
                    .get()
                    .addOnSuccessListener {
                       val invitations = it.documents.mapNotNull { it.toObject(Invitation::class.java) }
                        val combinedList = students.map {student->
                            val invitationStatus = invitations.find {student.id==it.userId }?.status?:"none"
                            studentWithStatus(
                                student = student,
                                status = invitationStatus
                            )
                        }

                        _students.value = combinedList
                        isLoading.value=false
                        Log.d("AddMemberLogs",combinedList.toList().toString())
                    }
                    .addOnFailureListener {
                        isLoading.value=false
                        Log.d("AddMemberLogs","invitationfailed ${it.message}")
                    }
            }
            .addOnFailureListener {
                isLoading.value=false
                Log.d("AddMemberLogs","studentsfailed ${it.message}")
            }
        }

    fun onInvite(studentWithStatus: studentWithStatus,
                 role: String,
                 onSuccess : (String)-> Unit){
        val student = studentWithStatus.student
        Firebase.firestore.collection("clubs")
            .document(clubId)
            .get()
            .addOnSuccessListener { result->
              var clubName= result.getString("username")?:""

                val invitationMap = hashMapOf(
                    "clubId" to clubId,
                    "clubName" to clubName,
                    "username" to student.username,
                    "userId" to student.id,
                    "role" to role,
                    "status" to "pending"
                )
                Firebase.firestore.collection("students")
                    .document(studentWithStatus.student.id)
                    .collection("invitations")
                    .add(invitationMap)
                    .addOnSuccessListener {
                        Firebase.firestore.collection("invitations")
                            .add(invitationMap)
                            .addOnSuccessListener {
                                onSuccess("invitation sent successfully!!")
                                _students.value = _students.value.map {
                                    if (it.student.id == student.id) it.copy(status = "pending")
                                    else it
                                }
                            }
                            .addOnFailureListener {
                                onSuccess("Invitation unsuccessful ${it.message}")
                            }
                    }
                    .addOnFailureListener {
                        Log.d("AddMemberLogs","Unable to create entry in students")
                    }
            }.addOnFailureListener {
                Log.d("AddMemberLogs","Unable to fetch name of club")
            }


    }

    fun onCancelInvite(studentWithStatus: studentWithStatus){
        val student = studentWithStatus.student
        Firebase.firestore.collection("invitations")
            .whereEqualTo("clubId",clubId)
            .whereEqualTo("userId",student.id)
            .get()
            .addOnSuccessListener {
                it.documents.forEach {
                    it.reference.delete()
                }

                Firebase.firestore.collection("students")
                    .document(student.id)
                    .collection("invitations")
                    .whereEqualTo("clubId",clubId)
                    .whereEqualTo("userId",student.id)
                    .get()
                    .addOnSuccessListener {
                        it.documents.forEach {
                            it.reference.delete()
                        }
                        _students.value = _students.value.map {
                            if (it.student.id == student.id) it.copy(status = "none")
                            else it
                        }
                    }
                    .addOnFailureListener {
                        Log.d("AddMemberLogs","Unable to cancel invitation student level")
                    }
            }
            .addOnFailureListener {
                Log.d("AddMemberLogs","Unable to cancel invitation invitation level")
            }
    }

    fun onResend(studentWithStatus: studentWithStatus){
        //change status in invitations and add in student/invitations
        Firebase.firestore.collection("students")
            .document(studentWithStatus.student.id)
            .collection("invitations")
            .whereEqualTo("clubId",clubId)
            .whereEqualTo("userId",studentWithStatus.student.id)
            .get()
            .addOnSuccessListener { doc->
                doc.documents.forEach {
                    it.reference.update("status","pending")
                }
                Firebase.firestore.collection("invitations")
                    .whereEqualTo("clubId",clubId)
                    .whereEqualTo("userId",studentWithStatus.student.id)
                    .get()
                    .addOnSuccessListener {
                        it.documents.forEach {
                            it.reference.update("status","pending")
                        }
                        _students.value = _students.value.map {
                            if (it.student.id == studentWithStatus.student.id) it.copy(status = "pending")
                            else it
                        }
                    }
                    .addOnFailureListener {
                        Log.d("AddMemberLogs","Unable to resend invitation")
                    }

            }
            .addOnFailureListener {
                Log.d("AddMemberLogs","Unable to resend invitation")
            }

    }
}
