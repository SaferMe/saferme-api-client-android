package com.thundermaps.apilib.android.api.resources

import com.thundermaps.apilib.android.api.com.thundermaps.env.Environment
import com.thundermaps.apilib.android.api.requests.models.SessionBody
import com.thundermaps.apilib.android.api.responses.models.ResponseError
import com.thundermaps.apilib.android.api.responses.models.Sessions

interface SessionsResource {
    fun updateEnvironment(environment: Environment)
    suspend fun login(body: SessionBody, success: (data: Sessions) -> Unit, error: (data: ResponseError) -> Unit)
}
