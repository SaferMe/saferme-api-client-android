package com.thundermaps.apilib.android.api.requests

import com.thundermaps.apilib.android.api.SaferMeCredentials
import com.thundermaps.apilib.android.api.com.thundermaps.env.EnvironmentManager
import com.thundermaps.apilib.android.impl.HeaderType
import io.ktor.http.HttpHeaders

data class RequestParameters(
    val customRequestHeaders: Map<String, String>,
    val credentials: SaferMeCredentials?,

    // Server Configuration:
    val host: String,
    val port: Int? = null,
    val api_version: Int,
    val parameters: Map<String, String>? = null
)

fun buildRequestParameters(parameters: Map<String, String>? = null) =
    RequestParameters(
        customRequestHeaders = hashMapOf(
            HeaderType.xAppId to EnvironmentManager.appId,
            HttpHeaders.Accept to Constants.APPLICATION_JSON
        ),
        credentials = null,
        host = EnvironmentManager.host,
        api_version = 4,
        parameters = parameters
    )
