package com.teblung.dicodingstory.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.teblung.dicodingstory.data.Repository
import com.teblung.dicodingstory.data.source.remote.response.UserLoginResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthVM @Inject constructor(private val repository: Repository) : ViewModel() {
    val userLogin: LiveData<UserLoginResult> = repository.userLogin
    val message: LiveData<String> = repository.message
    val loading: LiveData<Boolean> = repository.isLoading

    fun register(name: String, email: String, password: String) =
        repository.registerUser(name, email, password)

    fun login(email: String, password: String) =
        repository.loginUser(email, password)
}