package com.thundermaps.apilib.android.impl.resources

import androidx.annotation.VisibleForTesting
import com.google.gson.Gson
import com.thundermaps.apilib.android.api.com.thundermaps.isInternetAvailable
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.resources.TeamUsersResource
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.ResultHandler
import com.thundermaps.apilib.android.api.responses.models.TeamUserDetails
import com.thundermaps.apilib.android.impl.AndroidClient
import io.ktor.client.call.HttpClientCall
import io.ktor.client.call.call
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.url
import io.ktor.http.HttpMethod
import io.ktor.util.KtorExperimentalAPI
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@KtorExperimentalAPI
@Singleton
class TeamUsersResourceImpl @Inject constructor(
    private val androidClient: AndroidClient,
    private val resultHandler: ResultHandler,
    private val gson: Gson
) : TeamUsersResource {

    private suspend fun getTeamUsersCall(
        parameters: RequestParameters,
        teamId: String
    ): HttpClientCall {
        val (client, requestBuilder) = androidClient.client(parameters)
        val call = client.call(HttpRequestBuilder().takeFrom(requestBuilder).apply {
            method = HttpMethod.Get
            url(AndroidClient.baseUrlBuilder(parameters).apply {
                encodedPath = "${encodedPath}$TEAM_PATH/$teamId?$FIELDS_PARAM"
            }.build())
        })
        return call
    }

    override suspend fun getTeamUsers(
        parameters: RequestParameters,
        teamId: String
    ): Result<List<TeamUserDetails>> {
        if (!parameters.host.isInternetAvailable()) {
            return resultHandler.handleException(UnknownHostException())
        }
        val call = getTeamUsersCall(parameters, teamId)
        return resultHandler.processResult(call, gson)
    }

    companion object {
        private const val TEAM_PATH = "team_users"

        @VisibleForTesting
        const val FIELDS_PARAM =
            "fields=first_name,last_name,email,-supervisor_id"
    }
}
