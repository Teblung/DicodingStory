package com.teblung.dicodingstory.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teblung.dicodingstory.data.source.remote.response.UserLoginResponse
import com.teblung.dicodingstory.data.source.remote.response.UserLoginResult
import com.teblung.dicodingstory.data.source.remote.service.GetRetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _userLogin = MutableLiveData<UserLoginResult>()
    val userLogin: LiveData<UserLoginResult> = _userLogin

    fun login(email: String, password: String) {
        _isLoading.value = true
        GetRetrofitInstance.getApiService().login(email, password)
            .enqueue(object : Callback<UserLoginResponse?> {
                override fun onResponse(
                    call: Call<UserLoginResponse?>,
                    response: Response<UserLoginResponse?>
                ) {
                    _isLoading.value = false
                    if (!response.isSuccessful) {
                        _message.value = response.message()
                    } else {
                        _userLogin.value = response.body()?.loginResult
                        _message.value = response.body()?.message
                    }
                }

                override fun onFailure(call: Call<UserLoginResponse?>, t: Throwable) {
                    _message.value = t.message
                    _isLoading.value = false
                }
            })
    }
}