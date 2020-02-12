package com.thundermaps.apilib.android.api.requests

class SaferMeApiError(
    val serverStatus: SaferMeApiStatus?,
    val requestHeaders: Map<String, List<String>>,
    val responseHeaders: Map<String, List<String>>
) : Exception()
