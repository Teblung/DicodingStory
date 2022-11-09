package com.teblung.dicodingstory.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import com.teblung.dicodingstory.data.source.local.preference.SessionUser
import com.teblung.dicodingstory.data.source.local.room.Database
import com.teblung.dicodingstory.data.source.remote.response.*
import com.teblung.dicodingstory.data.source.remote.service.ApiService
import com.teblung.dicodingstory.utils.wrapWithEspressoIdlingResource
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class Repository @Inject constructor(
    private val database: Database,
    private val apiService: ApiService,
    private val preference: SessionUser
) {
    private val _userLogin = MutableLiveData<UserLoginResult>()
    val userLogin: LiveData<UserLoginResult> = _userLogin

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _storyList = MutableLiveData<List<StoryResponse>>()
    val storyList: LiveData<List<StoryResponse>> = _storyList

    fun registerUser(usrName: String, usrEmail: String, usrPass: String) {
        wrapWithEspressoIdlingResource {
            _isLoading.value = true
            apiService.register(usrName, usrEmail, usrPass)
                .enqueue(object : Callback<UserRegisterResponse> {
                    override fun onResponse(
                        call: Call<UserRegisterResponse>,
                        response: Response<UserRegisterResponse>
                    ) {
                        if (response.isSuccessful) {
                            _message.value = response.message()
                            _isLoading.value = false
                        }
                    }

                    override fun onFailure(call: Call<UserRegisterResponse>, t: Throwable) {
                        _message.value = t.message
                        _isLoading.value = false
                    }
                })
        }
    }

    fun loginUser(usrEmail: String, usrPass: String) {
        wrapWithEspressoIdlingResource {
            _isLoading.value = true
            apiService.login(usrEmail, usrPass)
                .enqueue(object : Callback<UserLoginResponse> {
                    override fun onResponse(
                        call: Call<UserLoginResponse>,
                        response: Response<UserLoginResponse>
                    ) {
                        if (response.isSuccessful) {
                            _message.value = response.body()?.message
                            _userLogin.value = response.body()?.loginResult
                        } else {
                            _message.value = response.message()
                        }
                        _isLoading.value = false
                    }

                    override fun onFailure(call: Call<UserLoginResponse>, t: Throwable) {
                        _message.value = t.message
                        _isLoading.value = false
                    }
                })
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getAllStory(): LiveData<PagingData<StoryResponse>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = RemoteMediator(database, apiService, preference),
            pagingSourceFactory = {
                database.storyDao().getAllStory()
            }
        ).liveData
    }

    fun uploadStory(token: String, picture: File, desc: String) {
        wrapWithEspressoIdlingResource {
            _isLoading.value = true
            val description = desc.toRequestBody("text/plain".toMediaType())
            val pictureFile = picture.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                picture.name,
                pictureFile
            )
            val service = apiService.uploadStory(token, imageMultipart, description)
            service.enqueue(object : Callback<AddStoryResponse> {
                override fun onResponse(
                    call: Call<AddStoryResponse>,
                    response: Response<AddStoryResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {
                            _message.value = responseBody.message
                        } else {
                            _message.value = response.message()
                        }
                        _isLoading.value = false
                    }
                }

                override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                    _message.value = t.message
                    _isLoading.value = false
                }
            })
        }
    }

    fun getStoryWithLocation(token: String) {
        wrapWithEspressoIdlingResource {
            _isLoading.value = true
            apiService.getAllStoryWithLocation(token, 1)
                .enqueue(object : Callback<StoryListResponse?> {
                    override fun onResponse(
                        call: Call<StoryListResponse?>,
                        response: Response<StoryListResponse?>
                    ) {
                        if (response.isSuccessful) {
                            _storyList.postValue(response.body()?.listStory)
                            _message.postValue(response.body()?.message)
                        } else {
                            _message.postValue(response.message())
                        }
                        _isLoading.value = false
                    }

                    override fun onFailure(call: Call<StoryListResponse?>, t: Throwable) {
                        _message.postValue(t.message)
                        _isLoading.value = false
                    }
                })
        }
    }

    fun uploadStoryWithLocation(
        token: String,
        picture: File,
        storyDesc: String,
        usrLat: Float,
        usrLong: Float
    ) {
        _isLoading.value = true
        val description = storyDesc.toRequestBody("text/plain".toMediaType())
        val pictureFile = picture.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            picture.name,
            pictureFile
        )
        apiService.uploadStoryWithLocation(
            token,
            imageMultipart,
            description,
            usrLat,
            usrLong
        ).enqueue(object : Callback<AddStoryResponse> {
            override fun onResponse(
                call: Call<AddStoryResponse>,
                response: Response<AddStoryResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _message.value = responseBody.message
                    } else {
                        _message.value = response.message()
                    }
                    _isLoading.value = false
                }
            }

            override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                _message.value = t.message
                _isLoading.value = false
            }
        })
    }
}
