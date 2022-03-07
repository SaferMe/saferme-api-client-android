package com.thundermaps.apilib.android.api

import com.thundermaps.apilib.android.api.com.thundermaps.env.EnvironmentManager
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.resources.BrandResource
import com.thundermaps.apilib.android.api.resources.CategoryResource
import com.thundermaps.apilib.android.api.resources.ChannelResource
import com.thundermaps.apilib.android.api.resources.DeviceInfoLogsResource
import com.thundermaps.apilib.android.api.resources.FormResource
import com.thundermaps.apilib.android.api.resources.MeResource
import com.thundermaps.apilib.android.api.resources.ReportResource
import com.thundermaps.apilib.android.api.resources.SessionsResource
import com.thundermaps.apilib.android.api.resources.ShapeResource
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

    val sessionsResource: SessionsResource
    val teamResource: TeamResource
    val meResource: MeResource
    val channelResource: ChannelResource
    val brandResource: BrandResource
    val shapeResource: ShapeResource
    val categoryResource: CategoryResource
    val formResource: FormResource
}
