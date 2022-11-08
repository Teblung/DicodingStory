package com.teblung.dicodingstory.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teblung.dicodingstory.data.source.remote.response.StoryListResponse
import com.teblung.dicodingstory.data.source.remote.response.StoryResponse
import com.teblung.dicodingstory.data.source.remote.service.GetRetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()

    private val _message = MutableLiveData<String>()

    private val _listStoryData = MutableLiveData<ArrayList<StoryResponse>>()
    val listStoryData: LiveData<ArrayList<StoryResponse>> = _listStoryData

    fun getAllStory(token: String) {
        _isLoading.value = true
        GetRetrofitInstance.getApiService().getAllStory("Bearer $token")
            .enqueue(object : Callback<StoryListResponse?> {
                override fun onResponse(
                    call: Call<StoryListResponse?>,
                    response: Response<StoryListResponse?>
                ) {
                    _isLoading.value = false
                    if (!response.isSuccessful) {
                        _message.postValue(response.message())
                    } else {
                        _listStoryData.postValue(response.body()?.listStory)
                        _message.postValue(response.body()?.message)
                    }
                }

                override fun onFailure(call: Call<StoryListResponse?>, t: Throwable) {
                    _message.value = t.message
                    _isLoading.value = false
                }
            })
    }

    fun getAllStoryWithLocation(token: String) {
        _isLoading.value = true
        GetRetrofitInstance.getApiService().getAllStoryWithLocation("Bearer $token", 1)
            .enqueue(object : Callback<StoryListResponse?> {
                override fun onResponse(
                    call: Call<StoryListResponse?>,
                    response: Response<StoryListResponse?>
                ) {
                    _isLoading.value = false
                    if (!response.isSuccessful) {
                        _message.postValue(response.message())
                    } else {
                        _listStoryData.postValue(response.body()?.listStory)
                        _message.postValue(response.body()?.message)
                    }
                }

                override fun onFailure(call: Call<StoryListResponse?>, t: Throwable) {
                    _message.value = t.message
                    _isLoading.value = false
                }
            })
    }
}