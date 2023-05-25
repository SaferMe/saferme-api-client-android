package com.thundermaps.apilib.android.impl.resources

import apiRequest
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.resources.ChannelResource
import com.thundermaps.apilib.android.api.resources.DeletedResourceList
import com.thundermaps.apilib.android.api.responses.models.Channel
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.impl.AndroidClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChannelResourceImpl @Inject constructor(
    private val androidClient: AndroidClient
) : ChannelResource {
    override suspend fun getChannels(
        parameters: RequestParameters,
        teamId: Long
    ): Result<List<Channel>> {
        val params = parameters.copy(parameters = (parameters.parameters ?: emptyMap()) + mapOf("team_id" to teamId.toString(), "fields" to CHANNELS_FIELDS.joinToString(",")))
        val (client, requestBuilder) = androidClient.build(params, PATH)
        return client.apiRequest(requestBuilder)
    }

    override suspend fun getChannelsDeletedAfter(
        parameters: RequestParameters
    ): Result<DeletedResourceList> {
        val params = parameters.copy(parameters = parameters.parameters ?: mapOf("type" to "account"))
        val (client, requestBuilder) = androidClient.build(params, PATH_DELETED_RESOURCES)
        return client.apiRequest(requestBuilder)
    }

    companion object {
        internal const val PATH = "channels"
        internal const val PATH_DELETED_RESOURCES = "deleted_resources"
        internal val CHANNELS_FIELDS = listOf("hazard_channel", "is_deletable_by", "member_count", "last_report_date", "-additional_fields", "tune_in_count")
    }
}
