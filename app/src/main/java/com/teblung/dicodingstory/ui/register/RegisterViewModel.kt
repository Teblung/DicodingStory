package com.teblung.dicodingstory.ui.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teblung.dicodingstory.data.source.remote.response.UserRegisterResponse
import com.teblung.dicodingstory.data.source.remote.service.GetRetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    fun register(name: String, email: String, password: String) {
        _isLoading.value = true
        GetRetrofitInstance.getApiService().register(name, email, password)
            .enqueue(object : Callback<UserRegisterResponse?> {
                override fun onResponse(
                    call: Call<UserRegisterResponse?>,
                    response: Response<UserRegisterResponse?>
                ) {
                    _isLoading.value = false
                    if (!response.isSuccessful) {
                        _message.value = response.message()
                    } else {
                        _message.value = response.body()?.message
                    }
                }

                override fun onFailure(call: Call<UserRegisterResponse?>, t: Throwable) {
                    Log.d("ViewModel", "Start4")
                    _message.value = t.message
                    _isLoading.value = false
                }
            })
    }
}