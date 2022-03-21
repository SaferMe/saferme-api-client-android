package com.thundermaps.apilib.android.api.requests

data class SaferMeApiResult<T>(
    val data: T,
    val serverStatus: SaferMeApiStatus,
    val requestHeaders: Map<String, List<String>>,
    val responseHeaders: Map<String, List<String>>
)
