package com.thundermaps.apilib.android.api

import com.thundermaps.apilib.android.api_impl.SaferMeClientModule
import dagger.Component
import javax.inject.Singleton

@Component(modules = [SaferMeClientModule::class])
@Singleton
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
