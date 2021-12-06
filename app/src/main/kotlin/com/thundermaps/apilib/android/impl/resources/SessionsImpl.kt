package com.thundermaps.apilib.android.impl.resources

import com.google.gson.Gson
import com.thundermaps.apilib.android.api.com.thundermaps.env.EnvironmentManager
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.requests.models.EmailBody
import com.thundermaps.apilib.android.api.requests.models.SessionBody
import com.thundermaps.apilib.android.api.resources.SessionsResource
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.ResultHandler
import com.thundermaps.apilib.android.api.responses.models.Sessions
import com.thundermaps.apilib.android.impl.AndroidClient
import com.thundermaps.apilib.android.impl.Provider
import io.ktor.client.call.HttpClientCall
import io.ktor.client.call.call
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.util.KtorExperimentalAPI
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
                "X-AppID" to applicationId,
                "Accept" to APPLICATION_JSON,
                "Content-Type" to APPLICATION_JSON,
                "X-Installationid" to Provider.xInstallId
            ),
            credentials = null,
            host = host,
            api_version = apiVersion
        )

    override suspend fun login(
        body: SessionBody,
        applicationId: String
    ): Result<Sessions> {
        val call = requestHandler(body, applicationId, LOGIN_PATH)
        return resultHandler.processResult(call, gson)
    }

    override suspend fun requestPassword(body: EmailBody, applicationId: String): Result<String> {
        val call = requestHandler(body, applicationId, RESET_PASSWORD_PATH)
        return resultHandler.processResult<EmailBody>(call, gson).convert { it.email }
    }

    private suspend inline fun <reified T : Any> requestHandler(
        bodyParameters: T,
        applicationId: String,
        path: String
    ): HttpClientCall {
        val parameters = createParameters(
                environmentManager.environment.servers.first(),
                applicationId,
                path.getApiVersion()
            )
        val (client, requestBuilder) = androidClient.client(parameters)
        val call = client.call(HttpRequestBuilder().takeFrom(requestBuilder).apply {
            method = HttpMethod.Post
            url(AndroidClient.baseUrlBuilder(parameters).apply {
                encodedPath = "$encodedPath$path"
            }.build())
            contentType(ContentType.Application.Json)
            body = bodyParameters
        })
        return call
    }

    private fun String.getApiVersion(): Int {
        return if (LOGIN_PATH == this) 3 else 4
    }

    companion object {
        private const val APPLICATION_JSON = "application/json"
        private const val LOGIN_PATH = "sessions"
        private const val RESET_PASSWORD_PATH = "reset_passwords/request_token"
    }
}
