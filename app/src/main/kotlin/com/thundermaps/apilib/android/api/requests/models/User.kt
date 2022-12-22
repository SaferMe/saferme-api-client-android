package com.thundermaps.apilib.android.api.requests.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class User(
    val email: String,
    val password: String
)

data class SessionBody(
    val user: User
)

data class UserV4(
    @SerializedName("app_bundle_id") @Expose val appBundleId: String,
    val email: String,
    val password: String
)

data class SessionBodyV4(
    val session: UserV4
)
