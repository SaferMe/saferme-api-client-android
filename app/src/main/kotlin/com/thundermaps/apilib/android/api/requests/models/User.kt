package com.thundermaps.apilib.android.api.requests.models

data class User(
    val email: String,
    val password: String
)

data class SessionBody(
    val user: User
)
