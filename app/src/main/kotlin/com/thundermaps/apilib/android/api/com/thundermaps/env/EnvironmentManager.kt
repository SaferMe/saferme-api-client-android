package com.thundermaps.apilib.android.api.com.thundermaps.env

import com.thundermaps.apilib.android.impl.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EnvironmentManager @Inject constructor() {
    var environment: Environment = if (BuildConfig.DEBUG) {
        Staging
    } else {
        Live
    }
        private set

    internal fun updateEnvironment(env: Environment) {
        environment = env
    }

    fun isStaging() = environment == Staging
}
