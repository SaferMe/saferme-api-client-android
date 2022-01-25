package com.thundermaps.apilib.android.impl.resources

import androidx.annotation.VisibleForTesting
import com.google.gson.Gson
import com.thundermaps.apilib.android.api.com.thundermaps.env.EnvironmentManager
import com.thundermaps.apilib.android.api.com.thundermaps.isInternetAvailable
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.requests.models.EmailBody
import com.thundermaps.apilib.android.api.requests.models.SessionBody
import com.thundermaps.apilib.android.api.resources.SessionsResource
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.ResultHandler
import com.thundermaps.apilib.android.api.responses.models.Sessions
import com.thundermaps.apilib.android.api.responses.models.SsoDetails
import com.thundermaps.apilib.android.api.responses.models.SsoSessions
import com.thundermaps.apilib.android.impl.AndroidClient
import com.thundermaps.apilib.android.impl.HeaderType
import io.ktor.client.call.HttpClientCall
import io.ktor.client.call.call
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.util.KtorExperimentalAPI
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@KtorExperimentalAPI
@Singleton
class SessionsImpl @Inject constructor(
    private val androidClient: AndroidClient,
    private val environmentManager: EnvironmentManager,
    private val resultHandler: ResultHandler,
    private val gson: Gson
) : SessionsResource {
    override fun isStaging(): Boolean = environmentManager.isStaging()
    private fun createParameters(host: String, applicationId: String, apiVersion: Int) =
        RequestParameters(
            customRequestHeaders = hashMapOf(
                HeaderType.xAppId to applicationId,
                HttpHeaders.Accept to APPLICATION_JSON,
                HttpHeaders.ContentType to APPLICATION_JSON
            ),
            credentials = null,
            host = host,
            api_version = apiVersion
        )

    override suspend fun login(
        body: SessionBody,
        applicationId: String
    ): Result<Sessions> {
        if (!environmentManager.environment.servers.first().isInternetAvailable()) {
            return resultHandler.handleException(UnknownHostException())
        }
        val call = requestHandler(body, applicationId, LOGIN_PATH)
        return resultHandler.processResult(call, gson)
    }

    override suspend fun requestPassword(body: EmailBody, applicationId: String): Result<String> {
        if (!environmentManager.environment.servers.first().isInternetAvailable()) {
            return resultHandler.handleException(UnknownHostException())
        }
        val call = requestHandler(body, applicationId, RESET_PASSWORD_PATH)
        return resultHandler.processResult<EmailBody>(call, gson).convert { it.email }
    }

    override suspend fun getSsoDetails(ssoId: String, applicationId: String): Result<SsoDetails> {
        if (!environmentManager.environment.servers.first().isInternetAvailable()) {
            return resultHandler.handleException(UnknownHostException())
        }
        val call = requestHandler(bodyParameters = null, applicationId = applicationId, path = "$SSO_DETAILS_PATH/$ssoId", methodType = HttpMethod.Get)
        return resultHandler.processResult(call, gson)
    }

    override suspend fun getSsoSessions(code: String, applicationId: String, ssoDetails: SsoDetails): Result<SsoSessions> {
        val call = requestHandler(
            bodyParameters = null,
            applicationId = applicationId,
            path = """
            $SSO_SESSIONS_PATH?code=$code&sso_team_id=${ssoDetails.ssoTeamId}&callback_url=msauth.com.thundermaps.saferme://auth&fields=installation_id,client_uuid
            """.trimIndent(),
            methodType = HttpMethod.Get
        )
        return resultHandler.processResult(call, gson)
    }

    private suspend fun <T : Any> requestHandler(
        bodyParameters: T? = null,
        applicationId: String,
        path: String,
        methodType: HttpMethod = HttpMethod.Post
    ): HttpClientCall {
        val parameters = createParameters(
            environmentManager.environment.servers.first(),
            applicationId,
            path.getApiVersion()
        )
        val (client, requestBuilder) = androidClient.client(parameters)
        val call = client.call(HttpRequestBuilder().takeFrom(requestBuilder).apply {
            method = methodType
            url(AndroidClient.baseUrlBuilder(parameters).apply {
                encodedPath = if (path.contains(SSO_SESSIONS_PATH)) path else "$encodedPath$path"
            }.build())
            if (bodyParameters != null && methodType == HttpMethod.Post) {
                contentType(ContentType.Application.Json)
                body = bodyParameters
            } else {
                headers.remove(HttpHeaders.ContentType)
            }
        })
        return call
    }

    private fun String.getApiVersion(): Int {
        return if (LOGIN_PATH == this) 3 else 4
    }

    companion object {
        private const val APPLICATION_JSON = "application/json, text/plain, */*"
        private const val SSO_DETAILS_PATH = "sso_details"
        private const val LOGIN_PATH = "sessions"
        @VisibleForTesting
        const val SSO_SESSIONS_PATH = "auth/sm_azure_oauth2/callback"
        private const val RESET_PASSWORD_PATH = "reset_passwords/request_token"
    }
}
