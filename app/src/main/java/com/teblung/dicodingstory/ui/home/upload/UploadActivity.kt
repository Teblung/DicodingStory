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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.teblung.dicodingstory.R
import com.teblung.dicodingstory.data.source.local.preference.SessionUser
import com.teblung.dicodingstory.data.source.remote.response.AddStoryResponse
import com.teblung.dicodingstory.data.source.remote.service.GetRetrofitInstance
import com.teblung.dicodingstory.databinding.ActivityUploadBinding
import com.teblung.dicodingstory.utils.Utils
import com.teblung.dicodingstory.utils.Utils.reduceFileImage
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class UploadActivity : AppCompatActivity() {

    private val binding: ActivityUploadBinding by lazy {
        ActivityUploadBinding.inflate(layoutInflater)
    }

    private lateinit var preferences: SessionUser
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
        setupPref()
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

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            btnOpenCamera.isEnabled = !isLoading
            btnOpenGallery.isEnabled = !isLoading
            btnUploadStory.isEnabled = !isLoading
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun setupPref() {
        preferences = SessionUser(this)
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
        if (getFile != null) {
            descriptionText = binding.edDesc.text.toString()
            if (descriptionText.isEmpty()) {
                binding.edDesc.error = resources.getString(R.string.req_field)
            } else {
                showLoading(true)
                val file = reduceFileImage(getFile as File)
                val description = descriptionText.toRequestBody("text/plain".toMediaType())
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )

                val token = "Bearer ${preferences.getLoginData().token}"
                if (latitude == 0.0 && longitude == 0.0) {
                    uploadWithoutLocation(token, imageMultipart, description)
                } else {
                    uploadWithLocation(token, imageMultipart, description, latitude, longitude)
                }
            }
        } else {
            Toast.makeText(
                this@UploadActivity,
                getString(R.string.enter_picture),
                Toast.LENGTH_SHORT
            ).show()
            showLoading(false)
        }
    }

    private fun uploadWithLocation(
        token: String,
        imageMultipart: MultipartBody.Part,
        description: RequestBody,
        latitude: Double,
        longitude: Double
    ) {
        GetRetrofitInstance.getApiService().uploadStoryWithLocation(
            token,
            imageMultipart,
            description,
            latitude.toFloat(),
            longitude.toFloat()
        )
            .enqueue(object : Callback<AddStoryResponse?> {
                override fun onResponse(
                    call: Call<AddStoryResponse?>,
                    response: Response<AddStoryResponse?>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {
                            Toast.makeText(
                                this@UploadActivity,
                                responseBody.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        } else {
                            Toast.makeText(
                                this@UploadActivity,
                                response.message(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    showLoading(false)
                }

                override fun onFailure(call: Call<AddStoryResponse?>, t: Throwable) {
                    Toast.makeText(
                        this@UploadActivity,
                        t.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    showLoading(false)
                }
            })
    }

    private fun uploadWithoutLocation(
        token: String,
        imageMultipart: MultipartBody.Part,
        description: RequestBody
    ) {
        GetRetrofitInstance.getApiService().uploadStory(token, imageMultipart, description)
            .enqueue(object : Callback<AddStoryResponse?> {
                override fun onResponse(
                    call: Call<AddStoryResponse?>,
                    response: Response<AddStoryResponse?>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {
                            Toast.makeText(
                                this@UploadActivity,
                                responseBody.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        } else {
                            Toast.makeText(
                                this@UploadActivity,
                                response.message(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    showLoading(false)
                }

                override fun onFailure(call: Call<AddStoryResponse?>, t: Throwable) {
                    Toast.makeText(
                        this@UploadActivity,
                        t.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    showLoading(false)
                }
            })
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