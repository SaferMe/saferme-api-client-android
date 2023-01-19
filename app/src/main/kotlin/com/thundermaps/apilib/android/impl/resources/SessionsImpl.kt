package com.thundermaps.apilib.android.impl.resources

import androidx.annotation.VisibleForTesting
import com.google.gson.Gson
import com.thundermaps.apilib.android.api.com.thundermaps.env.EnvironmentManager
import com.thundermaps.apilib.android.api.com.thundermaps.isInternetAvailable
import com.thundermaps.apilib.android.api.requests.Constants.APPLICATION_JSON
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.requests.models.EmailBody
import com.thundermaps.apilib.android.api.requests.models.SessionBody
import com.thundermaps.apilib.android.api.requests.models.SessionBodyV4
import com.thundermaps.apilib.android.api.requests.models.UserV4
import com.thundermaps.apilib.android.api.resources.SessionsResource
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.ResultHandler
import com.thundermaps.apilib.android.api.responses.models.Session
import com.thundermaps.apilib.android.api.responses.models.SsoDetails
import com.thundermaps.apilib.android.api.responses.models.SsoSessions
import com.thundermaps.apilib.android.impl.AndroidClient
import com.thundermaps.apilib.android.impl.HeaderType
import io.ktor.client.call.HttpClientCall
import io.ktor.client.features.auth.providers.BearerTokens
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import timber.log.Timber
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionsImpl @Inject constructor(
    private val androidClient: AndroidClient,
    private val environmentManager: EnvironmentManager,
    private val resultHandler: ResultHandler,
    private val gson: Gson
) : SessionsResource {
    private val sessionCache = mutableListOf<Session>()
    override val tokens: BearerTokens?
        get() = sessionCache.lastOrNull()?.let {
            Timber.d("token = ${it.session.accessToken}")
            BearerTokens(it.session.accessToken, it.session.refreshToken)
        }

    override fun isStaging(): Boolean = environmentManager.isStaging()

    override fun updateSession(session: Session) {
        sessionCache.add(session)
        Timber.d("update session = $session")
    }

    override suspend fun getCurrentSession(): Result<Session> {
        val call = requestHandler(null, "", SESSION_PATH, HttpMethod.Get)
        return resultHandler.processResult<Session>(call, gson).let {
            if (it.isSuccess) {
                it.convert { _ ->
                    sessionCache.last()
                }
            } else {
                it
            }
        }
    }

    override suspend fun refreshSessionToken(): Result<Session> {
        val call = requestHandler(sessionCache.lastOrNull()?.toSessionRequest(), "", SESSION_PATH, HttpMethod.Patch)
        return resultHandler.processResult<Session>(call, gson).also {
            it.getNullableData()?.let { session ->
                sessionCache.add(session)
            }
        }
    }

    override suspend fun deleteCurrentSession() {
        val call = requestHandler(null, "", SESSION_PATH, HttpMethod.Delete)
        resultHandler.processResult<HttpResponse>(call, gson).also {
            sessionCache.clear()
        }
    }

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
    ): Result<Session> = loginV4(body, applicationId)

    private suspend fun loginV4(
        body: SessionBody,
        applicationId: String
    ): Result<Session> {
        if (!environmentManager.environment.servers.first().isInternetAvailable()) {
            return resultHandler.handleException(UnknownHostException())
        }
        val requestBody = SessionBodyV4(UserV4(applicationId, body.user.email, body.user.password))
        val call = requestHandler(requestBody, applicationId, LOGIN_PATH)
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
        val call = requestHandler(
            bodyParameters = null,
            applicationId = applicationId,
            path = "$SSO_DETAILS_PATH/$ssoId",
            methodType = HttpMethod.Get
        )
        return resultHandler.processResult(call, gson)
    }

    override suspend fun getSsoSessions(
        code: String,
        applicationId: String,
        ssoDetails: SsoDetails,
        nonce: String?
    ): Result<SsoSessions> {
        val call = requestHandler(
            bodyParameters = null,
            applicationId = applicationId,
            path = "$SSO_SESSIONS_PATH?code=$code&sso_team_id=${ssoDetails.ssoTeamId}&callback_url=$SSO_REDIRECT_URL&fields=$SSO_FIELDS&nonce=$nonce",
            methodType = HttpMethod.Post
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
            getApiVersion()
        )
        val (client, requestBuilder) = androidClient.client(parameters)
        val call = client.request<HttpResponse> (
            HttpRequestBuilder().takeFrom(requestBuilder).apply {
                method = methodType
                url(
                    AndroidClient.baseUrlBuilder(parameters).apply {
                        encodedPath = if (path.contains(SSO_SESSIONS_PATH)) path else "$encodedPath$path"
                    }.build()
                )
                if (bodyParameters != null) {
                    contentType(ContentType.Application.Json)
                    body = bodyParameters
                } else {
                    headers.remove(HttpHeaders.ContentType)
                }
            }
        ).call
        return call
    }

    private fun getApiVersion(): Int = 4

    companion object {
        private const val SSO_DETAILS_PATH = "sso_details"
        private const val LOGIN_PATH = "session"
        private const val SESSION_PATH = "session"

        @VisibleForTesting
        const val SSO_SESSIONS_PATH = "auth/sm_azure_oauth2/callback"
        private const val RESET_PASSWORD_PATH = "reset_passwords/request_token"

        @VisibleForTesting
        const val SSO_REDIRECT_URL = "msauth.com.thundermaps.saferme://auth"
        const val SSO_FIELDS = "installation_id,client_uuid"
    }
}
