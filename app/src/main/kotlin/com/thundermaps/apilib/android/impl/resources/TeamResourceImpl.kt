package com.thundermaps.apilib.android.impl.resources

import androidx.annotation.VisibleForTesting
import com.google.gson.Gson
import com.thundermaps.apilib.android.api.com.thundermaps.isInternetAvailable
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.resources.TeamResource
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.ResultHandler
import com.thundermaps.apilib.android.api.responses.models.Team
import com.thundermaps.apilib.android.api.responses.models.TeamUser
import com.thundermaps.apilib.android.impl.AndroidClient
import io.ktor.client.call.HttpClientCall
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeamResourceImpl @Inject constructor(
    private val androidClient: AndroidClient,
    private val resultHandler: ResultHandler,
    private val gson: Gson
) : TeamResource {
    override suspend fun getTeams(parameters: RequestParameters): Result<List<Team>> {
        if (!parameters.host.isInternetAvailable()) {
            return resultHandler.handleException(UnknownHostException())
        }
        val call = getTeamsCall(parameters)
        return resultHandler.processResult(call, gson)
    }

    private suspend fun getTeamsCall(
        parameters: RequestParameters
    ): HttpClientCall {
        val (client, requestBuilder) = androidClient.client(parameters)
        val call = client.request<HttpResponse>(
            HttpRequestBuilder().takeFrom(requestBuilder).apply {
                method = HttpMethod.Get
                url(
                    AndroidClient.baseUrlBuilder(parameters).apply {
                        encodedPath = "${encodedPath}$TEAM_PATH?$TEAM_FIELDS"
                    }.build()
                )
            }
        ).call
        return call
    }

    private suspend fun getTeamUsersCall(
        parameters: RequestParameters,
        teamId: String
    ): HttpClientCall {
        val (client, requestBuilder) = androidClient.client(parameters)
        val call = client.request<HttpResponse>(
            HttpRequestBuilder().takeFrom(requestBuilder).apply {
                method = HttpMethod.Get
                url(
                    AndroidClient.baseUrlBuilder(parameters).apply {
                        encodedPath = "$encodedPath$TEAM_PATH/$teamId/$TEAM_USER_PATH?$TEAM_USER_FIELDS"
                    }.build()
                )
            }
        ).call
        return call
    }

    override suspend fun getTeamUsers(
        parameters: RequestParameters,
        teamId: String
    ): Result<List<TeamUser>> {
        if (!parameters.host.isInternetAvailable()) {
            return resultHandler.handleException(UnknownHostException())
        }
        val call = getTeamUsersCall(parameters, teamId)
        return resultHandler.processResult(call, gson)
    }

    companion object {
        private const val TEAM_USER_PATH = "team_users"

        @VisibleForTesting
        const val TEAM_USER_FIELDS = "fields=first_name,last_name,email"

        const val TEAM_PATH = "teams"
        const val TEAM_FIELDS = "fields=mapbox_username,mapbox_dataset_id,mapbox_access_token"
    }
}
