package com.example.clubsconnect.ViewModel.userside

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException

data class StudentProfile(
    val email : String="",
    val username: String="",
    val prnno: String="",
    val academicyear: String="",
    val department: String="",
    val bio: String="",
    val imageUri: String=""
)
class UserEditProfileViewmodel : ViewModel() {
    private val _userProfile = MutableStateFlow(StudentProfile())
    val userProfile = _userProfile

    val isLoading = mutableStateOf(false)
    private val uid = FirebaseAuth.getInstance().currentUser!!.uid


    fun fetchUserProfile(onerror : (String)->Unit){
        isLoading.value = true
        Firebase.firestore.collection("students")
            .document(uid)
            .addSnapshotListener { snapshot,error ->
                if(error!=null){
                    onerror(error.message.toString())
                    return@addSnapshotListener
                }
                if(snapshot!=null && snapshot.exists()) {
                    val user = snapshot.toObject(StudentProfile::class.java)
                    _userProfile.value = user!!
                    isLoading.value=false
                    Log.d("UserEditProfileViewmodel", "fetchUserProfile: $user")
                }
//                Log.d("UserEditProfileViewmodel", "fetchUserProfile: ${_userProfile.value}")
            }
    }

    fun saveChanges(profile: StudentProfile,onsuccess : (String)-> Unit){
        isLoading.value = true
        Firebase.firestore.collection("students")
            .document(uid)
            .update(mapOf(
                "username" to profile.username,
                "prnno" to profile.prnno,
                "academicyear" to profile.academicyear,
                "department" to profile.department,
                "bio" to profile.bio,
                "imageUri" to profile.imageUri
            ))
            .addOnSuccessListener {
                onsuccess("Profile Updated Successfully")
                isLoading.value = false
            }
            .addOnFailureListener {
                onsuccess(it.message.toString())
                isLoading.value = false
            }
    }

    fun saveImageToCloudinary(file : File,onUploaded : (String?)-> Unit){
        isLoading.value=true
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", file.name, file.asRequestBody("image/*".toMediaTypeOrNull()))
            .addFormDataPart("upload_preset", "clubprofile_image")
            .build()

        val request = Request.Builder()
            .url("https://api.cloudinary.com/v1_1/dzzglagqm/image/upload")
            .post(requestBody)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onUploaded(null)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    try {
                        val json = JSONObject(responseBody)

                        if (json.has("secure_url")) {
                            val url = json.getString("secure_url")
                            onUploaded(url)
                            isLoading.value=false
                        } else {
                            Log.e("Cloudinary", "secure_url not found. Full response: $json")
                            onUploaded(null)
                        }
                    } catch (e: JSONException) {
                        Log.e("Cloudinary", "JSON parsing error: ${e.message}. Response: $responseBody")
                        onUploaded(null)
                    }
                } else {
                    Log.e("Cloudinary", "Upload failed. Code: ${response.code}, Body: $responseBody")
                    onUploaded(null)
                }
            }

        })
    }
    fun uriToFile(context : Context,uri: Uri) : File?{
        val inputstream = context.contentResolver.openInputStream(uri)?: return null
        val file = File.createTempFile("temp_profile_image","jpg",context.cacheDir)
        file.outputStream().use {
                output->
            inputstream.copyTo(output)
        }
        return file
    }
}