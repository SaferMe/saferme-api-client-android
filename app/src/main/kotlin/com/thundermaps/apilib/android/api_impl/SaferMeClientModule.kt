package com.thundermaps.apilib.android.api_impl

import com.thundermaps.apilib.android.api.SaferMeClient
import dagger.Module
import dagger.Provides

@Module
class SaferMeClientModule {

    @Provides
    fun provideClient(): SaferMeClient = AndroidClient()

}