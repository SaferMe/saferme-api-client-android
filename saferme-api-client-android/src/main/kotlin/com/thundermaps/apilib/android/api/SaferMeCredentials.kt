package com.thundermaps.apilib.android.api

data class SaferMeCredentials(
    val ApiKey: String,
    val InstallationId: String,
    val AppId: String,
    val TeamId: String?,
    val clientUuid: String?
)
