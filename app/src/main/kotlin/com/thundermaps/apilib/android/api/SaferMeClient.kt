package com.thundermaps.apilib.android.api

import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.resources.DeviceInfoLogsResource
import com.thundermaps.apilib.android.api.resources.ReportResource
import com.thundermaps.apilib.android.api.resources.TaskResource
import com.thundermaps.apilib.android.api.resources.TracedContactsResource

abstract class SaferMeClient {
    abstract val Tasks: TaskResource
    abstract val Reports: ReportResource
    abstract fun defaultParams(): RequestParameters
    abstract val TracedContacts: TracedContactsResource
    abstract val DeviceInfoLogs: DeviceInfoLogsResource
}
