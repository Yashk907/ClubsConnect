package com.example.clubsconnect.InternalFun

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

internal fun saveUserToPref(context: Context,uid: String, name: String, type: String){
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    prefs.edit().apply {
        putString("uid", uid)
        putString("name", name)
        putString("type", type)
        apply()
    }
}

internal fun getUserInfoFromFireStore( onResult: (Triple<String?, String?, String?>) -> Unit={},
                                       onError: (Exception) -> Unit={}) : Triple<String?,String?,String?>{
    val currentUser = FirebaseAuth.getInstance().currentUser
    val uid = currentUser?.uid
    val name = currentUser?.displayName
    var type = mutableStateOf<String?>("")

    if (uid == null) {
        onResult(Triple(null, null, null))
        return Triple(null,null,null)
    }

    FirebaseFirestore.getInstance()
        .collection("users")
        .document(uid)
        .get()
        .addOnSuccessListener { document ->
             type.value = document.getString("type")
            onResult(Triple(uid, name, type.value))
        }
        .addOnFailureListener { exception ->
            onError(exception)
        }
    return Triple(uid,name,type.value)
}