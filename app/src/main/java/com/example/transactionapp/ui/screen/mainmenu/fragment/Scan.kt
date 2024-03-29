package com.example.transactionapp.ui.screen.mainmenu.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.distinctUntilChanged
import com.example.transactionapp.R
import com.example.transactionapp.databinding.FragmentScanBinding
import com.example.transactionapp.databinding.FragmentTransactionBinding
import com.example.transactionapp.domain.db.model.Transaction
import com.example.transactionapp.ui.viewmodel.auth.Auth
import com.example.transactionapp.ui.viewmodel.location.LocationViewModel
import com.example.transactionapp.ui.viewmodel.model.BillResponseSealed
import com.example.transactionapp.ui.viewmodel.transaction.TransactionViewModel
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.FileOutputStream
import java.util.Date

class Scan : Fragment() {

    private lateinit var binding: FragmentScanBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private val db: TransactionViewModel by activityViewModels()
    private val locationViewModel: LocationViewModel by activityViewModels()

    private lateinit var cameraExecutor: ExecutorService

    private lateinit var auth: Auth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentScanBinding.inflate(layoutInflater)
        val billList = mutableListOf<Transaction>()

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

        auth = ViewModelProvider(requireActivity())[Auth::class.java]

        if (allPermissionGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.CAMERA), 123
            )
        }

        binding.cameraButton.setOnClickListener {
            takePhoto()
        }

        binding.galleryButton.setOnClickListener {
            pickFromGallery()
        }

        binding.retakeButton.setOnClickListener {
            binding.overlay.setImageBitmap(null)
            binding.retakeButton.visibility = View.GONE
            binding.confirmButton.visibility = View.GONE
            binding.cameraButton.visibility = View.VISIBLE
            binding.galleryButton.visibility = View.VISIBLE
        }

        binding.confirmButton.setOnClickListener {
            val bitmap = (binding.overlay.drawable as? BitmapDrawable)?.bitmap
            if (bitmap != null) {
                sendBillToServer(bitmap)
            } else {
                Toast.makeText(requireContext(), "No image to confirm", Toast.LENGTH_SHORT).show()
            }
        }

        auth.billResponse.observe(requireActivity(), Observer {billValue ->
            when (val data = billValue) {
                is BillResponseSealed.Success -> {

                    var locationValue = ""
                    locationViewModel.location.observe(requireActivity()){ locationLambda ->
                        locationValue = locationLambda
                    }

                    data.data.items.items.forEach {
                        val transaction = Transaction(
                            title = it.name,
                            nominal = it.price.toLong() * 12000L,
                            category = "Expense",
                            createdAt = Date(),
                            location = locationValue
                        )
                        if (!billList.contains(transaction)) {
                            billList.add(transaction)
                        }

                    }

                    db.insertBillTransaction(billList)
                    db.changeAddStatus(true)
                    auth.resetBillResponse()
                }
                is BillResponseSealed.Error -> {
                    Toast.makeText(requireContext(), data.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        })

        return binding.root
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            val selectedImageUri: Uri? = data?.data
            selectedImageUri?.let {
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, selectedImageUri)
                    val exif = ExifInterface(requireActivity().contentResolver.openInputStream(selectedImageUri)!!)
                    val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                    val rotatedBitmap = rotateBitmap(bitmap, orientation)

                    binding.overlay.setImageBitmap(rotatedBitmap)
                    binding.retakeButton.visibility = View.VISIBLE
                    binding.confirmButton.visibility = View.VISIBLE
                    binding.cameraButton.visibility = View.GONE
                    binding.galleryButton.visibility = View.GONE

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun sendBillToServer(bitmap: Bitmap) {
        val requestFile = bitmap.toString().toRequestBody("image/jpg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", "image.jpg", requestFile)
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuaW0iOiIxMzUyMTAzMSIsImlhdCI6MTcxMDk5NTU0NywiZXhwIjoxNzEwOTk1ODQ3fQ._qxhoK5LGA6VznXOPxm7CODVr2OucU1ZNjPkGpKtzhs"

        auth.postBill("Bearer $token", body)
    }


    private fun getOutputDirectory(): File {
        val mediaDir = requireContext().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else requireContext().filesDir
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile = File(
            outputDirectory, System.currentTimeMillis().toString() + ".jpg"
        )
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(requireContext()), object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                }
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                    val exif = ExifInterface(photoFile.absolutePath)
                    val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                    val rotatedBitmap = rotateBitmap(bitmap, orientation)
                    binding.overlay.setImageBitmap(rotatedBitmap)

                    binding.retakeButton.visibility = View.VISIBLE
                    binding.confirmButton.visibility = View.VISIBLE
                    binding.cameraButton.visibility = View.GONE
                    binding.galleryButton.visibility = View.GONE
                }
            }
        )
    }

    private fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(270f)
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                Toast.makeText(requireContext(), "Use case binding failed", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 123) {
            if (allPermissionGranted()) {
                Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show()
                startCamera()
            } else {
                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun allPermissionGranted() = arrayOf(android.Manifest.permission.CAMERA).all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

}