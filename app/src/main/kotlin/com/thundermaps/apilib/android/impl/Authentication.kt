package com.thundermaps.apilib.android.impl

import io.ktor.client.features.auth.Auth
import io.ktor.client.features.auth.AuthProvider
import io.ktor.client.features.auth.providers.BearerTokens
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.auth.AuthScheme
import io.ktor.http.auth.HttpAuthHeader
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

// Based on io.ktor.client.features.auth.providers.BearerAuth

fun Auth.refreshToken(block: RefreshTokenAuthConfig.() -> Unit) {
    with(RefreshTokenAuthConfig().apply(block)) {
        providers.add(RefreshTokenAuthProvider(_refreshTokens, _loadTokens, _sendWithoutRequest, realm))
    }
}

class RefreshTokenAuthConfig {
    internal var _refreshTokens: suspend (response: HttpResponse) -> BearerTokens? = { null }
    internal var _loadTokens: suspend () -> BearerTokens? = { null }
    internal var _sendWithoutRequest: (HttpRequestBuilder) -> Boolean = { true }

    var realm: String? = null

    fun refreshTokens(block: suspend (response: HttpResponse) -> BearerTokens?) {
        _refreshTokens = block
    }

    fun loadTokens(block: suspend () -> BearerTokens?) {
        _loadTokens = block
    }

    /**
     * Send credentials in without waiting for [HttpStatusCode.Unauthorized].
     */
    fun sendWithoutRequest(block: (HttpRequestBuilder) -> Boolean) {
        _sendWithoutRequest = block
    }
}

class RefreshTokenAuthProvider(
    private val refreshTokens: suspend (response: HttpResponse) -> BearerTokens?,
    loadTokens: suspend () -> BearerTokens?,
    private val sendWithoutRequestCallback: (HttpRequestBuilder) -> Boolean = { true },
    private val realm: String?
) : AuthProvider {

    override val sendWithoutRequest: Boolean
        get() = error("Deprecated")

    private val tokensHolder = AuthTokenHolder(loadTokens)

    override fun sendWithoutRequest(request: HttpRequestBuilder): Boolean = sendWithoutRequestCallback(request)

    /**
     * Check if current provider is applicable to the request.
     */
    override fun isApplicable(auth: HttpAuthHeader): Boolean {
        if (auth.authScheme != AuthScheme.Bearer) return false
        if (realm == null) return true
        if (auth !is HttpAuthHeader.Parameterized) return false

        return auth.parameter("realm") == realm
    }

    /**
     * Add authentication method headers and creds.
     */
    override suspend fun addRequestHeaders(request: HttpRequestBuilder) {
        val token = tokensHolder.loadToken() ?: return

        request.headers {
            val tokenValue = "Token token=${token.accessToken}"
            if (contains(HttpHeaders.Authorization)) {
                remove(HttpHeaders.Authorization)
            }
            append(HttpHeaders.Authorization, tokenValue)
        }
    }

    override suspend fun refreshToken(response: HttpResponse): Boolean {
        return tokensHolder.setToken { refreshTokens(response) } != null
    }

    suspend fun clearToken() {
        tokensHolder.clearToken()
    }
}

internal class AuthTokenHolder<T>(
    private val loadTokens: suspend () -> T?
) {
    private val initialized = AtomicBoolean(false)

    private val cachedBearerTokens: AtomicReference<T?> = AtomicReference(null)

    internal fun clearToken() {
        cachedBearerTokens.set(null)
        initialized.set(false)
    }

    internal suspend fun loadToken(): T? {
        if (initialized.compareAndSet(false, true)) {
            val token = loadTokens()
            cachedBearerTokens.set(token)
            return token
        }

        return cachedBearerTokens.get()
    }

    internal suspend fun setToken(block: suspend () -> T?): T? {
        val token = block()
        cachedBearerTokens.set(token)
        return token
    }
}
