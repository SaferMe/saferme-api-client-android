package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.thundermaps.apilib.android.api.ExcludeFromJacocoGeneratedReport
import com.thundermaps.apilib.android.api.resources.SaferMeDatum

@ExcludeFromJacocoGeneratedReport
data class ReportStateChange(
    @Expose val id: Int,
    @Expose @SerializedName("created_at") val createdAt: String,
    @Expose @SerializedName("new_report_state") val newReportState: ReportState,
    @Expose @SerializedName("previous_report_state") val previousReportState: ReportState,
    @Expose @SerializedName("report_id") val reportId: Int,
    @Expose @SerializedName("user_id") val userId: Int,
    @Expose @SerializedName("report_uuid") val reportUUID: String? = null
) : SaferMeDatum
