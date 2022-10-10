package com.thundermaps.apilib.android.impl.resources

import com.google.gson.Gson
import com.thundermaps.apilib.android.api.com.thundermaps.env.EnvironmentManager
import com.thundermaps.apilib.android.api.com.thundermaps.isInternetAvailable
import com.thundermaps.apilib.android.api.requests.Constants
import com.thundermaps.apilib.android.api.requests.Constants.APPLICATION_JSON
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.resources.BrandResource
import com.thundermaps.apilib.android.api.responses.models.Brand
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.ResultHandler
import com.thundermaps.apilib.android.impl.AndroidClient
import com.thundermaps.apilib.android.impl.HeaderType
import io.ktor.client.call.HttpClientCall
import io.ktor.client.call.call
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.url
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.util.KtorExperimentalAPI
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@KtorExperimentalAPI
@Singleton
class BrandResourceImpl @Inject constructor(
    private val androidClient: AndroidClient,
    private val environmentManager: EnvironmentManager,
    private val resultHandler: ResultHandler,
    private val gson: Gson
) : BrandResource {
    private fun createParameters(applicationId: String, host: String, apiVersion: Int) =
        RequestParameters(
            customRequestHeaders = hashMapOf(
                HeaderType.xAppId to applicationId,
                HttpHeaders.Accept to APPLICATION_JSON
            ),
            credentials = null,
            host = host,
            api_version = apiVersion
        )

    override suspend fun getBrand(applicationId: String): Result<Brand> {
        if (!environmentManager.environment.servers.first().isInternetAvailable()) {
            return resultHandler.handleException(UnknownHostException())
        }
        val call = requestHandler(applicationId, PATH)
        return resultHandler.processResult(call, gson)
    }

    private suspend fun requestHandler(
        applicationId: String,
        path: String
    ): HttpClientCall {
        val parameters = createParameters(
            applicationId,
            environmentManager.environment.servers.first(),
            4
        )
        val (client, requestBuilder) = androidClient.client(parameters)
        val call = client.call(
            HttpRequestBuilder().takeFrom(requestBuilder).apply {
                method = HttpMethod.Get
                url(
                    AndroidClient.baseUrlBuilder(parameters).apply {
                        encodedPath = "$encodedPath$path?fields=${Constants.BRAND_FIELDS.joinToString(",")}"
                    }.build()
                )
            }
        )
        return call
    }

    companion object {
        private const val PATH = "branded_app"
    }
}
