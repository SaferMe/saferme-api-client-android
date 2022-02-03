package com.thundermaps.apilib.android.api.requests

object Constants {
    const val APPLICATION_JSON = "application/json, text/plain, */*"
    val BRAND_FIELDS = listOf(
        "name",
        "bundle_id",
        "android_url",
        "oauth_providers",
        "layer_tilejson",
        "custom_user_fields",
        "default_location",
        "default_zoom",
        "safety_app"
    )
}
