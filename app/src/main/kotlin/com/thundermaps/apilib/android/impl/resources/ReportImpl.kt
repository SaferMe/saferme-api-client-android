package com.thundermaps.apilib.android.impl.resources

import androidx.annotation.VisibleForTesting
import com.google.gson.Gson
import com.thundermaps.apilib.android.api.com.thundermaps.isInternetAvailable
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.requests.SaferMeApiResult
import com.thundermaps.apilib.android.api.resources.DeletedResourcelList
import com.thundermaps.apilib.android.api.resources.ReportResource
import com.thundermaps.apilib.android.api.responses.models.Report
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.ResultHandler
import com.thundermaps.apilib.android.impl.AndroidClient
import io.ktor.client.call.HttpClientCall
import io.ktor.client.call.call
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.url
import io.ktor.http.HttpMethod
import io.ktor.util.KtorExperimentalAPI
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@KtorExperimentalAPI
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
        Timber.e("Custom Parameters: ${parameters.customRequestHeaders}")
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

    override suspend fun getReportsDeletedAfter(parameters: RequestParameters): Result<DeletedResourcelList> {
        if (!parameters.host.isInternetAvailable()) {
            return resultHandler.handleException(UnknownHostException())
        }
        val call = getReportsCallDeletedAfter(parameters)

        return resultHandler.processResult(call, gson)
    }

    private suspend fun getReportsCallDeletedAfter(
        parameters: RequestParameters
    ): HttpClientCall {
        val (client, requestBuilder) = api.client(parameters)
        val call = client.call(HttpRequestBuilder().takeFrom(requestBuilder).apply {
            method = HttpMethod.Get
            url(AndroidClient.baseUrlBuilder(parameters).apply {
                val extensionParams = parameters.parameters?.toUriParameters()
                encodedPath =
                    extensionParams?.let { "${encodedPath}deleted_resources?$it" }
                        ?: "${encodedPath}deleted_resources?type=report"
            }.build())
        })
        return call
    }

    companion object {
        private const val REPORT_PATH = "reports"

        @VisibleForTesting
        const val FIELDS_PARAM =
            "fields=categories_title,category_id,comment_count,viewer_count,form_fields,hidden_fields,is_hazard,risk_level,risk_assessment,risk_matrix_config,risk_control_id,risk_control_editable_by,report_state,user_short_name,appearance,assignee_id,assignment_due_at,assignee"
    }
}
