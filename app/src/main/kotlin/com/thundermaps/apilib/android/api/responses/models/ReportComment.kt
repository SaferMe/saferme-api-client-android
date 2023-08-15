package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ReportComment(
    @Expose @SerializedName(UUID) val uuid: String,
    @Expose @SerializedName(ID) val id: Int = 0,
    @Expose @SerializedName(CONTENT) val content: String,
    @Expose @SerializedName(CREATED_AT) val createdAt: String,
    @Expose @SerializedName(IS_ANONYMOUS) val isAnonymous: Boolean,
    @Expose @SerializedName(REPORT_ID) val reportId: Int,
    @Expose @SerializedName(REPORT_UUID) val reportUuid: String,
    @Expose @SerializedName(USER_ID) val userId: Int
) {
    companion object {
        const val UUID = "uuid"
        const val ID = "id"
        const val CONTENT = "content"
        const val CREATED_AT = "created_at"
        const val IS_ANONYMOUS = "is_anonymous"
        const val REPORT_ID = "report_id"
        const val REPORT_UUID = "report_uuid"
        const val USER_ID = "user_id"
    }
}
