package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RiskMatrixConfig(
    @Expose val id: Int? = 0,
    @Expose val likelihoods: List<Likelihood> = ArrayList(),
    @Expose val severities: List<Severity> = ArrayList(),
    @SerializedName("risk_levels") @Expose val riskLevels: List<RiskLevel> = ArrayList()
)
