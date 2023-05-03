package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.thundermaps.apilib.android.api.ExcludeFromJacocoGeneratedReport

@ExcludeFromJacocoGeneratedReport
data class FileAttachment(
    @Expose val id: Int,
    @SerializedName(value = ORIGINAL_URL) @Expose val originalUrl: String,
    @SerializedName(value = FILE_NAME) @Expose val filename: String?,
    @SerializedName(value = STYLE_URL) @Expose val styleUrl: StyleUrl?
) {
    data class StyleUrl(@Expose val medium: String, @Expose val thumb: String)

    companion object {
        const val ORIGINAL_URL = "original_url"
        const val FILE_NAME = "filename"
        const val STYLE_URL = "style_url"
    }
}
