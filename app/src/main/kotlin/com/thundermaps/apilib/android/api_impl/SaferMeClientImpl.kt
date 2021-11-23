package com.thundermaps.apilib.android.api_impl

import com.thundermaps.apilib.android.api.SaferMeClient
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.resources.DeviceInfoLogsResource
import com.thundermaps.apilib.android.api.resources.ReportResource
import com.thundermaps.apilib.android.api.resources.TaskResource
import com.thundermaps.apilib.android.api.resources.TracedContactsResource
import com.thundermaps.apilib.android.api_impl.resources.DeviceInfoLogsImpl
import com.thundermaps.apilib.android.api_impl.resources.ReportImpl
import com.thundermaps.apilib.android.api_impl.resources.TasksImpl
import com.thundermaps.apilib.android.api_impl.resources.TracedContactsImpl
import javax.inject.Inject
import javax.inject.Singleton

class SaferMeClientImpl @Inject constructor(private val androidClient: AndroidClient): SaferMeClient {
    override val taskResource: TaskResource = TasksImpl(androidClient)
    
    override val reportResource: ReportResource = ReportImpl(androidClient)
    
    override val tracedContacts: TracedContactsResource = TracedContactsImpl(androidClient)
    
    override val deviceInfoLogs: DeviceInfoLogsResource = DeviceInfoLogsImpl(androidClient)
    
    override fun defaultParams(): RequestParameters = RequestParameters(
        customRequestHeaders = HashMap(),
        credentials = null,
        host = DEFAULT_API_ENDPOINT,
        port = null,
        api_version = 4
    )
    
    companion object {
        //Constants
        private const val DEFAULT_API_ENDPOINT = "public-api.thundermaps.com"
    }
}
