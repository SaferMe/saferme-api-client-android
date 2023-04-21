package com.thundermaps.apilib.android.api.requests

class SaferMeApiError(
    val statusCode: Int,
    val serverStatus: SaferMeApiStatus?,
    val requestHeaders: Map<String, List<String>>,
    val responseHeaders: Map<String, List<String>>,
    val url: String? = null,
    val error: String? = null
) : Exception()
