package com.thundermaps.apilib.android.api_impl

import com.thundermaps.apilib.android.api.SaferMeClient
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class SaferMeClientModule {
    @Provides
    fun provideClient(client: SaferMeClientImpl): SaferMeClient = client
}
