package com.example.transactionapp.ui.screen.mainmenu.fragment

import CameraAdapter
import ImageCaptureCallback
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.RectF
import android.media.ExifInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.transactionapp.R
import com.example.transactionapp.databinding.FragmentEditProfileBinding
import com.example.transactionapp.ui.viewmodel.profile.ProfileViewModel
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class EditProfile : Fragment(), ImageCaptureCallback {

    private lateinit var binding: FragmentEditProfileBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraAdapter: CameraAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using binding
        binding = FragmentEditProfileBinding.inflate(layoutInflater)

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

        cameraAdapter = CameraAdapter( requireContext(), viewLifecycleOwner, binding.previewView, this)
        cameraAdapter.startCamera()

        // Get view model
        val editProfileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        // Toggle twibbon handler
        binding.btnToggleTwibbon.setOnClickListener {
            editProfileViewModel.toggleTwibbon()
        }

        // Shutter button handler
        binding.btnCameraButton.setOnClickListener {
            takePhoto()
        }

        // Twibbon state observer
        editProfileViewModel.isTwibbonActive.observe(requireActivity()) {
            if (it) {
                binding.btnToggleTwibbon.setText(R.string.btn_enable_twibbon)
                binding.ivTwibbon.visibility = View.VISIBLE
            } else {
                binding.btnToggleTwibbon.setText(R.string.btn_disable_twibbon)
                binding.ivTwibbon.visibility = View.INVISIBLE
            }
        }

        return binding.root
    }

    override fun onImageCaptureInitialized(imageCapture: ImageCapture) {
        this.imageCapture = imageCapture
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
                    scaleCenterCrop(rotatedBitmap)
                    binding.cpCameraPreview.visibility = View.VISIBLE

                    binding.previewView.visibility = View.GONE
                }
            }
        )
    }

    fun scaleCenterCrop(source: Bitmap) {
        val sourceWidth = source.width
        val sourceHeight = source.height
        val targetWidth = 350
        val targetHeight = 350

        val xScale = targetWidth.toFloat() / sourceWidth
        val yScale = targetHeight.toFloat() / sourceHeight
        val scale = Math.max(xScale, yScale)

        val scaledWidth = scale * sourceWidth
        val scaledHeight = scale * sourceHeight

        val left = (targetWidth - scaledWidth) / 2
        val top = (targetHeight - scaledHeight) / 2

        val targetRect = RectF(left, top, left + scaledWidth, top + scaledHeight)

        val dest = Bitmap.createBitmap(targetWidth, targetHeight, source.config)
        val canvas = Canvas(dest)
        canvas.drawBitmap(source, null, targetRect, null)

        binding.cpCameraPreview.setImageBitmap(dest)
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
}