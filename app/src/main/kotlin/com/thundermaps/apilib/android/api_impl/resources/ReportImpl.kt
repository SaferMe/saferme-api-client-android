package com.thundermaps.apilib.android.api_impl.resources

import android.util.Log
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.requests.SaferMeApiResult
import com.thundermaps.apilib.android.api.resources.Report
import com.thundermaps.apilib.android.api.resources.ReportResource
import com.thundermaps.apilib.android.api.resources.Task
import com.thundermaps.apilib.android.api_impl.AndroidClient
import java.lang.IllegalArgumentException

class ReportImpl( val api: AndroidClient): ReportResource {



    override suspend fun create(
        parameters: RequestParameters,
        item: Report,
        success: (SaferMeApiResult<Report>) -> Unit,
        failure: (Exception) -> Unit
    ) {
        StandardMethods.create(
            api = api, path =  "reports", parameters = parameters, item = item, success = success, failure = failure
        )
    }
    override suspend fun read(
        parameters: RequestParameters,
        item: Report,
        success: (SaferMeApiResult<Report>) -> Unit,
        failure: (Exception) -> Unit
    ) {
        StandardMethods.read(
            api = api, path = "reports/${item.uuid}?fields=categories_title,comment_count,viewer_count,form_fields,hidden_fields,is_hazard,risk_level,risk_assessment,risk_matrix_config,risk_control_id,risk_control_editable_by", parameters = parameters, success = success, failure = failure
        )
    }

    override suspend fun update(
        parameters: RequestParameters,
        item: Report,
        success: (SaferMeApiResult<Report>) -> Unit,
        failure: (Exception) -> Unit
    ) {
        StandardMethods.update(
            api = api, path=  "reports/${item.uuid}?fields=categories_title,comment_count,viewer_count,form_fields,hidden_fields,is_hazard,risk_level,risk_assessment,risk_matrix_config,risk_control_id,risk_control_editable_by", parameters = parameters, item = item, success = success, failure = failure
        )
    }


    override suspend fun index(
        parameters: RequestParameters,
        success: (SaferMeApiResult<List<Report>>) -> Unit,
        failure: (Exception) -> Unit
    ) {
        class TaskListToken : TypeToken<List<Report>>()
        StandardMethods.index(
            api = api, path=  "reports?fields=categories_title,comment_count,viewer_count,form_fields,hidden_fields,is_hazard,risk_level,risk_assessment,risk_matrix_config,risk_control_id,risk_control_editable_by", listType = TaskListToken(), parameters = parameters, success = success, failure = failure
        )
    }



    override suspend fun delete(
        parameters: RequestParameters,
        identifier: Report,
        success: (SaferMeApiResult<Report>) -> Unit,
        failure: (Exception) -> Unit

    ) {
        StandardMethods.delete(
            api = api, path = "reports/${identifier.uuid}", parameters = parameters, success = success, failure = failure, item = identifier
        )
    }

}