package com.teblung.dicodingstory.data.source.remote.response

data class UserLoginResponse(
    val error: Boolean,
    val loginResult: UserLoginResult,
    val message: String
)

data class UserLoginResult(
    var name: String,
    var token: String,
    var userId: String
)

