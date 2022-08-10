package com.thundermaps.apilib.android.impl.resources

import com.google.gson.Gson
import com.thundermaps.apilib.android.api.com.thundermaps.isInternetAvailable
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.requests.models.EmailBody
import com.thundermaps.apilib.android.api.requests.models.FirebaseTokenBody
import com.thundermaps.apilib.android.api.requests.models.UpdateAddressBody
import com.thundermaps.apilib.android.api.requests.models.UpdateContactNumberBody
import com.thundermaps.apilib.android.api.requests.models.UpdateEmailNotificationEnableBody
import com.thundermaps.apilib.android.api.requests.models.UpdateNameBody
import com.thundermaps.apilib.android.api.requests.models.UpdatePasswordBody
import com.thundermaps.apilib.android.api.requests.models.UpdateProfileBody
import com.thundermaps.apilib.android.api.resources.MeResource
import com.thundermaps.apilib.android.api.responses.models.Clients
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.ResultHandler
import com.thundermaps.apilib.android.api.responses.models.UserDetails
import com.thundermaps.apilib.android.impl.AndroidClient
import io.ktor.client.call.HttpClientCall
import io.ktor.client.call.call
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.util.KtorExperimentalAPI
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@KtorExperimentalAPI
@Singleton
class MeResourceImpl @Inject constructor(
    private val androidClient: AndroidClient,
    private val resultHandler: ResultHandler,
    private val gson: Gson
) : MeResource {
    override suspend fun getUserDetails(parameters: RequestParameters): Result<UserDetails> {
        if (!parameters.host.isInternetAvailable()) {
            return resultHandler.handleException(UnknownHostException())
        }
        val call = processCall<Unit>(
            parameters = parameters,
            methodType = HttpMethod.Get,
            query = USER_DETAILS_QUERY
        )
        return resultHandler.processResult(call, gson)
    }

    override suspend fun updateUserProfile(
        parameters: RequestParameters,
        userId: String,
        updateProfileBody: UpdateProfileBody
    ): Result<Unit> {
        if (!parameters.host.isInternetAvailable()) {
            return resultHandler.handleException(UnknownHostException())
        }
        val call = processCall(
            parameters = parameters,
            bodyRequest = updateProfileBody,
            path = USER_PATH,
            query = userId
        )
        return resultHandler.processResult(call, gson)
    }

    override suspend fun updateAddress(
        parameters: RequestParameters,
        addressBody: UpdateAddressBody
    ): Result<Unit> {
        if (!parameters.host.isInternetAvailable()) {
            return resultHandler.handleException(UnknownHostException())
        }
        val call = processCall(parameters = parameters, bodyRequest = addressBody)
        return resultHandler.processResult(call, gson)
    }

    override suspend fun updatePassword(
        parameters: RequestParameters,
        updatePasswordBody: UpdatePasswordBody
    ): Result<Unit> {
        if (!parameters.host.isInternetAvailable()) {
            return resultHandler.handleException(UnknownHostException())
        }
        val call = processCall(parameters = parameters, bodyRequest = updatePasswordBody)
        return resultHandler.processResult(call, gson)
    }

    override suspend fun updateContactNumber(
        parameters: RequestParameters,
        updateContactNumberBody: UpdateContactNumberBody
    ): Result<Unit> {
        if (!parameters.host.isInternetAvailable()) {
            return resultHandler.handleException(UnknownHostException())
        }
        val call = processCall(parameters = parameters, bodyRequest = updateContactNumberBody)
        return resultHandler.processResult(call, gson)
    }

    override suspend fun updateEmail(
        parameters: RequestParameters,
        emailBody: EmailBody
    ): Result<Unit> {
        if (!parameters.host.isInternetAvailable()) {
            return resultHandler.handleException(UnknownHostException())
        }
        val call = processCall(parameters = parameters, bodyRequest = emailBody)
        return resultHandler.processResult(call, gson)
    }

    override suspend fun updateName(
        parameters: RequestParameters,
        updateNameBody: UpdateNameBody
    ): Result<Unit> {
        if (!parameters.host.isInternetAvailable()) {
            return resultHandler.handleException(UnknownHostException())
        }
        val call = processCall(parameters = parameters, bodyRequest = updateNameBody)
        return resultHandler.processResult(call, gson)
    }

    override suspend fun updateEmailNotificationEnabled(
        parameters: RequestParameters,
        userId: String,
        updateEmailNotificationEnableBody: UpdateEmailNotificationEnableBody
    ): Result<Unit> {
        if (!parameters.host.isInternetAvailable()) {
            return resultHandler.handleException(UnknownHostException())
        }
        val call = processCall(
            parameters = parameters,
            bodyRequest = updateEmailNotificationEnableBody,
            path = USER_PATH,
            query = userId
        )
        return resultHandler.processResult(call, gson)
    }

    override suspend fun getClients(parameters: RequestParameters): Result<Clients> {
        if (!parameters.host.isInternetAvailable()) {
            return resultHandler.handleException(UnknownHostException())
        }

        val call = processCall(
            parameters = parameters,
            path = CLIENTS_PATH,
            methodType = HttpMethod.Get,
            bodyRequest = null
        )

        return resultHandler.processResult(call, gson)
    }

    override suspend fun updateFirebaseToken(
        parameters: RequestParameters,
        firebaseTokenBody: FirebaseTokenBody
    ): Result<Clients> {
        if (!parameters.host.isInternetAvailable()) {
            return resultHandler.handleException(UnknownHostException())
        }

        val call = processCall(
            parameters = parameters,
            path = CLIENTS_PATH,
            methodType = HttpMethod.Patch,
            bodyRequest = firebaseTokenBody
        )

        return resultHandler.processResult(call, gson)
    }

    private suspend fun <T : Any> processCall(
        parameters: RequestParameters,
        methodType: HttpMethod = HttpMethod.Patch,
        path: String = USER_ME_PATH,
        query: String = "",
        bodyRequest: T? = null
    ): HttpClientCall {
        val (client, requestBuilder) = androidClient.client(parameters)
        val call = client.call(HttpRequestBuilder().takeFrom(requestBuilder).apply {
            method = methodType
            url(AndroidClient.baseUrlBuilder(parameters).apply {
                encodedPath = "${encodedPath}$path$query"
            }.build())
            bodyRequest?.let {
                contentType(ContentType.Application.Json)
                body = it
            }
        })
        return call
    }

    companion object {
        const val USER_PATH = "users/"
        const val USER_ME_PATH = "users/me"
        const val CLIENTS_PATH = "clients"
        const val USER_DETAILS_QUERY = "?fields=personal_account_option"
    }
}
