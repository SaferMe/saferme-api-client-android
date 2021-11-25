package com.thundermaps.apilib.android.impl.resources

import com.google.gson.Gson
import com.thundermaps.apilib.android.api.com.thundermaps.apilib.android.logging.ELog
import com.thundermaps.apilib.android.api.com.thundermaps.env.Environment
import com.thundermaps.apilib.android.api.com.thundermaps.env.EnvironmentManager
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.requests.SaferMeApiStatus
import com.thundermaps.apilib.android.api.requests.models.SessionBody
import com.thundermaps.apilib.android.api.resources.SessionsResource
import com.thundermaps.apilib.android.api.responses.models.ResponseError
import com.thundermaps.apilib.android.api.responses.models.ResponseException
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.ResultHandler
import com.thundermaps.apilib.android.api.responses.models.Sessions
import com.thundermaps.apilib.android.impl.AndroidClient
import io.ktor.client.call.HttpClientCall
import io.ktor.client.call.call
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.util.toByteArray
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionsImpl @Inject constructor(
    private val androidClient: AndroidClient,
    private val environmentManager: EnvironmentManager,
    private val resultHandler: ResultHandler
) : SessionsResource {

    override fun updateEnvironment(environment: Environment) {
        environmentManager.updateEnvironment(environment)
    }

    private fun createLoginParameters(host: String): RequestParameters {
        return RequestParameters(
            customRequestHeaders = hashMapOf(
                "X-AppID" to "com.thundermaps.saferme",
                "Accept" to "application/json",
                "Content-Type" to "application/json"
            ),
            credentials = null,
            host = host,
            api_version = 3
        )
    }

    override suspend fun login(
        body: SessionBody
    ): Result<Sessions> {
        val call = loginHandler(body)
        val status = SaferMeApiStatus.statusForCode(call.response.status.value)
        val responseString = String(call.response.content.toByteArray())
        val gson = Gson()
        ELog.i(this::class.java.simpleName, "response login: $responseString")
        return when (status) {
            SaferMeApiStatus.OK, SaferMeApiStatus.OTHER_200 -> {
                try {
                    val sessions = gson.fromJson(
                        responseString,
                        Sessions::class.java
                    )
                    resultHandler.handleSuccess(sessions)
                } catch (exception: Exception) {
                    resultHandler.handleException(exception)
                }
            }
            else -> {
                val exception = try {
                    val responseError = gson.fromJson(
                        responseString,
                        ResponseError::class.java
                    )
                    ResponseException(responseError)
                } catch (exception: Exception) {
                    exception
                }
                resultHandler.handleException(exception)
            }
        }
    }

    private suspend inline fun loginHandler(
        sessionBody: SessionBody
    ): HttpClientCall {
        val client = androidClient.currentClient
        val call = client.call(HttpRequestBuilder().apply {
            method = HttpMethod.Post
            url(AndroidClient.baseUrlBuilder(createLoginParameters(environmentManager.environment.servers.first())).apply {
                encodedPath = "${this.encodedPath}${"sessions"}"
            }.build())
            contentType(ContentType.Application.Json)
            body = sessionBody
        })
        return call
    }
}
