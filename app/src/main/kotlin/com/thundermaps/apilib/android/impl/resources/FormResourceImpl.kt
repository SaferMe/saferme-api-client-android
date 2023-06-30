package com.thundermaps.apilib.android.impl.resources

import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.resources.FormResource
import com.thundermaps.apilib.android.api.responses.models.Form
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.impl.AndroidClient
import com.thundermaps.apilib.android.impl.apiRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FormResourceImpl @Inject constructor(
    private val androidClient: AndroidClient
) : FormResource {
    override suspend fun getForm(
        parameters: RequestParameters,
        channelId: Int
    ): Result<Form> {
        val (client, requestBuilder) = androidClient.build(parameters, "channels/$channelId/form")
        return client.apiRequest(requestBuilder)
    }

    override suspend fun getForms(
        parameters: RequestParameters
    ): Result<List<Form>> {
        val (client, requestBuilder) = androidClient.build(parameters, "forms")
        return client.apiRequest(requestBuilder)
    }
}
