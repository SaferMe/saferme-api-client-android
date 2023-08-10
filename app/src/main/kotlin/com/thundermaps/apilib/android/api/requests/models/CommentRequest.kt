package com.thundermaps.apilib.android.api.requests.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ReportCommentRequest(
    @SerializedName("uuid") @Expose val uuid: String? = null,
    @SerializedName("content") @Expose val content: String? = null,
    @SerializedName("is_anonymous") @Expose val isAnonymous: Boolean? = null,
    @SerializedName("user_id") @Expose val userId: Int? = null,
    @SerializedName("report_uuid") @Expose val reportUuid: String? = null
)

data class CommentRequest(
    @SerializedName("report_comment") @Expose val reportComment: ReportCommentRequest? = null
)
