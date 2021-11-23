package com.thundermaps.apilib.android.api.resources

import com.thundermaps.apilib.android.api.requests.models.SessionBody
import com.thundermaps.apilib.android.api.responses.models.ResponseError
import com.thundermaps.apilib.android.api.responses.models.Sessions

interface SessionsResource {
    suspend fun login(body: SessionBody, success: (data: Sessions) -> Unit, error: (data: ResponseError) -> Unit)
}
