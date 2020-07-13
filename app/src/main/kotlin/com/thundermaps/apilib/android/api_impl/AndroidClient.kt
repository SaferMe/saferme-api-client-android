package com.thundermaps.apilib.android.api_impl

import com.google.gson.GsonBuilder
import com.thundermaps.apilib.android.api.SaferMeClient
import com.thundermaps.apilib.android.api.SaferMeCredentials
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.resources.DeviceInfoLogsResource
import com.thundermaps.apilib.android.api.resources.TaskResource
import com.thundermaps.apilib.android.api.resources.TracedContactsResource
import com.thundermaps.apilib.android.api_impl.resources.DeviceInfoLogsImpl
import com.thundermaps.apilib.android.api_impl.resources.TasksImpl
import com.thundermaps.apilib.android.api_impl.resources.TracedContactsImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import javax.inject.Singleton


@Singleton
class AndroidClient : SaferMeClient() {

    //Supported API Endpoints
    override val Tasks: TaskResource = TasksImpl(this)

    override val TracedContacts: TracedContactsResource = TracedContactsImpl(this)

    override val DeviceInfoLogs: DeviceInfoLogsResource = DeviceInfoLogsImpl(this)

    //Reusable / Shared Components (Singleton)
    private var currentClient: HttpClient? = null
    private var requestBuilderTemplate = HttpRequestBuilder()

    //Store the most recently used credentials
    private var currentCredentials: SaferMeCredentials? = null

    //Default Request Options
    override fun defaultParams(): RequestParameters {
        return defaultOptions
    }

    @io.ktor.util.KtorExperimentalAPI
    fun client(params: RequestParameters): Pair<HttpClient, HttpRequestBuilder> {
        //Reinitialize if users credentials have changed
        if (currentClient == null || currentCredentials != params.credentials) {
            currentClient = HttpClient(CIO) {
                install(JsonFeature) {
                    serializer = GsonSerializer().apply { gsonBuilder }

                }
            }
            requestBuilderTemplate = HttpRequestBuilder().apply {
                val creds  = params.credentials
                currentCredentials = creds
                if (creds != null) {
                    headers.append("X-InstallationID", creds.InstallationId)
                    headers.append("Authorization", "Token token=${creds.ApiKey}")
                    headers.append("X-AppID", creds.AppId)
                    if (creds.TeamId != null)
                        headers.append("X-TeamID", creds.TeamId)
                }
                headers.append("Accept", "application/json, text/plain, */*")
                params.customRequestHeaders.forEach {(k,v) -> headers.append(k, v)}
            }

        }

        return Pair(currentClient!!,requestBuilderTemplate)
    }

    //Widely used static builders/configuration (Assists with DRY code)
    companion object {

        //Constants
        private const val DEFAULT_API_ENDPOINT = "public-api.thundermaps.com"

        //Default GSON Configuration
        val gsonBuilder = GsonBuilder().apply {
            disableHtmlEscaping()
            excludeFieldsWithoutExposeAnnotation()
            serializeNulls()
        }

        //Reusable serializer configured with default options
        val gsonSerializer = gsonBuilder.create()!!

        //Default resource options
        val defaultOptions =
            RequestParameters(
                customRequestHeaders = HashMap(),
                credentials = null,
                host = DEFAULT_API_ENDPOINT,
                port = null,
                api_version = 4
            )


        //Reusable URL Builder
        fun baseUrlBuilder(params: RequestParameters): URLBuilder {
            return URLBuilder().apply {
                protocol = URLProtocol.HTTPS
                host = params.host
                port = params.port ?: 443
                encodedPath = "/api/v${params.api_version}/"
            }
        }
    }
}
