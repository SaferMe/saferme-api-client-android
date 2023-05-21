package com.thundermaps.apilib.android.api

import io.ktor.client.HttpClient

interface ApiClient {
    val ktorClient: HttpClient
}
