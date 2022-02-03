package com.thundermaps.apilib.android.api

import com.thundermaps.apilib.android.impl.SaferMeClientModule
import dagger.Component
import javax.inject.Singleton

@Component(modules = [SaferMeClientModule::class])
@Singleton
interface SaferMeClientService {
    fun getClient(): SaferMeClient

    companion object {
        private var INSTANCE: SaferMeClientService? = null

        fun getService(): SaferMeClientService {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = DaggerSaferMeClientService
                    .builder()
                    .saferMeClientModule(SaferMeClientModule())
                    .build()
                INSTANCE = instance

                return instance
            }
        }
    }
}
