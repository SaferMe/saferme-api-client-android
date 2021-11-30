package com.thundermaps.apilib.android.api.resources

import com.thundermaps.apilib.android.api.requests.models.SessionBody
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.Sessions

interface SessionsResource {
    fun isStaging(): Boolean
    suspend fun login(body: SessionBody, applicationId: String): Result<Sessions>
}
