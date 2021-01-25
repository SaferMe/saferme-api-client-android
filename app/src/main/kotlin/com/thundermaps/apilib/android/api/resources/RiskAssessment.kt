package com.thundermaps.apilib.android.api.resources

import com.google.gson.annotations.Expose

data class RiskAssessment(
    @Expose
    val id: Int = 0,

    @Expose
    val user_image: String? = null,

    @Expose
    val user_short_name: String? = null,

    @Expose
    val eliminated: Boolean? = null,

    @Expose
    val likelihood: Likelihood? = null,

    @Expose
    val severity: Severity? = null,

    @Expose
    val risk_level: RiskLevel? = null,

    @Expose
    val comment: String? = null,

    @Expose
    val created_at: String? = null
)

data class Severity(
    @Expose
    val key: String = "",

    @Expose
    val label: String = "",

    @Expose
    val value: Int = 0
)

data class Likelihood(
    @Expose
    val key: String = "",

    @Expose
    val label: String = "",

    @Expose
    val value: Int = 0
)
