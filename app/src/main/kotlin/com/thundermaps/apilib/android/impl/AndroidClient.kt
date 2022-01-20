package com.thundermaps.apilib.android.impl

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.thundermaps.apilib.android.api.SaferMeCredentials
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.responses.models.FieldType
import com.thundermaps.apilib.android.api.responses.models.FieldTypeDecode
import com.thundermaps.apilib.android.api.responses.models.FormValue
import com.thundermaps.apilib.android.api.responses.models.FormValueDecode
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.HttpHeaders
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import javax.inject.Inject

class AndroidClient @Inject constructor() {
    // Reusable / Shared Components (Singleton)
    val currentClient: HttpClient by lazy {
        HttpClient(Android) {
            install(JsonFeature) {
                serializer = GsonSerializer().apply { gsonBuilder }
            }
        }
    }

    private var requestBuilderTemplate = HttpRequestBuilder()

    // Store the most recently used credentials
    private var currentCredentials: SaferMeCredentials? = null

    @io.ktor.util.KtorExperimentalAPI
    fun client(params: RequestParameters): Pair<HttpClient, HttpRequestBuilder> {
        // Reinitialize if users credentials have changed
        if (currentCredentials != params.credentials) {
            requestBuilderTemplate = HttpRequestBuilder().apply {
                val creds = params.credentials
                currentCredentials = creds
                if (creds != null) {
                    headers.append(HeaderType.xInstallationId, creds.InstallationId)
                    headers.append(HttpHeaders.Authorization, "Token token=${creds.ApiKey}")
                    headers.append(HeaderType.xAppId, creds.AppId)
                    if (creds.TeamId != null)
                        headers.append(HeaderType.xTeamId, creds.TeamId)
                }
                headers.append(HttpHeaders.Accept, "application/json, text/plain, */*")
                params.customRequestHeaders.forEach { (key, value) ->
                    headers[key] = value
                }
            }
        } else if (currentCredentials == null) {
            requestBuilderTemplate.apply {
                params.customRequestHeaders.forEach { (key, value) ->
                    headers[key] = value
                }
            }
        }

        return Pair(currentClient, requestBuilderTemplate)
    }

    // Widely used static builders/configuration (Assists with DRY code)
    companion object {

        // Default GSON Configuration
        val gsonBuilder = GsonBuilder().apply {
            disableHtmlEscaping()
            excludeFieldsWithoutExposeAnnotation()
            setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
            registerTypeAdapter(FieldType::class.java, FieldTypeDecode())
            registerTypeAdapter(FormValue::class.java, FormValueDecode())
        }

        // Reusable serializer configured with default options
        val gsonSerializer: Gson = gsonBuilder.create()

        val gson: Gson = GsonBuilder().apply {
            registerTypeAdapter(FieldType::class.java, FieldTypeDecode())
            registerTypeAdapter(FormValue::class.java, FormValueDecode())
        }.create()

        // Reusable URL Builder
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
