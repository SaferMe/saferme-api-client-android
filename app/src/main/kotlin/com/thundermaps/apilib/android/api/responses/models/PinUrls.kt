package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.thundermaps.apilib.android.api.ExcludeFromJacocoGeneratedReport

@ExcludeFromJacocoGeneratedReport
data class PinUrls(
    @SerializedName("at1x") @Expose val url1x: String,
    @SerializedName("at2x") @Expose val url2x: String,
    @SerializedName("at3x") @Expose val url3x: String
)
