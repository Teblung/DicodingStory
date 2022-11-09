package com.teblung.dicodingstory.data.source.local.preference

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DataStoreVM @Inject constructor(private val preferences: SessionUser) : ViewModel() {

    fun getLoginSession(): LiveData<User> {
        return preferences.getLoginData().asLiveData()
    }

    fun setUserLogin(userLogin: User) {
        viewModelScope.launch {
            preferences.setUserLogin(userLogin)
        }
    }

    fun setUserLogout() {
        viewModelScope.launch {
            preferences.setUserLogout()
        }
    }
}