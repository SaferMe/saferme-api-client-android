package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.thundermaps.apilib.android.api.ExcludeFromJacocoGeneratedReport

@ExcludeFromJacocoGeneratedReport
data class FileAttachment(
    @Expose val id: Int,
    @SerializedName(value = ORIGINAL_URL) @Expose val originalUrl: String,
    @SerializedName(value = FILE_NAME) @Expose val fileName: String?
) {
    companion object {
        const val ORIGINAL_URL = "original_url"
        const val FILE_NAME = "filename"
    }
}
