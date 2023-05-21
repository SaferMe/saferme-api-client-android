package com.thundermaps.apilib.android.api

import com.thundermaps.apilib.android.impl.AndroidClient
import com.thundermaps.apilib.android.impl.DefaultKtorClient
import com.thundermaps.apilib.android.impl.SaferMeClientModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(modules = [SaferMeClientModule::class])
@Singleton
interface SaferMeClientService {
    fun getClient(): SaferMeClient

    fun inject(client: AndroidClient)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun apiClient(apiClient: ApiClient): Builder
        fun build(): SaferMeClientService
    }

    companion object {
        private var INSTANCE: SaferMeClientService? = null

        fun getService(apiClient: ApiClient? = null): SaferMeClientService {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = DaggerSaferMeClientService
                    .builder()
                    .apiClient(DefaultKtorClient(apiClient?.ktorClient))
                    .build()
                INSTANCE = instance

                return instance
            }
        }
    }
}
