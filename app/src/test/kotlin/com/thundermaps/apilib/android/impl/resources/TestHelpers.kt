package com.thundermaps.apilib.android.impl.resources

import com.thundermaps.apilib.android.api.SaferMeCredentials
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.requests.SaferMeApiResult
import com.thundermaps.apilib.android.impl.AndroidClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.HttpRequestData
import io.ktor.http.Headers
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.util.KtorExperimentalAPI
import io.mockk.every
import java.util.concurrent.ThreadLocalRandom
import kotlin.streams.asSequence
import kotlinx.coroutines.runBlocking

class TestHelpers {
    companion object {

        // Generic parameters that can be used as a template
        val defaultParams = RequestParameters(
            emptyMap(),
            SaferMeCredentials("", "", "", null, ""),
            "",
            null, 0)

        /**
         * Helper method - will create a mocked HTTP Client using the provided options.
         *
         * The inspector will be provided the constructed request data at the time the HTTP request call
         * is made - this is a useful place to put assertions
         */
        fun testClient(
            requestInspector: (HttpRequestData) -> Unit = {},
            content: String = "",
            status: HttpStatusCode = HttpStatusCode.OK,
            headers: Headers = headersOf()
        ): HttpClient {
            return HttpClient(MockEngine) {
                engine {
                    addHandler { request ->
                        // allows caller to make assertions on the request
                        requestInspector(request)

                        // allows caller to specify the response
                        respond(content, status, headers)
                    }
                }

                install(JsonFeature) {
                    serializer = GsonSerializer().apply { AndroidClient.gsonBuilder }
                }
            }
        }

        /**
         * Helper method - will create a generic create request using the provided options.
         * Default options will work, so you only need to override the options you want to test
         */
        @KtorExperimentalAPI
        fun testCreateRequest(
            api: AndroidClient,
            client: HttpClient = testClient(),
            path: String = "test",
            params: RequestParameters = defaultParams,
            credentials: SaferMeCredentials? = defaultParams.credentials,
            item: GenericTestObject = GenericTestObject.random(),
            httpRequestBuilder: HttpRequestBuilder = HttpRequestBuilder(),
            success: (SaferMeApiResult<GenericTestObject>) -> Unit = {},
            failure: (Exception) -> Unit = {}
        ) {

            // Copy provided credentials if they differ
            val paramsToSend = if (params.credentials != credentials) {
                params.copy(credentials = credentials)
            } else params

            // Make sure the mock client is used
            every {
                api.client(any())
            } answers {
                Pair(client, httpRequestBuilder)
            }

            // Call the Create method and test the result is correct
            runBlocking {
                StandardMethods.create(api, path, paramsToSend, item, success, failure)
            }
        }

        /**
         * Helper method - will create a generic create request using the provided options.
         * Default options will work, so you only need to override the options you want to test
         */
        @KtorExperimentalAPI
        fun testDeleteRequest(
            api: AndroidClient,
            client: HttpClient = testClient(),
            path: String = "test",
            params: RequestParameters = defaultParams,
            credentials: SaferMeCredentials? = defaultParams.credentials,
            item: GenericTestObject = GenericTestObject.random(),
            httpRequestBuilder: HttpRequestBuilder = HttpRequestBuilder(),
            success: (SaferMeApiResult<GenericTestObject>) -> Unit = {},
            failure: (Exception) -> Unit = {}
        ) {

            // Copy provided credentials if they differ
            val paramsToSend = if (params.credentials != credentials) {
                params.copy(credentials = credentials)
            } else params

            // Make sure the mock client is used
            every {
                api.client(any())
            } answers {
                Pair(client, httpRequestBuilder)
            }

            // Call the Create method and test the result is correct
            runBlocking {
                StandardMethods.delete(api, path, paramsToSend, item, success, failure)
            }
        }

        /**
         * Helper method - will create a generic update request using the provided options.
         * Default options will work, so you only need to override the options you want to test
         */
        @KtorExperimentalAPI
        fun testUpdateRequest(
            api: AndroidClient,
            client: HttpClient = testClient(),
            path: String = "test",
            params: RequestParameters = defaultParams,
            credentials: SaferMeCredentials? = defaultParams.credentials,
            item: GenericTestObject = GenericTestObject.random(),
            httpRequestBuilder: HttpRequestBuilder = HttpRequestBuilder(),
            success: (SaferMeApiResult<GenericTestObject>) -> Unit = {},
            failure: (Exception) -> Unit = {}
        ) {

            // Copy provided credentials if they differ
            val paramsToSend = if (params.credentials != credentials) {
                params.copy(credentials = credentials)
            } else params

            // Make sure the mock client is used
            every {
                api.client(any())
            } answers {
                Pair(client, httpRequestBuilder)
            }

            // Call the Create method and test the result is correct
            runBlocking {
                StandardMethods.update(api, path, paramsToSend, item, success, failure)
            }
        }

        /**
         * Helper method - will create a generic read request using the provided options.
         * Default options will work, so you only need to override the options you want to test
         */
        @KtorExperimentalAPI
        fun testReadRequest(
            api: AndroidClient,
            client: HttpClient = testClient(),
            path: String = "test/123",
            params: RequestParameters = defaultParams,
            credentials: SaferMeCredentials? = defaultParams.credentials,
            httpRequestBuilder: HttpRequestBuilder = HttpRequestBuilder(),
            success: (SaferMeApiResult<GenericTestObject>) -> Unit = {},
            failure: (Exception) -> Unit = {}
        ) {

            // Copy provided credentials if they differ
            val paramsToSend = if (params.credentials != credentials) {
                params.copy(credentials = credentials)
            } else params

            // Make sure the mock client is used
            every {
                api.client(any())
            } answers {
                Pair(client, httpRequestBuilder)
            }

            // Call the Create method and test the result is correct
            runBlocking {
                StandardMethods.read(api, path, paramsToSend, success, failure)
            }
        }

        /**
         * Helper method - will create a generic create request using the provided options.
         * Default options will work, so you only need to override the options you want to test
         */
        @KtorExperimentalAPI
        fun testIndexRequest(
            api: AndroidClient,
            client: HttpClient = testClient(),
            path: String = "test",
            params: RequestParameters = defaultParams,
            credentials: SaferMeCredentials? = defaultParams.credentials,
            httpRequestBuilder: HttpRequestBuilder = HttpRequestBuilder(),
            success: (SaferMeApiResult<List<GenericTestObject>>) -> Unit = {},
            failure: (Exception) -> Unit = {}
        ) {

            // Copy provided credentials if they differ
            val paramsToSend = if (params.credentials != credentials) {
                params.copy(credentials = credentials)
            } else params

            // Make sure the mock client is used
            every {
                api.client(any())
            } answers {
                Pair(client, httpRequestBuilder)
            }

            // Call the Create method and test the result is correct
            runBlocking {
                StandardMethods.index(api = api,
                    path = path,
                    parameters = paramsToSend,
                    listType = GenericTestObject.GenericListToken(),
                    success = success,
                    failure = failure)
            }
        }

        private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9') + '-' + ' ' + '_'
        fun randomString(length: Long = 10): String {
            return ThreadLocalRandom.current()
                .ints(length, 0, charPool.size)
                .asSequence()
                .map(charPool::get)
                .joinToString("")
        }
    }
}
