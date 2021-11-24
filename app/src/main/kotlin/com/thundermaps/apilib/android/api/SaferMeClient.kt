package com.thundermaps.apilib.android.api

import com.thundermaps.apilib.android.api.com.thundermaps.env.Environment
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.requests.models.SessionBody
import com.thundermaps.apilib.android.api.resources.DeviceInfoLogsResource
import com.thundermaps.apilib.android.api.resources.ReportResource
import com.thundermaps.apilib.android.api.resources.TaskResource
import com.thundermaps.apilib.android.api.resources.TracedContactsResource
import com.thundermaps.apilib.android.api.responses.models.ResponseError
import com.thundermaps.apilib.android.api.responses.models.Sessions

interface SaferMeClient {
    val taskResource: TaskResource
    val reportResource: ReportResource
    fun defaultParams(): RequestParameters
    val tracedContacts: TracedContactsResource
    val deviceInfoLogs: DeviceInfoLogsResource
    fun updateEnvironment(environment: Environment)
    suspend fun login(
        sessionBody: SessionBody,
        success: (data: Sessions) -> Unit,
        error: (data: ResponseError) -> Unit
    )
}
