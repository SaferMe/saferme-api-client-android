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

object LiveOceanfarmr : Environment(
    listOf(
        "api1.prod.infra.oceanfarmr.com",
        "api2.prod.infra.oceanfarmr.com",
        "api3.prod.infra.oceanfarmr.com",
        "api4.prod.infra.oceanfarmr.com"
    )
)

object StagingOceanfarmr : Environment(
    listOf("api.staging.infra.oceanfarmr.com", "api1.staging.infra.oceanfarmr.com")
)
