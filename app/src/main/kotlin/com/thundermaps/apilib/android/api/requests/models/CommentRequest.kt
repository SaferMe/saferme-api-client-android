package com.thundermaps.apilib.android.api.requests.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ReportCommentRequest(
    @SerializedName(UUID) @Expose val uuid: String,
    @SerializedName(CONTENT) @Expose val content: String,
    @SerializedName(IS_ANONYMOUS) @Expose val isAnonymous: Boolean,
    @SerializedName(USER_ID) @Expose val userId: Int,
    @SerializedName(REPORT_UUID) @Expose val reportUuid: String
) {
    companion object {
        const val UUID = "uuid"
        const val CONTENT = "content"
        const val IS_ANONYMOUS = "is_anonymous"
        const val USER_ID = "user_id"
        const val REPORT_UUID = "report_uuid"
    }
}

data class CommentRequest(
    @SerializedName(REPORT_COMMENT) @Expose val reportComment: ReportCommentRequest
) {
    companion object {
        const val REPORT_COMMENT = "report_comment"
    }
}
