package com.example.clubsconnect.ViewModel.Clubside

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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
import java.net.URI

data class ClubProfile(
    val clubName: String = "",//firebae
    val clubDescription: String = "",//manual
    val memberCount: Int = 0,//firebase
    val eventCount: Int = 0,//firebase
    val establishedYear: String = "",//manual
    val category: String = "",//manual
    val clubImage: String = ""//firebase

)


class ClubProfileViewmodel : ViewModel() {
    private val _clubProfile = MutableStateFlow(ClubProfile())
    val clubProfile = _clubProfile
//
    val isLoading = mutableStateOf(false)

     val clubid = MutableStateFlow(FirebaseAuth.getInstance().currentUser?.uid?:"")

    fun loadId(){
        clubid.value= FirebaseAuth.getInstance().currentUser?.uid?:""
    }

    suspend fun fetchClubInfo(onError: (String) -> Unit) {
        try {
            isLoading.value = true

            val clubDoc = Firebase.firestore.collection("clubs")
                .document(clubid.value).get().await()

            if (!clubDoc.exists()) {
                onError("Club document not found")
                return
            }

            val clubname = clubDoc.getString("username") ?: ""
            val clubdescription = clubDoc.getString("clubdescription") ?: ""
            val establishedyear = clubDoc.getString("establishyear") ?: ""
            val category = clubDoc.getString("category") ?: ""
            val clubimage = clubDoc.getString("imageUri") ?: ""

            _clubProfile.value = _clubProfile.value.copy(
                clubName = clubname,
                clubDescription = clubdescription,
                establishedYear = establishedyear,
                category = category,
                clubImage = clubimage
            )

            val membersSnapshot = Firebase.firestore.collection("clubs")
                .document(clubid.value)
                .collection("members")
                .get()
                .await()

            _clubProfile.value = _clubProfile.value.copy(
                memberCount = membersSnapshot.documents.size
            )

            val eventSnapshot = Firebase.firestore.collection("events")
                .whereEqualTo("clubUid", clubid.value)
                .get()
                .await()

            _clubProfile.value = _clubProfile.value.copy(
                eventCount = eventSnapshot.documents.size
            )
        } catch (e: Exception) {
            onError(e.message ?: "Failed to fetch club info")
        } finally {
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
    fun SaveChanges(clubProfile: ClubProfile,onSuccess : (String)-> Unit){
        isLoading.value = true
        Firebase.firestore.collection("clubs")
            .document(clubid.value)
            .update(mapOf(
                "username" to clubProfile.clubName,
                "clubdescription" to clubProfile.clubDescription,
                "establishyear" to clubProfile.establishedYear,
                "category" to clubProfile.category,
                "imageUri" to clubProfile.clubImage
            ))
            .addOnSuccessListener {
                isLoading.value = false
                viewModelScope.launch {
                    fetchClubInfo(onSuccess)
                }
                onSuccess("Profile Updated")
            }
            .addOnFailureListener {
                isLoading.value = false
                onSuccess(it.message.toString())
            }
    }
}