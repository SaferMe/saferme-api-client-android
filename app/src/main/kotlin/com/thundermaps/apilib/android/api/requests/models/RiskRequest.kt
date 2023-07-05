package com.thundermaps.apilib.android.api.requests.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RiskRequest(
    @Expose val uuid: String? = null,
    @Expose val eliminated: Boolean? = null,
    @SerializedName(LIKELIHOOD_KEY) @Expose val likelihoodKey: String? = null,
    @SerializedName(SEVERITY_KEY) @Expose val severityKey: String? = null,
    @Expose val comment: String? = null,
    @SerializedName(CREATED_AT) @Expose val createdAt: String? = null
) {
    companion object {
        const val LIKELIHOOD_KEY = "likelihood_key"
        const val SEVERITY_KEY = "severity_key"
        const val CREATED_AT = "created_at"
    }
}
