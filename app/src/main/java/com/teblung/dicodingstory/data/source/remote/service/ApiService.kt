package com.teblung.dicodingstory.data.source.remote.service

import com.teblung.dicodingstory.data.source.remote.response.AddStoryResponse
import com.teblung.dicodingstory.data.source.remote.response.StoryListResponse
import com.teblung.dicodingstory.data.source.remote.response.UserLoginResponse
import com.teblung.dicodingstory.data.source.remote.response.UserRegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<UserRegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<UserLoginResponse>

    @GET("stories")
    fun getAllStory(
        @Header("Authorization") auth: String
    ): Call<StoryListResponse>

    @GET("stories")
    fun getAllStoryWithLocation(
        @Header("Authorization") auth: String,
        @Query("location")location: Int
    ): Call<StoryListResponse>

    @Multipart
    @POST("stories")
    fun uploadStory(
        @Header("Authorization") auth: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<AddStoryResponse>

    @Multipart
    @POST("stories")
    fun uploadStoryWithLocation(
        @Header("Authorization") auth: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat")latitude: Float,
        @Part("lon")longitude: Float
    ): Call<AddStoryResponse>
}