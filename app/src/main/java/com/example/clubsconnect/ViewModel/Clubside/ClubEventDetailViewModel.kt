package com.example.clubsconnect.ViewModel.Clubside

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.location.Location
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.google.android.gms.location.LocationServices
import java.io.File

data class RegisteredAttendes(
    val name : String = "",
    val email : String = "",
    val timestamp : Long =0,
    val present : Boolean = false
)

data class screenState(
    val loading : Boolean =false
)
class ClubEventDetailViewModel(eventId : String) : ViewModel() {

    private val _state = mutableStateOf(screenState())
    val state : State<screenState> = _state

    private val _event = mutableStateOf(clubEvent())
    val event : State<clubEvent> = _event

    private val _registeredAttendes = mutableStateListOf<RegisteredAttendes>()
    val registeredAttendes : List<RegisteredAttendes> = _registeredAttendes

    private val _presentAttendes = mutableStateListOf<RegisteredAttendes>()
    val presentAttendes : List<RegisteredAttendes> = _presentAttendes

    private val _activateQrcode = mutableStateOf(false)
    val activateQrcode: Boolean
        get() = _activateQrcode.value

    private val _rangeOfQR = mutableStateOf(0.0)
    val rangeOfQR : String
        get() = _rangeOfQR.value.toString()

    private val db = Firebase.firestore

    val QrCodeBitmap = mutableStateOf<Bitmap?>(null)

    init {
        viewModelScope.launch {
            fetchInfo(eventId)
            QrCodeBitmap.value = createQRCode(event.value.qrcodeid)
        }
    }

    suspend fun fetchInfo(eventId : String){
        _state.value = state.value.copy(loading = true)
        try{
            val document = db.collection("events")
                .document(eventId)
                .get()
                .await()

            val attendees = db.collection("event_registrations")
                .document(eventId)
                .collection("users")
                .get()
                .await()

            _registeredAttendes.clear()
            _registeredAttendes.clear()
            _registeredAttendes.addAll(
                attendees.documents.mapNotNull { it.toObject(RegisteredAttendes::class.java) }
            )
            _presentAttendes.addAll(
                registeredAttendes.filter { it.present }
            )


            Log.d("ClubEventDetailViewModel", "Registered attendees: ${registeredAttendes.toList()}")
            _event.value = document.toObject(clubEvent::class.java) ?: clubEvent()
            val attendes = fetchattendes(eventId)
            _event.value=_event.value.copy(attendes = attendes)
            _state.value = state.value.copy(loading = false)
            fetchActivateQrState(event.value.qrcodeid)
    }
        catch (e : Exception){
            Log.d("ClubEventDetailViewModel", "Error fetching event details", e)
            _state.value = state.value.copy(loading = false)

        }
    }

    suspend fun fetchattendes(id : String) : Int{
        return try {
            val result = db.collection("event_registrations")
                .document(id)
                .collection("users")
                .get()
                .await()
            result.size()


        }catch (e : Exception){
            Log.d("fetchattendes", "Error fetching attendes: ${e.message}")
            0
        }

    }

    suspend fun fetchActivateQrState(id : String){
        val result = db.collection("qr_codes")
            .document(id)
            .get()
            .await()
        _activateQrcode.value = result.getBoolean("valid") ?: false
        _rangeOfQR.value=result.getDouble("range")?:0.0
    }

    fun createQRCode(qrcodeId : String) : Bitmap{

        val size = 512
        val bits = QRCodeWriter().encode(qrcodeId, BarcodeFormat.QR_CODE,size,size )
        val bmp = createBitmap(size, size, Bitmap.Config.RGB_565)

        for(x in 0 until size){
            for (y in 0 until size){
                bmp[x, y] = if (bits.get(
                        x,
                        y
                    )
                ) Color.BLACK else Color.WHITE
            }
        }
        Log.d("qrcode","completed")
        return bmp
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun activateDeactivateQr(activate : Boolean,range : Double, qrCodeId : String, context: Context,onactivate : (String)-> Unit){
        val fusedLocation = LocationServices.getFusedLocationProviderClient(context)
        fusedLocation.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val latitude = location.latitude
                val longitude = location.longitude

                        Firebase.firestore.collection("qr_codes")
                            .document(qrCodeId)
                            .update(
                                mapOf(
                                    "valid" to activate,
                                    "latitude" to latitude,
                                    "longitude" to longitude,
                                    "range" to range
                                )
                            )
                            .addOnSuccessListener {
                                if(activate){
                                    onactivate("QR Code Activated : For ${range} metres")
                                }else{
                                    onactivate("QR Code Deactivated")
                                }
                            }
                            .addOnFailureListener {
                                onactivate("Error Updating QR Code")
                            }

            }
        }
            .addOnFailureListener {
                onactivate("Error Getting Location")
                Log.d("ClubEventDetailViewModel", "Error getting location", it)
            }


    }

    fun saveQrCodeToFile(context : Context, event: clubEvent, bitmap: Bitmap): File{
        val file = File(context.cacheDir,"${event.name}_qr_code.png")
        file.outputStream().use {
            out->
            bitmap.compress(Bitmap.CompressFormat.PNG,100,out)
        }
        return file
    }

  fun deleteEvent(eventId: String){

         db.collection("events")
             .document(eventId)
             .delete()
             .addOnSuccessListener {
                 db.collection("event_registraions")
                     .document(eventId)
                     .delete()
                     .addOnSuccessListener {
                         Log.d("ClubEventDetailViewModel", "Event and associated registrations deleted successfully")
                     }
                     .addOnFailureListener { e ->
                         Log.d("ClubEventDetailViewModel", "Error deleting event registrations", e)
                     }
                 Log.d("ClubEventDetailViewModel", "Event deleted successfully")
             }
             .addOnFailureListener { e ->
                 Log.d("ClubEventDetailViewModel", "Error deleting event", e)
             }

    }
}