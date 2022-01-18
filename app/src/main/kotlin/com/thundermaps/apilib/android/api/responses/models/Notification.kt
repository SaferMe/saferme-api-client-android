package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Notification(
    @Expose val archived: Boolean = false,
    @SerializedName("archived_at") val archivedAt: String? = null,
    @SerializedName("created_at") val createdAt: String,
    val id: Int,
    val read: Boolean = false,
    @SerializedName("report_id") val reportId: Int,
    @SerializedName("report_uuid") val reportUuid: String,
    @SerializedName("user_id") val userId: Int
)
