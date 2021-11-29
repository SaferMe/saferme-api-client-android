package com.thundermaps.apilib.android.impl.resources

import com.google.gson.Gson
import com.thundermaps.apilib.android.api.com.thundermaps.env.EnvironmentManager
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.requests.models.SessionBody
import com.thundermaps.apilib.android.api.resources.SessionsResource
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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionsImpl @Inject constructor(
    private val androidClient: AndroidClient,
    private val environmentManager: EnvironmentManager,
    private val resultHandler: ResultHandler,
    private val gson: Gson
) : SessionsResource {
    override fun isStaging(): Boolean = environmentManager.isStaging()

    private fun createLoginParameters(host: String, applicationId: String) = RequestParameters(
        customRequestHeaders = hashMapOf(
            "X-AppID" to applicationId,
            "Accept" to APPLICATION_JSON,
            "Content-Type" to APPLICATION_JSON
        ),
        credentials = null,
        host = host,
        api_version = 4
    )

    override suspend fun login(
        body: SessionBody,
        applicationId: String
    ): Result<Sessions> {
        val call = loginHandler(body, applicationId)
        return resultHandler.processResult(call, gson)
    }

    private suspend inline fun loginHandler(
        sessionBody: SessionBody,
        applicationId: String
    ): HttpClientCall {
        val parameters =
            createLoginParameters(environmentManager.environment.servers.first(), applicationId)
        val (client, requestBuilder) = androidClient.client(parameters)
        val call = client.call(HttpRequestBuilder().takeFrom(requestBuilder).apply {
            method = HttpMethod.Post
            url(AndroidClient.baseUrlBuilder(parameters).apply {
                encodedPath = "${encodedPath}sessions"
            }.build())
            contentType(ContentType.Application.Json)
            body = sessionBody
        })
        return call
    }

    companion object {
        private const val APPLICATION_JSON = "application/json"
    }
}
