package com.thundermaps.apilib.android.impl.resources

import com.thundermaps.apilib.android.api.com.thundermaps.apilib.android.logging.ELog
import com.thundermaps.apilib.android.api.com.thundermaps.apilib.android.logging.SafermeException
import com.thundermaps.apilib.android.api.fromJsonString
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.requests.SaferMeApiError
import com.thundermaps.apilib.android.api.requests.SaferMeApiResult
import com.thundermaps.apilib.android.api.requests.SaferMeApiStatus
import com.thundermaps.apilib.android.api.resources.SaferMeDatum
import com.thundermaps.apilib.android.impl.AndroidClient
import com.thundermaps.apilib.android.impl.AndroidClient.Companion.gsonSerializer
import io.ktor.client.call.HttpClientCall
import io.ktor.client.call.receive
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.util.toByteArray
import io.ktor.util.toMap

class StandardMethods {
    companion object {

        /**
         * Standardized RESTful create call. Uses POST, expects a valid item to send
         * to the server.
         *
         * @param api Reference to client
         * @param path The endpoint to send the request to
         * @param parameters Request parameters to use
         * @param item The resource data to create on the server
         * @param success Invoked if the request was successful
         * @param failure Invoked if the request failed
         */
        suspend inline fun <reified T : Any> create(
            api: AndroidClient,
            path: String,
            parameters: RequestParameters,
            item: T,
            crossinline success: (SaferMeApiResult<T>) -> Unit,
            crossinline failure: (Exception) -> Unit
        ) {
            try {
                standardCall(api, HttpMethod.Post, path, parameters, item) { call ->
                    when (val status = SaferMeApiStatus.statusForCode(call.response.status.value)) {
                        SaferMeApiStatus.CREATED -> success(
                            SaferMeApiResult(
                                data = call.receive(),
                                serverStatus = status,
                                requestHeaders = call.request.headers.toMap(),
                                responseHeaders = call.response.headers.toMap()
                            )
                        )
                        SaferMeApiStatus.ACCEPTED -> success(
                            SaferMeApiResult(
                                data = item,
                                serverStatus = status,
                                requestHeaders = call.request.headers.toMap(),
                                responseHeaders = call.response.headers.toMap()
                            )
                        )
                        SaferMeApiStatus.OTHER_200 -> success(
                            SaferMeApiResult(
                                data = item,
                                serverStatus = status,
                                requestHeaders = call.request.headers.toMap(),
                                responseHeaders = call.response.headers.toMap()
                            )
                        )
                        SaferMeApiStatus.OTHER_400 -> {
                            failure(
                                SaferMeApiError(
                                    statusCode = call.response.status.value,
                                    serverStatus = status,
                                    requestHeaders = call.request.headers.toMap(),
                                    responseHeaders = call.response.headers.toMap(),
                                    url = path,
                                    error = call.response.readText()
                                )
                            )
                        }
                        else -> failure(
                            SaferMeApiError(
                                statusCode = call.response.status.value,
                                serverStatus = status,
                                requestHeaders = call.request.headers.toMap(),
                                responseHeaders = call.response.headers.toMap(),
                                url = path,
                                error = call.response.readText()
                            )
                        )
                    }
                }
            } catch (ex: Exception) {
                if (ex.message != null) {
                    ELog.e(SafermeException.Builder(th = ex, message = ex.message!!).build())
                } else {
                    ELog.e(SafermeException.Builder(th = ex).build())
                }
                failure(ex)
            }
        }

        /**
         * Standardized RESTful GET call. Uses GET, expects the path to reference a valid
         * resoure on the server.
         *
         * @param api Reference to client
         * @param path The endpoint to send the request to
         * @param parameters Request parameters to use
         * @param item The resource data to create on the server
         * @param success Invoked if the request was successful
         * @param failure Invoked if the request failed
         */
        suspend inline fun <reified T : Any> read(
            api: AndroidClient,
            path: String,
            parameters: RequestParameters,
            crossinline success: (SaferMeApiResult<T>) -> Unit,
            crossinline failure: (Exception) -> Unit
        ) {
            try {
                standardCall(api, HttpMethod.Get, path, parameters, null) { call ->

                    when (val status = SaferMeApiStatus.statusForCode(call.response.status.value)) {
                        SaferMeApiStatus.OK -> success(
                            SaferMeApiResult(
                                data = call.receive(),
                                serverStatus = status,
                                requestHeaders = call.request.headers.toMap(),
                                responseHeaders = call.response.headers.toMap()
                            )
                        )
                        else -> failure(
                            SaferMeApiError(
                                statusCode = call.response.status.value,
                                serverStatus = status,
                                requestHeaders = call.request.headers.toMap(),
                                responseHeaders = call.response.headers.toMap(),
                                url = path,
                                error = call.response.readText()
                            )
                        )
                    }
                }
            } catch (ex: Exception) {
                if (ex.message != null) {
                    ELog.e(SafermeException.Builder(th = ex, message = ex.message!!).build())
                } else {
                    ELog.e(SafermeException.Builder(th = ex).build())
                }
                failure(ex)
            }
        }

        /**
         * Standardized RESTful update call. Uses PATCH, expects a valid item to send
         * to the server.
         *
         * @param api Reference to client
         * @param path The endpoint to send the request to
         * @param parameters Request parameters to use
         * @param item The resource data to create on the server
         * @param success Invoked if the request was successful
         * @param failure Invoked if the request failed
         */
        suspend inline fun <reified Resource : SaferMeDatum> update(
            api: AndroidClient,
            path: String,
            parameters: RequestParameters,
            item: Resource,
            crossinline success: (SaferMeApiResult<Resource>) -> Unit,
            crossinline failure: (Exception) -> Unit
        ) {
            try {
                standardCall(api, HttpMethod.Patch, path, parameters, item) { call ->

                    when (val status = SaferMeApiStatus.statusForCode(call.response.status.value)) {
                        SaferMeApiStatus.ACCEPTED, SaferMeApiStatus.OTHER_200, SaferMeApiStatus.NO_CONTENT -> {
                            /** Unlike create, we may receive an empty body **/
                            // If this is the case, we need to set data to what was sent in the request
                            val json = String(call.response.content.toByteArray())
                            val data: Resource? =
                                if (json == "" || json.trim() == "{}") null
                                else gsonSerializer.fromJsonString(json)
                            success(
                                SaferMeApiResult(
                                    data = data ?: item,
                                    serverStatus = status,
                                    requestHeaders = call.request.headers.toMap(),
                                    responseHeaders = call.response.headers.toMap()
                                )
                            )
                        }
                        else -> failure(
                            SaferMeApiError(
                                statusCode = call.response.status.value,
                                serverStatus = status,
                                requestHeaders = call.request.headers.toMap(),
                                responseHeaders = call.response.headers.toMap(),
                                url = path,
                                error = call.response.readText()
                            )
                        )
                    }
                }
            } catch (ex: Exception) {
                if (ex.message != null) {
                    ELog.e(SafermeException.Builder(th = ex, message = ex.message!!).build())
                } else {
                    ELog.e(SafermeException.Builder(th = ex).build())
                }
                failure(ex)
            }
        }

        /**
         * Standardized RESTful index call. Uses GET, expects a valid index path.
         * A little different fro the others as the serializer has trouble working
         * out which type of list to create due to the generics, so an explicit type
         * needs to be provided.
         *
         * @param api Reference to client
         * @param path The endpoint to send the request to
         * @param parameters Request parameters to use
         * @param success Invoked if the request was successful
         * @param failure Invoked if the request failed
         */
        suspend inline fun <reified T : Any> index(
            api: AndroidClient,
            path: String,
            parameters: RequestParameters,
            success: (SaferMeApiResult<T>) -> Unit,
            failure: (Exception) -> Unit
        ) {
            try {
                standardCall(api, HttpMethod.Get, path, parameters, null) { call ->
                    when (val status = SaferMeApiStatus.statusForCode(call.response.status.value)) {
                        SaferMeApiStatus.OK -> {
                            val json = String(call.response.content.toByteArray())
                            val result = gsonSerializer.fromJsonString<T>(json)
                            success(
                                SaferMeApiResult(
                                    data = result,
                                    serverStatus = status,
                                    requestHeaders = call.request.headers.toMap(),
                                    responseHeaders = call.response.headers.toMap()
                                )
                            )
                        }
                        else -> failure(
                            SaferMeApiError(
                                statusCode = call.response.status.value,
                                serverStatus = status,
                                requestHeaders = call.request.headers.toMap(),
                                responseHeaders = call.response.headers.toMap(),
                                url = path,
                                error = call.response.readText()
                            )
                        )
                    }
                }
            } catch (ex: Exception) {
                if (ex.message != null) {
                    ELog.e(SafermeException.Builder(th = ex, message = ex.message!!).build())
                } else {
                    ELog.e(SafermeException.Builder(th = ex).build())
                }
                failure(ex)
            }
        }

        /**
         * Standardized RESTful update call. Uses PATCH, expects a valid item to send
         * to the server.
         *
         * @param api Reference to client
         * @param path The endpoint to send the request to
         * @param parameters Request parameters to use
         * @param item The resource data to create on the server
         * @param success Invoked if the request was successful
         * @param failure Invoked if the request failed
         */
        public suspend inline fun <reified Resource : SaferMeDatum> delete(
            api: AndroidClient,
            path: String,
            parameters: RequestParameters,
            item: Resource,
            crossinline success: (SaferMeApiResult<Resource>) -> Unit,
            crossinline failure: (Exception) -> Unit
        ) {
            try {
                standardCall(api, HttpMethod.Delete, path, parameters, item) { call ->
                    when (val status = SaferMeApiStatus.statusForCode(call.response.status.value)) {
                        SaferMeApiStatus.ACCEPTED, SaferMeApiStatus.OTHER_200, SaferMeApiStatus.NO_CONTENT -> {
                            success(
                                SaferMeApiResult(
                                    data = item,
                                    serverStatus = status,
                                    requestHeaders = call.request.headers.toMap(),
                                    responseHeaders = call.response.headers.toMap()
                                )
                            )
                        }
                        SaferMeApiStatus.OTHER_400 -> {
                            failure(
                                SaferMeApiError(
                                    statusCode = call.response.status.value,
                                    serverStatus = status,
                                    requestHeaders = call.request.headers.toMap(),
                                    responseHeaders = call.response.headers.toMap(),
                                    url = path,
                                    error = call.response.readText()
                                )
                            )
                        }
                        else -> failure(
                            SaferMeApiError(
                                statusCode = call.response.status.value,
                                serverStatus = status,
                                requestHeaders = call.request.headers.toMap(),
                                responseHeaders = call.response.headers.toMap(),
                                url = path,
                                error = call.response.readText()
                            )
                        )
                    }
                }
            } catch (ex: Exception) {
                if (ex.message != null) {
                    ELog.e(SafermeException.Builder(th = ex, message = ex.message!!).build())
                } else {
                    ELog.e(SafermeException.Builder(th = ex).build())
                }
                failure(ex)
            }
        }

        // Commonly pattern for HttpRequests
        suspend inline fun <T> standardCall(
            api: AndroidClient,
            requestMethod: HttpMethod,
            path: String,
            params: RequestParameters,
            payload: T?,
            result: (call: HttpClientCall) -> Unit
        ) {
            val (client, template) = api.client(params)
            val jsonBody = payload?.let {
                gsonSerializer.toJsonTree(payload)
            }

            val call = client.request<HttpResponse> (
                HttpRequestBuilder().takeFrom(template).apply {
                    method = requestMethod
                    url(
                        AndroidClient.baseUrlBuilder(params).apply {
                            encodedPath = "${this.encodedPath}$path"
                        }.build()
                    )
                    if (jsonBody != null) {
                        contentType(ContentType.Application.Json)
                        body = jsonBody
                    }
                }
            ).call

            ELog.i("Http", "${call.response.status}: ${requestMethod.value} [$path]\n- ${call.response.readText()}\n- $payload")
            result(call)
        }
    }
}
