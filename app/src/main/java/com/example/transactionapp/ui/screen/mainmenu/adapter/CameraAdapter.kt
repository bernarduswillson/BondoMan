import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import kotlinx.coroutines.launch

interface ImageCaptureCallback {
    fun onImageCaptureInitialized(imageCapture: ImageCapture)
}

class CameraAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val fragment: Fragment,
    private val previewView: PreviewView,
    private val imageCaptureCallback: ImageCaptureCallback
) {

    private var imageCapture: ImageCapture? = null

    fun startCamera() {
        if (allPermissionGranted()) {
            bindCameraUseCases()
        } else {
            requestCameraPermission()
        }
    }

    fun bindCameraUseCases() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(fragment.requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            val imageCapture = ImageCapture.Builder().build() // Initialize imageCapture
            imageCaptureCallback.onImageCaptureInitialized(imageCapture) // Invoke callback
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                Toast.makeText(fragment.requireContext(), "Use case binding failed", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(fragment.requireContext()))
    }

    private fun allPermissionGranted() = arrayOf(Manifest.permission.CAMERA).all {
        ContextCompat.checkSelfPermission(
            fragment.requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            fragment.requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 123
    }

}