package com.example.clubsconnect.ViewModel

import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.example.clubsconnect.InternalFun.saveUserToPref
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
               PRNno : String,
               onresult : (Boolean, String?)-> Unit){
        if(!email.endsWith("@viit.ac.in")){
            onresult(false,"UnAuthorised Email ID :  Use official VIIT Email Id")
            return
        }
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if(task.isSuccessful){
                onresult(true,null)
                val uid = task.result?.user?.uid
                saveUserToDataStore(uid,email,role,userName,PRNno,onresult)
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
                                    PRNno : String,
                                    onresult: (Boolean, String?) -> Unit){
        val userId = uid
        if(userId==null){
            //we can add exception handling
            onresult(false,"user Id Not found")
            return
        }
        val userMap = hashMapOf(
            "email" to email,
            "prn" to PRNno,
            "role" to role,
            "username" to userName,


        )

        firestordb.collection("users")
            .document(uid)
            .set(userMap)
            .addOnSuccessListener {
                onresult(true,null)
            }
            .addOnFailureListener {
                onresult(false,"data of user Not stored!!")
            }
    }
}