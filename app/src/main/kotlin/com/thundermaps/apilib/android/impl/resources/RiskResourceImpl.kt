package com.thundermaps.apilib.android.impl.resources

import com.thundermaps.apilib.android.api.resources.RiskResource
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.RiskAssessment
import com.thundermaps.apilib.android.impl.AndroidClient
import com.thundermaps.apilib.android.impl.apiRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RiskResourceImpl @Inject constructor(
    private val androidClient: AndroidClient
) : RiskResource {
    override suspend fun getRisks(
        reportId: String?,
        teamId: String?,
        updatedAfter: String?
    ): Result<List<RiskAssessment>> {
        val parameters = mutableMapOf(
            "fields" to FIELDS.joinToString(",")
        ).apply {
            if (reportId != null) {
                this["report_id"] = reportId
            }
            if (teamId != null) {
                this["team_id"] = teamId
            }
            if (updatedAfter != null) {
                this["updated_after"] = updatedAfter
            }
        }

        val (client, requestBuilder) = androidClient.buildRequest(parameters, PATH)
        return client.apiRequest(requestBuilder)
    }

    companion object {
        const val PATH = "risk_assessments"
        val FIELDS = arrayOf("report_uuid", "team_id", "created_at")
    }
}
