package com.example.clubsconnect.FrontEnd.userside.QRcodeScanScreen

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class QrCodeAnalyzer (private val onQrCodeAnlyze : (String)-> Unit): ImageAnalysis.Analyzer {
    private val scanner = BarcodeScanning.getClient()
    private var lastScannedTime = 0L
    private val cooldownMillis = 3000 // 3 seconds
    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if(mediaImage!=null){
            val inputImage =
                InputImage.fromMediaImage(mediaImage,imageProxy.imageInfo.rotationDegrees)

            scanner.process(inputImage)
                .addOnSuccessListener {
                    barcodes->
                    val now = System.currentTimeMillis()

                    if (now - lastScannedTime >= cooldownMillis) {
                        barcodes.firstOrNull()?.rawValue?.let { value ->
                            lastScannedTime = now
                            onQrCodeAnlyze(value)
                        }
                        //only once taking
                    }
                }
                .addOnFailureListener {
                    Log.d("QrCodeAnalyzer","Error",it)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }else{
            Log.d("QrCodeAnalyzer","Empty Image")
        }
    }
}