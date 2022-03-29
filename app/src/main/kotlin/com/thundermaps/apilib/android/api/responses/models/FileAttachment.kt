package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.SerializedName
import com.thundermaps.apilib.android.api.ExcludeFromJacocoGeneratedReport

@ExcludeFromJacocoGeneratedReport
data class FileAttachment(
    val id: Int,
    @SerializedName(value = ORIGINAL_URL) val originalUrl: String,
    @SerializedName(value = FILE_NAME) val fileName: String?
) {
    companion object {
        const val ORIGINAL_URL = "original_url"
        const val FILE_NAME = "filename"
    }
}
