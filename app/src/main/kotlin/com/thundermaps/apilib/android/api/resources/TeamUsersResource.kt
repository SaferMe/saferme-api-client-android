package com.thundermaps.apilib.android.api.resources

import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.TeamUserDetails

interface TeamUsersResource {
    suspend fun getTeamUsers(
        parameters: RequestParameters,
        teamId: String
    ): Result<List<TeamUserDetails>>
}
