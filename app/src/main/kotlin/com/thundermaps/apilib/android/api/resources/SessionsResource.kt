package com.thundermaps.apilib.android.api.resources

import com.thundermaps.apilib.android.api.requests.models.EmailBody
import com.thundermaps.apilib.android.api.requests.models.SessionBody
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.Sessions
import com.thundermaps.apilib.android.api.responses.models.SsoDetails
import com.thundermaps.apilib.android.api.responses.models.SsoSessions

interface SessionsResource {
    fun isStaging(): Boolean
    suspend fun login(body: SessionBody, applicationId: String): Result<Sessions>
    suspend fun requestPassword(body: EmailBody, applicationId: String): Result<String>
    suspend fun getSsoDetails(ssoId: String, applicationId: String): Result<SsoDetails>
    suspend fun getSsoSessions(code: String, applicationId: String, ssoDetails: SsoDetails): Result<SsoSessions>
}
