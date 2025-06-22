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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.clubsconnect.ViewModel.userside.QrScannerVieModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import androidx.camera.compose.CameraXViewfinder
import com.journeyapps.barcodescanner.CameraPreview

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPreviewScreen(modifier: Modifier = Modifier) {
    val cameraPermissionState = rememberPermissionState(permission = android.Manifest.permission.CAMERA)
    if(cameraPermissionState.status.isGranted){
        QrCodeScreen()}
    else{
        Column(modifier.fillMaxSize()) {
            val textToShow = if(cameraPermissionState.status.shouldShowRationale){
                "Whoops! Looks like we need your camera to work our magic!" +
                        "Don't worry, we just wanna see your pretty face (and maybe some cats).  " +
                        "Grant us permission and let's get this party started!"
            }else{
                "Hi there! We need your camera to work our magic! ✨\n" +
                        "Grant us permission and let's get this party started! \uD83C\uDF89"
            }

            Text(textToShow, textAlign = TextAlign.Center)
            Spacer(modifier= Modifier.height(16.dp))
            Button(onClick = {cameraPermissionState.launchPermissionRequest()}) {
                Text("Unleash the Camera!!")
            }
        }
    }
}
@Composable
fun QrCodeScreen(
    vieModel: QrScannerVieModel,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    modifier: Modifier = Modifier) {
    val surfaceRequest = vieModel.surfaceRequest.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(surfaceRequest) {
        vieModel.bindToCamera(context,lifecycleOwner)
    }
    surfaceRequest?.let { newrequest ->
        CameraXViewfinder(
            surfaceRequest = newrequest,
            modifier = modifier.fillMaxSize()
        )
    }

}