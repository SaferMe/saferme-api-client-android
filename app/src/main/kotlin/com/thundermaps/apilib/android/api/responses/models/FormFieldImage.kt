package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class FormFieldImage(
    @Expose val id: Int,
    @SerializedName("original_url") val originalUrl: String,
    @Expose val filename: String,
    @SerializedName("style_url") val styleUrl: ImageStyleUrl
)

data class ImageStyleUrl(@Expose val medium: String, @Expose val thumb: String)
