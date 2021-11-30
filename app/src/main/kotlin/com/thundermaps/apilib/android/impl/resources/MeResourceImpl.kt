package com.thundermaps.apilib.android.impl.resources

import com.google.gson.Gson
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.requests.models.EmailBody
import com.thundermaps.apilib.android.api.requests.models.UpdateAddressBody
import com.thundermaps.apilib.android.api.requests.models.UpdateContactNumberBody
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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MeResourceImpl @Inject constructor(
    private val androidClient: AndroidClient,
    private val resultHandler: ResultHandler,
    private val gson: Gson
) : MeResource {
    override suspend fun getUserDetails(parameters: RequestParameters): Result<UserDetails> {
        val call = processCall<Unit>(
            parameters = parameters,
            methodType = HttpMethod.Get,
            "?fields=personal_account_option"
        )
        return resultHandler.processResult(call, gson)
    }

    override suspend fun updateAddress(
        parameters: RequestParameters,
        addressBody: UpdateAddressBody
    ): Result<Unit> {
        val call = processCall(parameters = parameters, bodyRequest = addressBody)
        return resultHandler.processResult(call, gson)
    }

    override suspend fun updatePassword(
        parameters: RequestParameters,
        updatePasswordBody: UpdatePasswordBody
    ): Result<Unit> {
        val call =
            processCall(parameters = parameters, bodyRequest = updatePasswordBody)
        return resultHandler.processResult(call, gson)
    }

    override suspend fun updateContactNumber(
        parameters: RequestParameters,
        updateContactNumberBody: UpdateContactNumberBody
    ): Result<Unit> {
        val call =
            processCall(parameters = parameters, bodyRequest = updateContactNumberBody)
        return resultHandler.processResult(call, gson)
    }

    override suspend fun updateEmail(
        parameters: RequestParameters,
        emailBody: EmailBody
    ): Result<Unit> {
        val call = processCall(parameters = parameters, bodyRequest = emailBody)
        return resultHandler.processResult(call, gson)
    }

    override suspend fun updateName(
        parameters: RequestParameters,
        updateNameBody: UpdateNameBody
    ): Result<Unit> {
        val call = processCall(parameters = parameters, bodyRequest = updateNameBody)
        return resultHandler.processResult(call, gson)
    }

    private suspend inline fun <T : Any> processCall(
        parameters: RequestParameters,
        methodType: HttpMethod = HttpMethod.Patch,
        query: String = "",
        bodyRequest: T? = null
    ): HttpClientCall {
        val (client, requestBuilder) = androidClient.client(parameters)
        val call = client.call(HttpRequestBuilder().takeFrom(requestBuilder).apply {
            method = methodType
            url(AndroidClient.baseUrlBuilder(parameters).apply {
                encodedPath = "${encodedPath}users/me$query"
            }.build())
            bodyRequest?.let {
                contentType(ContentType.Application.Json)
                body = it
            }
        })
        return call
    }
}
