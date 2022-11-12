package com.teblung.dicodingstory.ui.home.upload

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.teblung.dicodingstory.R
import com.teblung.dicodingstory.data.source.local.preference.DataStoreVM
import com.teblung.dicodingstory.databinding.ActivityUploadBinding
import com.teblung.dicodingstory.ui.home.MainActivity
import com.teblung.dicodingstory.utils.Utils
import com.teblung.dicodingstory.utils.Utils.reduceFileImage
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class UploadActivity : AppCompatActivity() {

    private val binding: ActivityUploadBinding by lazy {
        ActivityUploadBinding.inflate(layoutInflater)
    }
    private val uploadVM by viewModels<UploadVM>()
    private val dataStoreVM by viewModels<DataStoreVM>()

    private lateinit var path: String
    private var descriptionText: String = ""
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var getFile: File? = null

    private val cameraIntentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(path)
            getFile = myFile

            val result = BitmapFactory.decodeFile(getFile?.path)
            binding.imgPreviewStory.setImageBitmap(result)
        }
    }

    private val galleryIntentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri

            val myFile = Utils.uriToFile(selectedImg, this@UploadActivity)

            getFile = myFile

            binding.imgPreviewStory.setImageURI(selectedImg)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupBundle()
        setupUI()
    }

    private fun setupBundle() {
        if (intent.getIntExtra(UPLOAD_REQUEST, 0) == MY_LOCATION_TO_SHARE) {
            latitude = intent.getDoubleExtra(LATITUDE_POINT, 0.0)
            longitude = intent.getDoubleExtra(LONGITUDE_POINT, 0.0)
        }
    }

    private fun setupUI() {
        supportActionBar?.title = getString(R.string.upload_story)
        binding.apply {
            btnOpenCamera.text = getString(R.string.camera)
            btnOpenGallery.text = getString(R.string.gallery)
            btnUploadStory.text = getString(R.string.upload)
            edDesc.hint = "Deskripsi"

            btnOpenCamera.setOnClickListener { openCamera() }
            btnOpenGallery.setOnClickListener { openGallery() }
            btnUploadStory.setOnClickListener {
                uploadStory()
            }
        }
    }

    private fun showMessage(message: String) {
        when (message) {
            getString(R.string.success_upload) -> {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
            }
            else -> {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            btnOpenCamera.isEnabled = !isLoading
            btnOpenGallery.isEnabled = !isLoading
            btnUploadStory.isEnabled = !isLoading
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        Utils.createTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@UploadActivity,
                getString(R.string.auth),
                it
            )
            path = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            cameraIntentLauncher.launch(intent)
        }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.apply {
            action = Intent.ACTION_GET_CONTENT
            type = "image/*"
        }

        val chooser = Intent.createChooser(intent, getString(R.string.select_image))
        galleryIntentLauncher.launch(chooser)
    }

    private fun uploadStory() {
        uploadVM.apply {
            loading.observe(this@UploadActivity) {
                showLoading(it)
            }
            message.observe(this@UploadActivity) {
                showMessage(it)
            }
            if (getFile != null) {
                descriptionText = binding.edDesc.text.toString()
                if (descriptionText.isEmpty()) {
                    binding.edDesc.error = resources.getString(R.string.req_field)
                } else {
                    val file = reduceFileImage(getFile as File)
                    dataStoreVM.apply {
                        getLoginSession().observe(this@UploadActivity) {
                            if (latitude == 0.0 && longitude == 0.0) {
                                uploadWithoutLocation("Bearer ${it.token}", file, descriptionText)
                            } else {
                                uploadWithLocation(
                                    "Bearer ${it.token}",
                                    file,
                                    descriptionText,
                                    latitude,
                                    longitude
                                )
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(
                    this@UploadActivity,
                    getString(R.string.enter_picture),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionGranted()) {
                Toast.makeText(this, getString(R.string.permission), Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
        const val UPLOAD_REQUEST = "UPLOAD_REQUEST_CODE"
        const val LATITUDE_POINT = "LATITUDE_POINT"
        const val LONGITUDE_POINT = "LONGITUDE_POINT"
        const val MY_LOCATION_TO_SHARE = 11
    }
}