package com.thundermaps.apilib.android.api.com.thundermaps.env

sealed class Environment(val servers: List<String>)
object Live : Environment(
    listOf(
        "https://api1.thundermaps.com",
        "https://api2.thundermaps.com",
        "https://api3.thundermaps.com",
        "https://api4.thundermaps.com"
    )
)

object Staging : Environment(
    listOf("https://api.staging.saferme.io")
)
