package com.thundermaps.apilib.android.api.resources

import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.responses.models.Channel
import com.thundermaps.apilib.android.api.responses.models.Result

interface ChannelResource {
    suspend fun getChannels(
        parameters: RequestParameters,
        teamId: Long
    ): Result<List<Channel>>

    suspend fun getChannelsDeletedAfter(
        parameters: RequestParameters
    ): Result<DeletedResourceList>
}
