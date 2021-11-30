package com.thundermaps.apilib.android.impl.resources

import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.requests.SaferMeApiResult
import com.thundermaps.apilib.android.api.resources.DeviceInfoLog
import com.thundermaps.apilib.android.api.resources.DeviceInfoLogsResource
import com.thundermaps.apilib.android.impl.AndroidClient

class DeviceInfoLogsImpl(val api: AndroidClient) : DeviceInfoLogsResource {

    override suspend fun create(
        parameters: RequestParameters,
        item: DeviceInfoLog,
        success: (SaferMeApiResult<DeviceInfoLog>) -> Unit,
        failure: (Exception) -> Unit
    ) {
        StandardMethods.create(
            api = api, path = "device_info_logs", parameters = parameters, item = item, success = success, failure = failure
        )
    }
}
