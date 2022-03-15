package com.thundermaps.apilib.android.api.com.thundermaps.env

sealed class Environment(val servers: List<String>)
object Live : Environment(
    listOf(
        "api1.thundermaps.com",
        "api2.thundermaps.com",
        "api3.thundermaps.com",
        "api4.thundermaps.com"
    )
)

object Staging : Environment(
    listOf("api.staging.saferme.io", "api1.staging.saferme.io")
)
