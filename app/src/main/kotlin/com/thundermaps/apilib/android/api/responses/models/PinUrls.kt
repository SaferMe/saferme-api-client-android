package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.SerializedName

data class PinUrls(
    @SerializedName("at1x") val url1x: String,
    @SerializedName("at2x") val url2x: String,
    @SerializedName("at3x") val url3x: String
)
