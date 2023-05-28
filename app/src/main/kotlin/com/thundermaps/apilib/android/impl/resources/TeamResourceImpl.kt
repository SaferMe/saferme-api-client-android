package com.thundermaps.apilib.android.impl.resources

import androidx.annotation.VisibleForTesting
import apiRequest
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.resources.TeamResource
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.Team
import com.thundermaps.apilib.android.api.responses.models.TeamUser
import com.thundermaps.apilib.android.impl.AndroidClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeamResourceImpl @Inject constructor(
    private val androidClient: AndroidClient
) : TeamResource {
    override suspend fun getTeams(parameters: RequestParameters): Result<List<Team>> {
        val params = parameters.copy(parameters = mapOf("fields" to TEAM_FIELDS.joinToString(",")) + (parameters.parameters ?: emptyMap()))
        val (client, requestBuilder) = androidClient.build(params, TEAM_PATH)
        return client.apiRequest(requestBuilder)
    }

    override suspend fun getTeamUsers(
        parameters: RequestParameters,
        teamId: String
    ): Result<List<TeamUser>> {
        val params = parameters.copy(parameters = mapOf("fields" to TEAM_USER_FIELDS.joinToString(",")) + (parameters.parameters ?: emptyMap()))
        val (client, requestBuilder) = androidClient.build(params, "$TEAM_PATH/$teamId/$TEAM_USER_PATH")
        return client.apiRequest(requestBuilder)
    }

    companion object {
        @VisibleForTesting
        const val TEAM_USER_PATH = "team_users"
        @VisibleForTesting
        val TEAM_USER_FIELDS = listOf("first_name", "last_name", "email")

        @VisibleForTesting
        const val TEAM_PATH = "teams"
        @VisibleForTesting
        val TEAM_FIELDS = listOf("mapbox_username", "mapbox_dataset_id", "mapbox_access_token")
    }
}
