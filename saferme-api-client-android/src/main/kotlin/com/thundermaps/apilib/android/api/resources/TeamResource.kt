package com.thundermaps.apilib.android.api.resources

import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.Team

interface TeamResource {
    suspend fun getTeams(parameters: RequestParameters): Result<List<Team>>
}
