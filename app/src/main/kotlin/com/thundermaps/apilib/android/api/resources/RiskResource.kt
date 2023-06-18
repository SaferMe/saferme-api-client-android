package com.thundermaps.apilib.android.api.resources

import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.RiskAssessment

interface RiskResource {
    suspend fun getRisks(
        reportId: String? = null,
        teamId: String? = null,
        updatedAfter: String? = null
    ): Result<List<RiskAssessment>>
}
