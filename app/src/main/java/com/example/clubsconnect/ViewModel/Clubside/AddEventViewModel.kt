package com.example.clubsconnect.ViewModel.Clubside

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clubsconnect.Model.Event
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
import java.util.UUID

class AddEventViewModel : ViewModel() {


    fun uploadEvent(event: Event, onsuccess:(Boolean, String?)-> Unit){
        val qrCodeId = UUID.randomUUID().toString()
        viewModelScope.launch(Dispatchers.IO){
            val docref = Firebase.firestore.collection("events")
                .document()
            val eventWithID = event.copy(id=docref.id)
            Log.d("id_check",docref.id)

            //mapping
            val eventMap = hashMapOf(
                "id" to eventWithID.id,
                "name" to event.name,
                "description" to event.description,
                "type" to event.type,
                "location" to event.location,
                "startDate" to event.startDate,
                "eventDate" to event.eventDate,
                "endDate" to event.endDate,
                "registrationLink" to event.registrationLink,
                "registrationFee" to event.registrationFee,
                "clubName" to event.clubName,
                "clubUid" to event.clubUid,
                "imageUrl" to event.imageUrl,
                "qrcodeid" to qrCodeId,
                "timestamp" to System.currentTimeMillis() // Optional for sorting
            )

            docref.set(eventMap)
                .addOnSuccessListener { task ->
                    Firebase.firestore.collection("qr_codes")
                        .document(qrCodeId)
                        .set(mapOf(
                            "eventid" to eventWithID.id,
                            "valid" to false
                        ))
                        .addOnSuccessListener {
                            onsuccess(true,null)
                        }
                        .addOnFailureListener {
                            onsuccess(false,it.message)
                        }

                }
                .addOnFailureListener { task ->
                    onsuccess(false,task.message)
                }
        }
    }
    fun uploadToCloudinary(file: File, context: Context, onUploaded: (String?) -> Unit) {
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.Companion.FORM)
            .addFormDataPart("file", file.name, file.asRequestBody("image/*".toMediaTypeOrNull()))
            .addFormDataPart("upload_preset", "clubevents_image")
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
    fun uriToFile(context: Context, uri: Uri): File? {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val file = File.createTempFile("temp_image", ".jpg", context.cacheDir)
        file.outputStream().use { output ->
            inputStream.copyTo(output)
        }
        return file
    }


}