package com.thundermaps.apilib.android.impl

import com.thundermaps.apilib.android.api.ApiClient
import com.thundermaps.apilib.android.api.SaferMeClientService
import com.thundermaps.apilib.android.api.resources.SessionsResource
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.android.Android
import io.ktor.client.engine.android.AndroidEngineConfig
import io.ktor.client.features.auth.Auth
import io.ktor.client.features.auth.providers.BearerTokens
import io.ktor.client.features.json.JsonFeature

class DefaultKtorClient(private val httpClient: HttpClient? = null) : ApiClient {
    private lateinit var sessionsResource: SessionsResource

    override val ktorClient: HttpClient
        get() = httpClient?.let {
            it.config {
                plusAssign(ktorClientConfig as HttpClientConfig<Nothing>)
            }
        } ?: defaultClient

    // Reusable / Shared Components (Singleton)
    private val defaultClient: HttpClient by lazy {
        HttpClient(Android) {
            plusAssign(ktorClientConfig)
        }
    }

    private val ktorClientConfig = HttpClientConfig<AndroidEngineConfig>().apply {
        install(JsonFeature) {
            serializer = AndroidClient.jsonSerializer
        }
        install(Auth) {
            refreshToken {
                loadTokens {
                    sessionsResource = SaferMeClientService.getService().getClient().sessionsResource
                    sessionsResource.tokens
                }
                refreshTokens { response ->
                    val result = sessionsResource.refreshSessionToken()
                    result.getNullableData()?.let {
                        BearerTokens(it.session.accessToken, it.session.refreshToken)
                    }
                }
            }
        }

        expectSuccess = false
    }
}
