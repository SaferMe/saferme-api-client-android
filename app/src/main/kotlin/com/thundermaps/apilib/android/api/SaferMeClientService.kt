package com.thundermaps.apilib.android.api

import com.thundermaps.apilib.android.api_impl.SaferMeClientModule
import dagger.Component

@Component(modules = [SaferMeClientModule::class])
interface SaferMeClientService {
    fun getClient(): SaferMeClient

    companion object {
        fun getService(): SaferMeClientService {
            return DaggerSaferMeClientService
                .builder()
                .saferMeClientModule(SaferMeClientModule())
                .build()
        }
    }
}
