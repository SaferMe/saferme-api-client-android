package com.thundermaps.apilib.android.impl.resources

import com.thundermaps.apilib.android.api.resources.ReportCommentsResource
import com.thundermaps.apilib.android.api.responses.models.ReportComment
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.impl.AndroidClient
import com.thundermaps.apilib.android.impl.apiRequest
import javax.inject.Inject

class ReportCommentsResourceImpl @Inject constructor(
    private val androidClient: AndroidClient
) : ReportCommentsResource {
    override suspend fun getReportComments(
        reportId: String?,
        teamId: String?,
        userId: String?,
        updatedAfter: String?
    ): Result<List<ReportComment>> {
        val parameters = mutableMapOf(
            "fields" to FIELDS.joinToString(",")
        ).apply {
            if (reportId != null) {
                this["report_id"] = reportId
            }

            if (teamId != null) {
                this["team_id"] = teamId
            }

            if (userId != null) {
                this["user_id"] = userId
            }

            if (updatedAfter != null) {
                this["updated_after"] = updatedAfter
            }
        }

        val (client, requestBuilder) = androidClient.buildRequest(
            parameters,
            PATH
        )
        return client.apiRequest(requestBuilder)
    }

    companion object {
        const val PATH = "report_comments"
        val FIELDS = arrayOf("-created_at", "report_uuid")
    }
}
