package com.thundermaps.apilib.android.api.resources

import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.responses.models.Notification
import com.thundermaps.apilib.android.api.responses.models.Result

interface NotificationResource {
    suspend fun getNotifications(parameters: RequestParameters): Result<List<Notification>>
}
