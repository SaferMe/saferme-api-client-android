package com.thundermaps.apilib.android.api.resources

import com.thundermaps.apilib.android.api.requests.models.CommentRequest
import com.thundermaps.apilib.android.api.responses.models.ReportComment
import com.thundermaps.apilib.android.api.responses.models.Result

interface ReportCommentsResource {
    suspend fun getReportComments(
        reportUuid: String? = null,
        teamId: String? = null,
        userId: String? = null,
        updatedAfter: String? = null
    ): Result<List<ReportComment>>

    suspend fun createComment(reportId: String, comment: CommentRequest): Result<ReportComment>
}
