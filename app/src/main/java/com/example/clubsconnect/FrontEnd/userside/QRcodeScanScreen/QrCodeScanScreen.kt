import android.widget.Toast
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
//import com.example.clubsconnect.ViewModel.userside.QrScannerViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.clubsconnect.FrontEnd.userside.QRcodeScanScreen.QrCodeAnalyzer
import com.example.clubsconnect.ViewModel.userside.QrScannerViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPreviewScreen(modifier: Modifier = Modifier) {
    val cameraPermissionState = rememberPermissionState(permission = android.Manifest.permission.CAMERA)
    if(cameraPermissionState.status.isGranted){
        val qrviewmodel = QrScannerViewModel()
        QrCodeScreen(qrviewmodel,
            modifier=modifier.fillMaxSize())}
    else{
        Column(modifier.fillMaxSize()
            .padding(horizontal = 5.dp),
            verticalArrangement = Arrangement.Center) {
            val textToShow = if(cameraPermissionState.status.shouldShowRationale){
                "Whoops! Looks like we need your camera to work on your attendance," +
                        " don't worry just give the access to camera!!"
            }else{
                "Hi there! We need your camera to work our magic! ✨\n" +
                        "Grant us permission and let's get this party started! \uD83C\uDF89"
            }

            Text(textToShow, textAlign = TextAlign.Center)
            Spacer(modifier= Modifier.height(16.dp))
            Button(onClick = {cameraPermissionState.launchPermissionRequest()},
                modifier= Modifier.align(Alignment.CenterHorizontally)) {
                Text("Unleash the Camera!!",
                    )
            }
        }
    }
}
@Composable
fun QrCodeScreen(
    viewModel: QrScannerViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cameraController  = remember { LifecycleCameraController(context )
        .apply {
            setEnabledUseCases(
                CameraController.IMAGE_ANALYSIS
            )
        }}
    LaunchedEffect(Unit) {
        cameraController.setImageAnalysisAnalyzer(ContextCompat.getMainExecutor(context),
            QrCodeAnalyzer{
                result->
                viewModel.setAttendance(result) {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }

            }
        )
    }
    Box(modifier.fillMaxSize()){
        Column {
            Text("Scan The QR code",
                textAlign = TextAlign.Center,
                modifier= Modifier.align(Alignment.CenterHorizontally)
                )
            Spacer(modifier= Modifier.height(3.dp))
            Box (modifier= Modifier.align(Alignment.CenterHorizontally)){
                CameraPreview(cameraController,modifier= Modifier.align(Alignment.Center)
                    .fillMaxSize()
                )
            }

        }

    }


}


@Composable
fun CameraPreview(cameraController: LifecycleCameraController,
                  modifier: Modifier = Modifier) {
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
        modifier=modifier,
        factory = { PreviewView(it).apply {
            this.controller=cameraController
            cameraController.bindToLifecycle(lifecycleOwner)
        }}
    )
}