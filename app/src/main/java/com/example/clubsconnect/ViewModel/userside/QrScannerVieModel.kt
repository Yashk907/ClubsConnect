package com.example.clubsconnect.ViewModel.userside

import androidx.camera.core.CameraSelector
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.google.api.Context
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class QrScannerVieModel : ViewModel(){
    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    val surfaceRequest : StateFlow<SurfaceRequest?> = _surfaceRequest

    private val cameraPreviewUseCase = androidx.camera.core.Preview.Builder().build().apply {
        setSurfaceProvider {
            newSurfaceRequest->
            _surfaceRequest.update { newSurfaceRequest }
        }
    }

    suspend fun bindToCamera(context: android.content.Context,
                             localLifecycleOwner: LifecycleOwner){
        val processCameraProvider = ProcessCameraProvider.awaitInstance(context)
        processCameraProvider.bindToLifecycle(
            lifecycleOwner = localLifecycleOwner,
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
            cameraPreviewUseCase
        )

        try {
            awaitCancellation()
        } finally {
            processCameraProvider.unbindAll()
        }
    }
}