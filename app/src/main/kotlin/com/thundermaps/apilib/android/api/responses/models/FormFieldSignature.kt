package com.thundermaps.apilib.android.api.responses.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.thundermaps.apilib.android.api.ExcludeFromJacocoGeneratedReport
import kotlinx.android.parcel.Parcelize

@ExcludeFromJacocoGeneratedReport
@Parcelize
data class FormFieldSignature(
    @SerializedName(value = FILE_ATTACHMENT_ID) val fileAttachmentId: Int,
    @SerializedName(value = FILE_NAME) val fileName: String,
    @SerializedName(value = ORIGINAL_URL) val url: String,
    @SerializedName(value = SIGNEE_NAME) val signeeName: String
) : Parcelable {
    companion object {
        private const val FILE_ATTACHMENT_ID = "file_attachment_id"
        private const val FILE_NAME = "filename"
        private const val ORIGINAL_URL = "original_url"
        private const val SIGNEE_NAME = "signee_name"
    }
}
