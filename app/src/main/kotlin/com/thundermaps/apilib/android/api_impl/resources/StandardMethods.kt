package com.thundermaps.apilib.android.api_impl.resources

import android.util.Log
import com.google.gson.reflect.TypeToken
import com.thundermaps.apilib.android.api.com.thundermaps.apilib.android.logging.ELog
import com.thundermaps.apilib.android.api.com.thundermaps.apilib.android.logging.SafermeException
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.requests.SaferMeApiError
import com.thundermaps.apilib.android.api.requests.SaferMeApiResult
import com.thundermaps.apilib.android.api.requests.SaferMeApiStatus
import com.thundermaps.apilib.android.api.resources.SaferMeDatum
import com.thundermaps.apilib.android.api_impl.AndroidClient
import io.ktor.client.call.HttpClientCall
import io.ktor.client.call.call
import io.ktor.client.call.receive
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.url
import io.ktor.client.response.readBytes
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
        suspend inline fun <reified Resource : SaferMeDatum> create(
            api: AndroidClient,
            path: String,
            parameters: RequestParameters,
            item: Resource,
            crossinline success: (SaferMeApiResult<Resource>) -> Unit,
            crossinline failure: (Exception) -> Unit
        ) {

            try {
                standardCall(api, HttpMethod.Post, path, parameters, item) { call ->
                    var status = SaferMeApiStatus.statusForCode(call.response.status.value)
                    when (status) {
                        SaferMeApiStatus.CREATED -> success(
                            SaferMeApiResult(
                                data = call.receive(),
                                serverStatus = status,
                                requestHeaders = call.request.headers.toMap(),
                                responseHeaders = call.response.headers.toMap())
                        )
                        SaferMeApiStatus.ACCEPTED -> success(
                            SaferMeApiResult(
                                data = item,
                                serverStatus = status,
                                requestHeaders = call.request.headers.toMap(),
                                responseHeaders = call.response.headers.toMap())
                        )
                        SaferMeApiStatus.OTHER_200 -> success(
                            SaferMeApiResult(
                                data = item,
                                serverStatus = status,
                                requestHeaders = call.request.headers.toMap(),
                                responseHeaders = call.response.headers.toMap())
                        )
                        SaferMeApiStatus.OTHER_400 -> {
                            Log.e("sending error", call.response.readBytes().toString())
                            failure(
                                SaferMeApiError(
                                    serverStatus = status,
                                    requestHeaders = call.request.headers.toMap(),
                                    responseHeaders = call.response.headers.toMap()))
                        }
                        else -> failure(
                            SaferMeApiError(
                                serverStatus = status,
                                requestHeaders = call.request.headers.toMap(),
                                responseHeaders = call.response.headers.toMap()))
                    }
                    call.response.close()
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
        suspend inline fun <reified Resource : SaferMeDatum> read(
            api: AndroidClient,
            path: String,
            parameters: RequestParameters,
            crossinline success: (SaferMeApiResult<Resource>) -> Unit,
            crossinline failure: (Exception) -> Unit
        ) {
            try {
                standardCall(api, HttpMethod.Get, path, parameters, null) { call ->

                    var status = SaferMeApiStatus.statusForCode(call.response.status.value)
                    when (status) {
                        SaferMeApiStatus.OK -> success(
                            SaferMeApiResult(
                                data = call.receive(),
                                serverStatus = status,
                                requestHeaders = call.request.headers.toMap(),
                                responseHeaders = call.response.headers.toMap())
                        )
                        else -> failure(
                            SaferMeApiError(
                                serverStatus = status,
                                requestHeaders = call.request.headers.toMap(),
                                responseHeaders = call.response.headers.toMap()))
                    }
                    call.response.close()
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

                    var status = SaferMeApiStatus.statusForCode(call.response.status.value)
                    when (status) {
                        SaferMeApiStatus.ACCEPTED, SaferMeApiStatus.OTHER_200, SaferMeApiStatus.NO_CONTENT -> {
                            /** Unlike create, we may receive an empty body **/
                            // If this is the case, we need to set data to what was sent in the request
                            val json = String(call.response.content.toByteArray())
                            var data: Resource? =
                                if (json == "" || json.trim() == "{}") null
                                else AndroidClient.gsonSerializer.fromJson(json, Resource::class.java)
                            success(
                                SaferMeApiResult(
                                    data = data ?: item,
                                    serverStatus = status,
                                    requestHeaders = call.request.headers.toMap(),
                                    responseHeaders = call.response.headers.toMap())
                            )
                        }
                        else -> failure(
                            SaferMeApiError(
                                serverStatus = status,
                                requestHeaders = call.request.headers.toMap(),
                                responseHeaders = call.response.headers.toMap()))
                    }
                    call.response.close()
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
        suspend fun <T: SaferMeDatum>index(
            api: AndroidClient,
            path: String,
            parameters: RequestParameters,
            listType: TypeToken<List<T>>,
            success: (SaferMeApiResult<List<T>>) -> Unit,
            failure: (Exception) -> Unit
        ) {
            try {
                standardCall(api, HttpMethod.Get, path, parameters, null) { call ->
                    var status = SaferMeApiStatus.statusForCode(call.response.status.value)
                    when (status) {
                        SaferMeApiStatus.OK -> {
                            // There seems to be an issue serializing to a list natively,
                            // So we will be doing it manually
                            val json = String(call.response.content.toByteArray())
                            val result = AndroidClient.gsonSerializer.fromJson<List<T>>(json, listType.type)
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
                                serverStatus = status,
                                requestHeaders = call.request.headers.toMap(),
                                responseHeaders = call.response.headers.toMap()))
                    }
                    call.response.close()
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
                    var status = SaferMeApiStatus.statusForCode(call.response.status.value)
                    when (status) {
                        SaferMeApiStatus.ACCEPTED, SaferMeApiStatus.OTHER_200, SaferMeApiStatus.NO_CONTENT -> {
                            success(
                                SaferMeApiResult(
                                    data = item,
                                    serverStatus = status,
                                    requestHeaders = call.request.headers.toMap(),
                                    responseHeaders = call.response.headers.toMap())
                            )
                        }
                        SaferMeApiStatus.OTHER_400 -> {
                            Log.d("error", call.response.readBytes().toString())
                            failure(
                                SaferMeApiError(
                                    serverStatus = status,
                                    requestHeaders = call.request.headers.toMap(),
                                    responseHeaders = call.response.headers.toMap()))
                        }
                        else -> failure(
                            SaferMeApiError(
                                serverStatus = status,
                                requestHeaders = call.request.headers.toMap(),
                                responseHeaders = call.response.headers.toMap()))
                    }
                    call.response.close()
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
            val jsonBody =
                if (payload != null) AndroidClient.gsonSerializer.toJsonTree(payload) else null
            val call = client.call(HttpRequestBuilder().takeFrom(template).apply {
                method = requestMethod
                url(AndroidClient.baseUrlBuilder(params).apply {
                    encodedPath = "${this.encodedPath}${path}"
                }.build())
                if (jsonBody != null) {
                    contentType(ContentType.Application.Json)
                    body = jsonBody
                }
            })
            result(call)
        }
    }
}
