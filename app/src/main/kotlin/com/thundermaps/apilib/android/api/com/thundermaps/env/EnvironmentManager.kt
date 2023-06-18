package com.thundermaps.apilib.android.api.com.thundermaps.env

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EnvironmentManager @Inject constructor() {
    @Deprecated("Use EnvironmentManager.environment instead")
    val environment: Environment
        get() = Companion.environment

    @Deprecated(
        "Use EnvironmentManager.updateEnvironment instead",
        ReplaceWith(
            "Companion.updateEnvironment(env)",
            "com.thundermaps.apilib.android.api.com.thundermaps.env.EnvironmentManager.Companion"
        )
    )
    fun updateEnvironment(env: Environment) {
        Companion.environment = env
    }

    @Deprecated(
        "Use EnvironmentManager.isStaging instead",
        ReplaceWith(
            "Companion.isStaging()",
            "com.thundermaps.apilib.android.api.com.thundermaps.env.EnvironmentManager.Companion"
        )
    )
    fun isStaging() = Companion.isStaging()

    companion object {
        private const val APP_ID_SAFERME = "com.thundermaps.saferme"
        private const val APP_ID_OCEANFARMR = "com.thundermaps.smartoysters"

        // default to Saferme staging
        var environment: Environment = Staging
            private set

        fun isStaging() = environment == Staging || environment == StagingOceanfarmr

        val host = environment.servers.first()
        val appId = if (environment == Staging || environment == Live) APP_ID_SAFERME else APP_ID_OCEANFARMR
    }
}
