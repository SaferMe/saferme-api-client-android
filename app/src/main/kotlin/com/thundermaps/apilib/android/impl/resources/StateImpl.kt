package com.thundermaps.apilib.android.impl.resources

import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.requests.SaferMeApiResult
import com.thundermaps.apilib.android.api.resources.StateResource
import com.thundermaps.apilib.android.api.responses.models.ReportState
import com.thundermaps.apilib.android.impl.AndroidClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StateImpl @Inject constructor(val api: AndroidClient) : StateResource {
    override suspend fun read(
        parameters: RequestParameters,
        item: ReportState,
        success: (SaferMeApiResult<ReportState>) -> Unit,
        failure: (Exception) -> Unit
    ) {
        val stateId = item.id
        if (stateId == 0) {
            failure(Exception("State id is invalid"))
        } else {
            StandardMethods.read(
                api = api,
                path = "$STATE_PATH/$stateId",
                parameters = parameters,
                success = success,
                failure = failure
            )
        }
    }

    override suspend fun index(
        parameters: RequestParameters,
        success: (SaferMeApiResult<List<ReportState>>) -> Unit,
        failure: (Exception) -> Unit
    ) {
        val extensionParams = parameters.parameters?.toUriParameters()
        if (extensionParams == null) {
            failure(Exception("Missing information (team_id || channel_id) to get the report states. "))
        } else {
            StandardMethods.index(
                api = api,
                path = "$STATE_PATH?$extensionParams",
                parameters = parameters,
                success = success,
                failure = failure
            )
        }
    }

    companion object {
        const val STATE_PATH = "report_states"
    }
}
