package com.thundermaps.apilib.android.api.resources

import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.responses.models.Form
import com.thundermaps.apilib.android.api.responses.models.Result

interface FormResource {
    suspend fun getForm(
        parameters: RequestParameters,
        channelId: Int
    ): Result<Form>

    suspend fun getForms(
        parameters: RequestParameters
    ): Result<List<Form>>
}
