package com.thundermaps.apilib.android.api.resources

import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.RiskAssessment

interface RiskResource {
    suspend fun getRisks(
        parameters: Map<String, String>? = null
    ): Result<List<RiskAssessment>>
}
