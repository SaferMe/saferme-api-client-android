package com.thundermaps.apilib.android.api.com.thundermaps.env

sealed class Environment(val servers: List<String>)
object Live : Environment(
    listOf(
        "api1.prod.saferme.io",
        "api2.prod.saferme.io",
        "api3.prod.saferme.io",
        "api4.prod.saferme.io"
    )
)

object Staging : Environment(
    listOf("api.staging.saferme.io", "api1.staging.saferme.io")
)
