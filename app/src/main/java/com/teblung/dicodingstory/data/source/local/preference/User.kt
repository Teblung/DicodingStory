package com.teblung.dicodingstory.data.source.local.preference

data class User(
    val name: String,
    val token: String,
    val userId: String,
    val isLogin: Boolean
)