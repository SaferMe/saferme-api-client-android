package com.thundermaps.apilib.android.api.resources

import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.responses.models.Channel
import com.thundermaps.apilib.android.api.responses.models.Result

interface ChannelResource {
    suspend fun getChannels(
        parameters: RequestParameters,
        teamId: Long,
        fields: String = DEFAULT_FIELDS,
        updatedAfter: String
    ): Result<List<Channel>>

    companion object {
        internal const val DEFAULT_FIELDS = "hazard_channel,is_deletable_by,member_count,last_report_date,-additional_fields,tune_in_count"
    }
}
