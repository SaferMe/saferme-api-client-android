package com.thundermaps.apilib.android.impl.resources

import com.google.gson.Gson
import com.thundermaps.apilib.android.api.com.thundermaps.isInternetAvailable
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.requests.models.EmailBody
import com.thundermaps.apilib.android.api.requests.models.UpdateAddressBody
import com.thundermaps.apilib.android.api.requests.models.UpdateContactNumberBody
import com.thundermaps.apilib.android.api.requests.models.UpdateEmailNotificationEnableBody
import com.thundermaps.apilib.android.api.requests.models.UpdateNameBody
import com.thundermaps.apilib.android.api.requests.models.UpdatePasswordBody
import com.thundermaps.apilib.android.api.resources.MeResource
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
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MeResourceImpl @Inject constructor(
    private val androidClient: AndroidClient,
    private val resultHandler: ResultHandler,
    private val gson: Gson
) : MeResource {
    override suspend fun getUserDetails(
        parameters: RequestParameters,
        userId: String
    ): Result<UserDetails> {
        if (!parameters.host.isInternetAvailable()) {
            return resultHandler.handleException(UnknownHostException())
        }
        val call = processCall<Unit>(
            parameters = parameters,
            methodType = HttpMethod.Get,
            query = "$userId?fields=personal_account_option"
        )
        return resultHandler.processResult(call, gson)
    }

    override suspend fun updateAddress(
        parameters: RequestParameters,
        userId: String,
        addressBody: UpdateAddressBody
    ): Result<Unit> {
        if (!parameters.host.isInternetAvailable()) {
            return resultHandler.handleException(UnknownHostException())
        }
        val call = processCall(
            parameters = parameters, bodyRequest = addressBody,
            query = userId
        )
        return resultHandler.processResult(call, gson)
    }

    override suspend fun updatePassword(
        parameters: RequestParameters,
        userId: String,
        updatePasswordBody: UpdatePasswordBody
    ): Result<Unit> {
        if (!parameters.host.isInternetAvailable()) {
            return resultHandler.handleException(UnknownHostException())
        }
        val call = processCall(
            parameters = parameters,
            bodyRequest = updatePasswordBody,
            query = userId
        )
        return resultHandler.processResult(call, gson)
    }

    override suspend fun updateContactNumber(
        parameters: RequestParameters,
        userId: String,
        updateContactNumberBody: UpdateContactNumberBody
    ): Result<Unit> {
        if (!parameters.host.isInternetAvailable()) {
            return resultHandler.handleException(UnknownHostException())
        }
        val call =
            processCall(
                parameters = parameters, bodyRequest = updateContactNumberBody,
                query = userId
            )
        return resultHandler.processResult(call, gson)
    }

    override suspend fun updateEmail(
        parameters: RequestParameters,
        userId: String,
        emailBody: EmailBody
    ): Result<Unit> {
        if (!parameters.host.isInternetAvailable()) {
            return resultHandler.handleException(UnknownHostException())
        }
        val call = processCall(
            parameters = parameters, bodyRequest = emailBody,
            query = userId
        )
        return resultHandler.processResult(call, gson)
    }

    override suspend fun updateName(
        parameters: RequestParameters,
        userId: String,
        updateNameBody: UpdateNameBody
    ): Result<Unit> {
        if (!parameters.host.isInternetAvailable()) {
            return resultHandler.handleException(UnknownHostException())
        }
        val call = processCall(
            parameters = parameters, bodyRequest = updateNameBody,
            query = userId
        )
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
        val call =
            processCall(
                parameters = parameters, bodyRequest = updateEmailNotificationEnableBody,
                query = userId
            )
        return resultHandler.processResult(call, gson)
    }

    private suspend fun <T : Any> processCall(
        parameters: RequestParameters,
        methodType: HttpMethod = HttpMethod.Patch,
        query: String = "",
        bodyRequest: T? = null
    ): HttpClientCall {
        val (client, requestBuilder) = androidClient.client(parameters)
        val call = client.call(HttpRequestBuilder().takeFrom(requestBuilder).apply {
            method = methodType
            url(AndroidClient.baseUrlBuilder(parameters).apply {
                encodedPath = "${encodedPath}users/$query"
            }.build())
            bodyRequest?.let {
                contentType(ContentType.Application.Json)
                body = it
            }
        })
        return call
    }
}
