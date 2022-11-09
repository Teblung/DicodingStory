package com.teblung.dicodingstory.ui.home.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.teblung.dicodingstory.data.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UploadVM @Inject constructor(private val repository: Repository) : ViewModel() {
    val message: LiveData<String> = repository.message
    val loading: LiveData<Boolean> = repository.isLoading

    fun uploadWithoutLocation(token: String, image: File, desc: String) {
        repository.uploadStory(token, image, desc)
    }

    fun uploadWithLocation(
        token: String,
        file: File,
        descriptionText: String,
        latitude: Double,
        longitude: Double
    ) {
        repository.uploadStoryWithLocation(
            token,
            file,
            descriptionText,
            latitude.toFloat(),
            longitude.toFloat()
        )
    }
}