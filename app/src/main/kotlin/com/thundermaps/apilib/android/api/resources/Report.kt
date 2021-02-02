package com.thundermaps.apilib.android.api.resources

import com.google.gson.JsonArray
import com.google.gson.annotations.Expose

data class Report(
    @Expose
    val id: Int = 0,

    @Expose
    val title: String? = null,

    @Expose
    val account_id: Int? = null,

    @Expose
    val address: String? = null,

    @Expose
    val categories_title: String? = null,

    @Expose
    val comment_count: Int? = null,

    @Expose
    val description: String? = null,

    @Expose
    val is_anonymous: Boolean? = false,

    @Expose
    val is_hazard: Boolean? = false,

    @Expose
    val iso_created_at: String? = null,

    @Expose
    val location: Location? = null,

    @Expose
    val report_state_id: Int? = null,

    @Expose
    val risk_assessment: RiskAssessment? = null,

    @Expose
    val risk_control_editable_by: Boolean? = null,

    @Expose
    val risk_control_id: String? = null,

    @Expose
    val risk_level: RiskLevel? = null,

    @Expose
    val risk_matrix_config: RiskMatrixConfig? = null,

    @Expose
    val shape_id: Int? = null,

    @Expose
    val updated_at: String? = null,

    @Expose
    val user_id: Int? = null,

    @Expose
    val viewer_count: Int? = 0,

    @Expose
    val form_fields: JsonArray? = null,

    @Expose
    val hidden_fields: JsonArray? = null
) : SaferMeDatum {
    override fun toString(): String {
        return super.toString()
    }
}

interface ReportResource : SaferMeResource<Report>,
    Creatable<Report>, Readable<Report>, Indexable<Report>, Updatable<Report>, Deletable<Report>
