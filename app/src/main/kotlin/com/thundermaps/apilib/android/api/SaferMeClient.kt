package com.thundermaps.apilib.android.api

import com.thundermaps.apilib.android.api.com.thundermaps.env.EnvironmentManager
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.resources.DeviceInfoLogsResource
import com.thundermaps.apilib.android.api.resources.ReportResource
import com.thundermaps.apilib.android.api.resources.TaskResource
import com.thundermaps.apilib.android.api.resources.TeamResource
import com.thundermaps.apilib.android.api.resources.TracedContactsResource

interface SaferMeClient {
    val taskResource: TaskResource
    val reportResource: ReportResource
    fun defaultParams(): RequestParameters
    val tracedContacts: TracedContactsResource
    val deviceInfoLogs: DeviceInfoLogsResource
    val environmentManager: EnvironmentManager

    val teamResource: TeamResource
}
