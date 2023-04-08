package com.thundermaps.apilib.android.api.responses.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.thundermaps.apilib.android.api.ExcludeFromJacocoGeneratedReport
import kotlinx.parcelize.Parcelize

@ExcludeFromJacocoGeneratedReport
@Parcelize
data class FormFieldSignature(
    @SerializedName(value = FILE_ATTACHMENT_ID) @Expose val fileAttachmentId: Int,
    @SerializedName(value = FILE_NAME) @Expose val fileName: String?,
    @SerializedName(value = ORIGINAL_URL) @Expose val url: String,
    @SerializedName(value = SIGNEE_NAME) @Expose val signeeName: String,
    @SerializedName(value = ID) @Expose val id: Int? = null,
    @SerializedName(value = REPORT_SIGNATURE_ID) @Expose val reportSignatureId: Int? = null,
) : Parcelable {
    companion object {
        private const val ID = "id"
        private const val REPORT_SIGNATURE_ID = "report_signature_id"
        private const val FILE_ATTACHMENT_ID = "file_attachment_id"
        private const val FILE_NAME = "filename"
        private const val ORIGINAL_URL = "original_url"
        private const val STYLE_URL = "style_url"
        private const val SIGNEE_NAME = "signee_name"
    }
}
