package com.thundermaps.apilib.android.api.requests.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SessionHeader(
    @SerializedName("access_token") @Expose val accessToken: String,
    @SerializedName("refresh_token") @Expose val refreshToken: String
)

data class SessionRequest(
    @SerializedName("session") @Expose val session: SessionHeader
)
