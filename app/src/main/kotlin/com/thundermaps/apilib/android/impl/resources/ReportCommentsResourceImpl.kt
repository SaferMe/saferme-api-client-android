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
        reportId: Int?,
        userId: Int?
    ): Result<List<ReportComment>> {
        val parameters = mutableMapOf(
            "fields" to FIELDS.joinToString(",")
        ).apply {
            if (reportId != null) {
                this["report_id"] = reportId.toString()
            }

            if (userId != null) {
                this["user_id"] = userId.toString()
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
        val FIELDS = arrayOf("report_id", "user_id")
    }
}
