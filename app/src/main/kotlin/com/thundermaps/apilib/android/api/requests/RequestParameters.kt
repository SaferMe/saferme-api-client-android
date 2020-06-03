package com.thundermaps.apilib.android.api.requests

import com.thundermaps.apilib.android.api.SaferMeCredentials

data class RequestParameters(
    val customRequestHeaders: Map<String, String>,
    val credentials: SaferMeCredentials?,

    // Server Configuration:
    val host: String,
    val port: Int? = null,
    val api_version: Int
)

