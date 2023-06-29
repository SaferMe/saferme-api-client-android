package com.thundermaps.apilib.android.impl.resources

import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.requests.SaferMeApiResult
import com.thundermaps.apilib.android.api.resources.ReportStateChangeResource
import com.thundermaps.apilib.android.api.responses.models.ReportStateChange
import com.thundermaps.apilib.android.impl.AndroidClient
import javax.inject.Inject

class ReportStateChangeResourceImpl @Inject constructor(
    val api: AndroidClient
) : ReportStateChangeResource {
    override suspend fun read(
        parameters: RequestParameters,
        item: ReportStateChange,
        success: (SaferMeApiResult<ReportStateChange>) -> Unit,
        failure: (Exception) -> Unit
    ) {
        StandardMethods.read(
            api = api,
            path = ENDPOINT,
            parameters = parameters,
            success = success,
            failure = failure
        )
    }

    override suspend fun index(
        parameters: RequestParameters,
        success: (SaferMeApiResult<List<ReportStateChange>>) -> Unit,
        failure: (Exception) -> Unit
    ) {
        val extensionParameters = parameters.parameters?.toUriParameters()
        val apiPath = extensionParameters?.let { "$ENDPOINT?$FILTER_SYNCABLE&$it" } ?: "$ENDPOINT?$FILTER_SYNCABLE"
        StandardMethods.index(
            api = api,
            path = apiPath,
            parameters = parameters,
            success = success,
            failure = failure
        )
    }

    companion object {
        private const val ENDPOINT = "report_state_changes"

        const val FILTER_SYNCABLE = "preset_filter=syncable_reports"
    }
}
