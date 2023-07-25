package com.thundermaps.apilib.android.api.resources

import com.thundermaps.apilib.android.api.responses.models.ReportComment
import com.thundermaps.apilib.android.api.responses.models.Result

interface ReportCommentsResource {
    suspend fun getReportComments(
        reportId: Int? = null,
        userId: Int? = null
    ): Result<List<ReportComment>>
}
