package com.thundermaps.apilib.android.impl

import com.google.gson.Gson
import com.thundermaps.apilib.android.api.SaferMeClient
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class SaferMeClientModule {
    @Provides
    @Singleton
    fun provideGson(): Gson = AndroidClient.gsonSerializer

    @Provides
    @Singleton
    fun provideClient(client: SaferMeClientImpl): SaferMeClient = client
}
