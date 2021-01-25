package com.thundermaps.apilib.android.api.resources

import com.google.gson.annotations.Expose

data class RiskMatrixConfig(
    @Expose
    val id: Int = 0,

    @Expose
    val likelihoods: List<Likelihood> = ArrayList(),

    @Expose
    val severities: List<Severity> = ArrayList(),

    @Expose
    val risk_levels: List<RiskLevel> = ArrayList()
)
