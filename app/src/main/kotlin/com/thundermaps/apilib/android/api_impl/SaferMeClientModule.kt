package com.thundermaps.apilib.android.api_impl

import com.thundermaps.apilib.android.api.SaferMeClient
import com.thundermaps.apilib.android.api.resources.SessionsResource
import com.thundermaps.apilib.android.api_impl.resources.SessionsImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class SaferMeClientModule {
    @Provides
    @Singleton
    fun sessionsResource(sessionsImpl: SessionsImpl): SessionsResource = sessionsImpl
    
    @Provides
    @Singleton
    fun provideClient(client: SaferMeClientImpl): SaferMeClient = client
}
