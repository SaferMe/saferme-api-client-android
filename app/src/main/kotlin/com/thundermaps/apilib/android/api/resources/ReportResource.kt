package com.thundermaps.apilib.android.api.resources

import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.responses.models.Report
import com.thundermaps.apilib.android.api.responses.models.Result

interface ReportResource : SaferMeResource<Report>,
    Creatable<Report>, Readable<Report>, Indexable<Report>, Updatable<Report>, Deletable<Report> {

    suspend fun getReportsDeletedAfter(
        parameters: RequestParameters
    ): Result<DeletedResourceList>
}
