package com.thundermaps.apilib.android.impl.resources

import android.util.Log
import com.thundermaps.apilib.android.api.requests.SaferMeApiError
import com.thundermaps.apilib.android.api.requests.SaferMeApiStatus
import com.thundermaps.apilib.android.impl.AndroidClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.toMap
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import java.util.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class StandardIndexTest {
    @MockK
    lateinit var defaultAPI: AndroidClient

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockkStatic(Log::class)
        every { Log.v(any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
    }

    /**
     * GENERAL HTTP TESTS
     * These test check that the StandardMethods 'Index' will construct the right
     * request when given various options
     */

    /** Test for correct HTTP Method Call **/
    @KtorExperimentalAPI
    @Test
    fun testIndexHTTPMethod() {
        var called = false
        TestHelpers.testIndexRequest(
            api = defaultAPI,
            client = TestHelpers.testClient(requestInspector = {
                assertEquals(it.method, HttpMethod.Get)
                called = true
            })
        )
        assertTrue(called)
    }

    /**
     * Test the correct host is used
     */
    @KtorExperimentalAPI
    @Test
    fun testIndexHost() {
        var called = false
        val testHost = "TestHostString"
        val params = TestHelpers.defaultParams.copy(host = testHost)
        TestHelpers.testIndexRequest(
            api = defaultAPI,
            client = TestHelpers.testClient(requestInspector = {
                assertEquals(it.url.host, testHost)
                called = true
            }),
            params = params
        )
        assertTrue(called)
    }

    /**
     * Test the correct path is used
     */
    @KtorExperimentalAPI
    @Test
    fun testIndexPath() {
        var called = false
        val testPath = "Some/Test/Path"
        val version = Random().nextInt(999)
        val params = TestHelpers.defaultParams.copy(api_version = version)
        val expectedPath = "/api/v$version/$testPath" // '/api/v' prefix automatically applied
        TestHelpers.testIndexRequest(
            api = defaultAPI,
            path = testPath,
            params = params,
            client = TestHelpers.testClient(requestInspector = {
                assertEquals(it.url.encodedPath, expectedPath)
                called = true
            }
            ))
        assertTrue(called)
    }

    /**
     * Test the correct port is used
     */
    @KtorExperimentalAPI
    @Test
    fun testIndexPort() {
        var called = false
        val testPort = Random().nextInt(65535)
        val params = TestHelpers.defaultParams.copy(port = testPort)
        TestHelpers.testIndexRequest(
            api = defaultAPI,
            params = params,
            client = TestHelpers.testClient(requestInspector = {
                assertEquals(it.url.port, testPort)
                called = true
            }
            ))
        assertTrue(called)
    }

    /**
     * Test Request Builder is correctly applied
     */
    @KtorExperimentalAPI
    @Test
    fun testRequestBuilderHeaders() {
        var called = false
        val name = "A_Random-TestName"
        val value = "ARandom-Test_Value!"
        val builder = HttpRequestBuilder().apply {
            headers.append(name, value)
        }

        TestHelpers.testIndexRequest(
            api = defaultAPI,
            httpRequestBuilder = builder,
            client = TestHelpers.testClient(requestInspector = {
                assertTrue(it.headers.contains(name))
                assertEquals(it.headers[name], value)
                called = true
            }
            ))
        assertTrue(called)
    }

    /**
     * CREATE Functional Tests
     * These tests check that the StandardMethods 'Index' will construct the right
     * kind of object(s) for different responses, and will call the right callbacks.
     */

    /**
     * Test the the success callback is called with the correct data transformed from the HTTP Response
     */
    @KtorExperimentalAPI
    @Test
    fun testIndexSuccess() {
        var successLambdaCalls = 0
        var failLambdaCalls = 0
        val returnList = GenericTestObject.randomList()
        val returnJson = GenericTestObject.listToJson(returnList)
        val responseHeaders = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))

        val client = TestHelpers.testClient(
            content = returnJson,
            status = HttpStatusCode.OK,
            headers = responseHeaders
        )

        TestHelpers.testIndexRequest(
            api = defaultAPI,
            client = client,
            success = {
                synchronized(successLambdaCalls) {
                    successLambdaCalls++
                }

                // List returned should contain the same elements as the return list
                val actualList = it.data
                actualList.mapIndexed { i, v -> assertEquals(returnList[i], v) }

                assertEquals(returnList.size, actualList.size)

                // Correct status type
                assertEquals(it.serverStatus, SaferMeApiStatus.OK)

                // Response object captures all the headers in the response
                assertEquals(it.responseHeaders, responseHeaders.toMap())
            }, failure = {
                synchronized(failLambdaCalls) { failLambdaCalls++ }
            })

        // Ensure callbacks called the correct number of times
        assertEquals(successLambdaCalls, 1)
        assertEquals(failLambdaCalls, 0)
    }

    /**
     * Test that a valid request with an unexpected response generates the correct
     * API Exception with useful data. this is non-exceptional because no actual Exception
     * is involved, just an unexpected response
     */
    @KtorExperimentalAPI
    @Test
    fun testIndexNonExceptionFailure() {
        // Client response data:
        val responseHeaders = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
        val status = HttpStatusCode.BadRequest

        // Keep a count of how many times the success and fail handlers are called
        var successLambdaCalls = 0
        var failLambdaCalls = 0

        val client = TestHelpers.testClient(
            headers = responseHeaders,
            content = "",
            status = status
        )

        TestHelpers.testIndexRequest(
            api = defaultAPI,
            client = client,
            success = { synchronized(successLambdaCalls) { successLambdaCalls++ } },
            failure = {
                synchronized(failLambdaCalls) { failLambdaCalls++ }

                // We should get a SaferMeApiError Class
                assertEquals(it::class, SaferMeApiError::class)
                val error = it as SaferMeApiError

                // Correct status code
                assertEquals(
                    error.serverStatus,
                    SaferMeApiStatus.statusForCode(HttpStatusCode.BadRequest.value)
                )

                // Correct response headers
                assertEquals(error.responseHeaders, responseHeaders.toMap())
            })

        // Ensure callbacks called the correct number of times
        assertEquals(successLambdaCalls, 0)
        assertEquals(failLambdaCalls, 1)
    }

    /**
     * Test that a thrown exception will call the failure callback and provide the exception
     */
    @KtorExperimentalAPI
    @Test
    fun testIndexExceptionFailure() {
        // Keep a count of how many times the success and fail handlers are called

        var successLambdaCalls = 0
        var failLambdaCalls = 0

        val errorMessage = "A Test Error Message"

        val client = TestHelpers.testClient(requestInspector = { throw Exception(errorMessage) })

        TestHelpers.testIndexRequest(
            api = defaultAPI,
            client = client,
            success = { synchronized(successLambdaCalls) { successLambdaCalls++ } },
            failure = {
                synchronized(failLambdaCalls) { failLambdaCalls++ }

                // We should get a SaferMeApiError Class
                assertEquals(it.message, errorMessage)
            })

        // Ensure callbacks called the correct number of times
        assertEquals(successLambdaCalls, 0)
        assertEquals(failLambdaCalls, 1)
    }
}
