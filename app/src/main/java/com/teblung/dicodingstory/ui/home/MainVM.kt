package com.teblung.dicodingstory.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.teblung.dicodingstory.data.Repository
import com.teblung.dicodingstory.data.source.remote.response.StoryResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(private val repository: Repository) : ViewModel() {
    val message: LiveData<String> = repository.message
    val loading: LiveData<Boolean> = repository.isLoading
    val stories: LiveData<PagingData<StoryResponse>> =
        repository.getAllStory().cachedIn(viewModelScope)
}