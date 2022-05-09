package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.thundermaps.apilib.android.api.ExcludeFromJacocoGeneratedReport

@ExcludeFromJacocoGeneratedReport
data class FormFieldImage(
    @Expose val id: Int,
    @SerializedName("original_url") @Expose val originalUrl: String,
    @Expose val filename: String?,
    @SerializedName("style_url") @Expose val styleUrl: ImageStyleUrl?
)

@ExcludeFromJacocoGeneratedReport
data class ImageStyleUrl(@Expose val medium: String, @Expose val thumb: String)
