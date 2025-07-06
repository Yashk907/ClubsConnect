package com.example.clubsconnect.ViewModel

import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AuthViewModel : ViewModel() {
    private val auth  = FirebaseAuth.getInstance()
    private val firestordb = Firebase.firestore

    fun SignIn(email: String,password: String,onresult: (Boolean, String?) -> Unit){
        if(!email.endsWith("@viit.ac.in")){
            onresult(false,"UnAuthorised Email ID :  Use official VIIT Email Id")
            return
        }

        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if(task.isSuccessful){
                onresult(true,null)
            }else{
                onresult(false,task.exception?.message)
            }
        }
    }
    fun SignUp(email : String,
               password : String ,
               role : String,
               userName : String,
//               PRNno : String,
               onresult : (Boolean, String?)-> Unit){
        if(!email.endsWith("@viit.ac.in")){
            onresult(false,"UnAuthorised Email ID :  Use official VIIT Email Id")
            return
        }
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if(task.isSuccessful){
                onresult(true,null)
                val uid = task.result?.user?.uid
                saveUserToDataStore(uid,email,role,userName,onresult)
                //here we have to add role in firestore
            }else{
                onresult(false,task.exception?.message)
            }
        }

    }

    private fun saveUserToDataStore(uid : String?,
                                    email : String,
                                    role : String,
                                    userName : String,
//                                    PRNno : String,
                                    onresult: (Boolean, String?) -> Unit){
        val userId = uid
        if(userId==null){
            //we can add exception handling
            onresult(false,"user Id Not found")
            return
        }
        val userMap = hashMapOf(
            "email" to email,
//            "prn" to PRNno,
            "role" to role,
            "username" to userName,

        )

        firestordb.collection("users")
            .document(uid)
            .set(userMap)
            .addOnSuccessListener {
                onresult(true,null)
                saveAccordingToRole(
                    uid = uid,
                    email=email,
                    role = role,
                    userName=userName)
                Log.d("Firestore", "Saving user to /users/$uid")

            }
            .addOnFailureListener {
                onresult(false,"data of user Not stored!!")
            }
    }
    private fun saveAccordingToRole(
        uid : String,
        email : String,
        role : String,
        userName : String,
    ){
        val imageUri = "https://res.cloudinary.com/dzzglagqm/image/upload/v1744380093/307ce493-b254-4b2d-8ba4-d12c080d6651_cg9rg8.jpg"
        val userMap = hashMapOf(
            "email" to email,
            "imageUri" to imageUri,
            "username" to userName,
            "uid" to uid,
        )
        if(role=="Student"){
            userMap["prnno"]="0"
            userMap["department"]="Computer Science"
            userMap["academicyear"]="4th year"
            userMap["bio"]="Student of VIIT"
            firestordb.collection("students")
                .document(uid)
                .set(userMap)
        }else{
            userMap["clubdescription"]="Fostering innovation and technology excellence"
            userMap["establishyear"]="2025"
            userMap["category"]="Technology"
            firestordb.collection("clubs")
                .document(uid)
                .set(userMap)
        }

    }


}