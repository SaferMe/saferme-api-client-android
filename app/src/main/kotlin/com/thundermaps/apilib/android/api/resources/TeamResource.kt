package com.thundermaps.apilib.android.api.resources

import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.Team
import com.thundermaps.apilib.android.api.responses.models.TeamUser

interface TeamResource {
    suspend fun getTeams(parameters: RequestParameters): Result<List<Team>>
    suspend fun getTeamUsers(
        parameters: RequestParameters,
        teamId: String
    ): Result<List<TeamUser>>
}
