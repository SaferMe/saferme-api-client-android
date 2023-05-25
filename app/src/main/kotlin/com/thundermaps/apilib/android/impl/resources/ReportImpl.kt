package com.thundermaps.apilib.android.impl.resources

import androidx.annotation.VisibleForTesting
import apiRequest
import com.google.gson.Gson
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.requests.SaferMeApiResult
import com.thundermaps.apilib.android.api.resources.DeletedResourceList
import com.thundermaps.apilib.android.api.resources.ReportResource
import com.thundermaps.apilib.android.api.responses.models.Report
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.ResultHandler
import com.thundermaps.apilib.android.impl.AndroidClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportImpl @Inject constructor(
    val api: AndroidClient,
    private val resultHandler: ResultHandler,
    private val gson: Gson
) : ReportResource {
    override suspend fun create(
        parameters: RequestParameters,
        item: Report,
        success: (SaferMeApiResult<Report>) -> Unit,
        failure: (Exception) -> Unit
    ) {
        StandardMethods.create(
            api = api,
            path = REPORT_PATH,
            parameters = parameters,
            item = item,
            success = success,
            failure = failure
        )
    }

    override suspend fun read(
        parameters: RequestParameters,
        item: Report,
        success: (SaferMeApiResult<Report>) -> Unit,
        failure: (Exception) -> Unit
    ) {
        StandardMethods.read(
            api = api,
            path = "$REPORT_PATH/${item.uuid}?$FIELDS_PARAM",
            parameters = parameters,
            success = success,
            failure = failure
        )
    }

    override suspend fun update(
        parameters: RequestParameters,
        item: Report,
        success: (SaferMeApiResult<Report>) -> Unit,
        failure: (Exception) -> Unit
    ) {
        StandardMethods.update(
            api = api,
            path = "$REPORT_PATH/${item.uuid}?$FIELDS_PARAM",
            parameters = parameters,
            item = item,
            success = success,
            failure = failure
        )
    }

    override suspend fun index(
        parameters: RequestParameters,
        success: (SaferMeApiResult<List<Report>>) -> Unit,
        failure: (Exception) -> Unit
    ) {
        val extensionParams = parameters.parameters?.toUriParameters()
        val path = extensionParams?.let { "$REPORT_PATH?$FIELDS_PARAM&$it" }
            ?: "$REPORT_PATH?$FIELDS_PARAM"
        StandardMethods.index(
            api = api, path = path, parameters = parameters, success = success, failure = failure
        )
    }

    override suspend fun delete(
        parameters: RequestParameters,
        identifier: Report,
        success: (SaferMeApiResult<Report>) -> Unit,
        failure: (Exception) -> Unit

    ) {
        StandardMethods.delete(
            api = api,
            path = "$REPORT_PATH/${identifier.uuid}",
            parameters = parameters,
            success = success,
            failure = failure,
            item = identifier
        )
    }

    override suspend fun getReportsDeletedAfter(parameters: RequestParameters): Result<DeletedResourceList> {
        val params = parameters.copy(parameters = parameters.parameters ?: mapOf("type" to "report"))
        val (client, requestBuilder) = api.build(params, DELETED_REPORT_PATH)
        return client.apiRequest(requestBuilder)
    }

    companion object {
        internal const val REPORT_PATH = "reports"
        internal const val DELETED_REPORT_PATH = "deleted_resources"
        internal const val DELETED_AFTER = "type=report"

        @VisibleForTesting
        const val FIELDS_PARAM =
            "fields=categories_title,category_id,comment_count,viewer_count,form_fields,hidden_fields,is_hazard,risk_level,risk_assessment,risk_matrix_config,risk_control_id,risk_control_editable_by,report_state,user_short_name,appearance,assignee_id,assignment_due_at,assignee"
    }
}
