package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RiskAssessment(
    @Expose val id: Int = 0,
    @SerializedName(REPORT_UUID) @Expose val reportUuid: String? = null,
    @SerializedName(USER_ID) @Expose val userId: Int? = null,
    @SerializedName(USER_IMAGE) @Expose val userImage: String? = null,
    @SerializedName(USER_SHORT_NAME) @Expose val userShortName: String? = null,
    @Expose val eliminated: Boolean? = null,
    @Expose val likelihood: Likelihood? = null,
    @Expose val severity: Severity? = null,
    @SerializedName(RISK_LEVEL) @Expose val riskLevel: RiskLevel? = null,
    @Expose val comment: String? = null,
    @SerializedName(CREATED_AT) @Expose val createdAt: String? = null
) {
    companion object {
        const val USER_IMAGE = "user_image"
        const val USER_SHORT_NAME = "user_short_name"
        const val RISK_LEVEL = "risk_level"
        const val CREATED_AT = "created_at"
        const val REPORT_UUID = "report_uuid"
        const val USER_ID = "user_id"
    }
}

data class Severity(
    @Expose val key: String = "",
    @Expose val label: String = "",
    @Expose val value: Int = 0
)

data class Likelihood(
    @Expose val key: String = "",
    @Expose val label: String = "",
    @Expose val value: Int = 0
)

data class RiskLevel(
    @Expose val limit: Int = 0,
    @Expose val score: Int = 0,
    @Expose val label: String = "",
    @Expose val color: String = ""
)
