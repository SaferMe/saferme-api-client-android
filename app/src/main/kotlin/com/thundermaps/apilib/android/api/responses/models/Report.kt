package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.JsonArray
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.thundermaps.apilib.android.api.resources.Location
import com.thundermaps.apilib.android.api.resources.SaferMeDatum

data class Report(
    @Expose val uuid: String = "",
    @Expose val id: Int? = 0,
    @Expose val title: String? = null,
    @SerializedName(ACCOUNT_ID) @Expose val accountId: Int? = null,
    @Expose val address: String? = null,
    @SerializedName(CATEGORIES_TITLE) @Expose val categoriesTitle: String? = null,
    @SerializedName(COMMENT_COUNT) @Expose val commentCount: Int? = null,
    @Expose val description: String? = null,
    @SerializedName(IS_ANONYMOUS) @Expose val isAnonymous: Boolean? = false,
    @SerializedName(IS_HAZARD) @Expose val isHazard: Boolean? = false,
    @SerializedName(ISO_CREATED_AT) @Expose val isoCreatedAt: String? = null,
    @Expose val location: Location? = null,
    @SerializedName(REPORT_STATE) @Expose val reportState: ReportState? = null,
    @SerializedName(REPORT_STATE_ID) @Expose val reportStateId: Int? = null,
    @SerializedName(RISK_ASSESSMENT) @Expose val riskAssessment: RiskAssessment? = null,
    @SerializedName(RISK_CONTROL_EDITABLE_BY) @Expose val riskControlEditableBy: Boolean? = null,
    @SerializedName(RISK_CONTROL_ID) @Expose val riskControlId: String? = null,
    @SerializedName(RISK_LEVEL) @Expose val riskLevel: RiskLevel? = null,
    @SerializedName(RISK_MATRIX_CONFIG) @Expose val riskMatrixConfig: RiskMatrixConfig? = null,
    @SerializedName(SHAPE_ID) @Expose val shapeId: Int? = null,
    @SerializedName(UPDATED_AT) @Expose val updatedAt: String? = null,
    @SerializedName(USER_ID) @Expose val userId: Int? = null,
    @SerializedName(VIEWER_COUNT) @Expose val viewerCount: Int? = 0,
    @SerializedName(FORM_FIELDS) @Expose val formFields: JsonArray? = null,
    @SerializedName(HIDDEN_FIELDS) @Expose val hiddenFields: JsonArray? = null
) : SaferMeDatum {
    override fun toString(): String {
        return super.toString()
    }

    companion object {
        const val ACCOUNT_ID = "account_id"
        const val CATEGORIES_TITLE = "categories_title"
        const val COMMENT_COUNT = "comment_count"
        const val IS_ANONYMOUS = "is_anonymous"
        const val IS_HAZARD = "is_hazard"
        const val ISO_CREATED_AT = "iso_creared_at"
        const val REPORT_STATE = "report_state"
        const val REPORT_STATE_ID = "report_state_id"
        const val RISK_ASSESSMENT = "risk_assessment"
        const val RISK_CONTROL_EDITABLE_BY = "risk_control_editable_by"
        const val RISK_CONTROL_ID = "risk_control_id"
        const val RISK_LEVEL = "risk_level"
        const val RISK_MATRIX_CONFIG = "risk_matrix_config"
        const val SHAPE_ID = "shape_id"
        const val UPDATED_AT = "updated_at"
        const val USER_ID = "user_id"
        const val VIEWER_COUNT = "viewer_count"
        const val FORM_FIELDS = "form_fields"
        const val MODIFIED_DATE = "modified_date"
        const val HIDDEN_FIELDS = "hidden_fields"
    }
}