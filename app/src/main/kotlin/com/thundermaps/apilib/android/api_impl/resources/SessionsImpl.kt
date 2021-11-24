package com.thundermaps.apilib.android.api_impl.resources

import com.thundermaps.apilib.android.api.com.thundermaps.env.Environment
import com.thundermaps.apilib.android.api.com.thundermaps.env.EnvironmentManager
import com.thundermaps.apilib.android.api.com.thundermaps.env.Staging
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.requests.SaferMeApiResult
import com.thundermaps.apilib.android.api.requests.SaferMeApiStatus
import com.thundermaps.apilib.android.api.requests.models.SessionBody
import com.thundermaps.apilib.android.api.resources.SessionsResource
import com.thundermaps.apilib.android.api.responses.models.ResponseError
import com.thundermaps.apilib.android.api.responses.models.Sessions
import com.thundermaps.apilib.android.api_impl.AndroidClient
import io.ktor.client.HttpClient
import io.ktor.client.call.HttpClientCall
import io.ktor.client.call.call
import io.ktor.client.engine.android.Android
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.url
import io.ktor.client.response.readBytes
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.util.toMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionsImpl @Inject constructor(
    private val androidClient: AndroidClient,
    private val environmentManager: EnvironmentManager
) : SessionsResource {
    private var client = HttpClient(Android) {
        install(JsonFeature) {
            serializer = GsonSerializer().apply { AndroidClient.gsonBuilder }
        }
    }
    
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
        body: SessionBody,
        success: (data: Sessions) -> Unit,
        error: (data: ResponseError) -> Unit
    ) {
        loginHandler(body) { call ->
            val status = SaferMeApiStatus.statusForCode(call.response.status.value)
            val responseString = call.response.readBytes().toString()
            when (status) {
                SaferMeApiStatus.OTHER_200 -> {
                    success(
                        AndroidClient.gsonSerializer.fromJson(
                            responseString,
                            Sessions::class.java
                        )
                    )
                }
                else -> {
                    error(
                        SaferMeApiResult(
                            data = AndroidClient.gsonSerializer.fromJson(
                                responseString,
                                ResponseError::class.java
                            ),
                            serverStatus = status,
                            requestHeaders = call.request.headers.toMap(),
                            responseHeaders = call.response.headers.toMap()
                        )
                    )
                }
            }
        }
    }
    
    
    private suspend inline fun loginHandler(
        sessionBody: SessionBody,
        result: (call: HttpClientCall) -> Unit
    ) {
        val jsonBody = AndroidClient.gsonSerializer.toJsonTree(sessionBody)
        val client = androidClient.currentClient
        val call = client.call(HttpRequestBuilder().apply {
            method = HttpMethod.Post
            url(AndroidClient.baseUrlBuilder(createLoginParameters(environmentManager.environment.servers.first())).apply {
                encodedPath = "${this.encodedPath}${"sessions"}"
            }.build())
            if (jsonBody != null) {
                contentType(ContentType.Application.Json)
                body = jsonBody
            }
        })
        result(call)
    }
}
