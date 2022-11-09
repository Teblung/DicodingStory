package com.teblung.dicodingstory.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.teblung.dicodingstory.data.Repository
import com.teblung.dicodingstory.data.source.remote.response.StoryResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapsVM @Inject constructor(private val repository: Repository) : ViewModel() {
    val listStoryData: LiveData<List<StoryResponse>> = repository.storyList

    fun getStoryWithLocation(token: String) {
        repository.getStoryWithLocation("Bearer $token")
    }
}