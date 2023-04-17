package com.thundermaps.apilib.android.impl.resources

import android.util.Log
import com.thundermaps.apilib.android.api.com.thundermaps.apilib.android.logging.ELog
import com.thundermaps.apilib.android.api.requests.SaferMeApiError
import com.thundermaps.apilib.android.api.requests.SaferMeApiStatus
import com.thundermaps.apilib.android.impl.AndroidClient
import com.thundermaps.apilib.android.impl.resources.TestHelpers.Companion.testClient
import com.thundermaps.apilib.android.impl.resources.TestHelpers.Companion.testCreateRequest
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.toMap
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkObject
import io.mockk.mockkStatic
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Random

/**
 * Tests for the StandardMethod.create Method
 */
class StandardCreateTest {

    @MockK
    lateinit var defaultAPI: AndroidClient

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockkObject(ELog)
        every { ELog.i(any(), any()) } just Runs
        every { ELog.w(any(), any()) } just Runs
        every { ELog.e(any()) } just Runs
        mockkStatic(Log::class)
        every { Log.v(any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
    }

    /**
     * GENERAL HTTP TESTS
     * These test check that the StandardMethods 'Create' will construct the right
     * request when given various options
     */

    /** Test for correct HTTP Method Call **/
    @KtorExperimentalAPI
    @Test
    fun testCreateHTTPMethod() {
        var called = false
        testCreateRequest(
            api = defaultAPI,
            client = testClient(requestInspector = {
                assertEquals(it.method, HttpMethod.Post)
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
    fun testCreateHost() {
        var called = false
        val testHost = "TestHostString"
        val params = TestHelpers.defaultParams.copy(host = testHost)
        testCreateRequest(
            api = defaultAPI,
            client = testClient(requestInspector = {
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
    fun testCreatePath() {
        var called = false
        val testPath = "Some/Test/Path"
        val version = Random().nextInt(999)
        val params = TestHelpers.defaultParams.copy(api_version = version)
        val expectedPath = "/api/v$version/$testPath" // '/api/v' prefix automatically applied
        testCreateRequest(
            api = defaultAPI,
            path = testPath,
            params = params,
            client = testClient(
                requestInspector = {
                    assertEquals(it.url.encodedPath, expectedPath)
                    called = true
                }
            )
        )
        assertTrue(called)
    }

    /**
     * Test the correct port is used
     */
    @KtorExperimentalAPI
    @Test
    fun testCreatePort() {
        var called = false
        val testPort = Random().nextInt(65535)
        val params = TestHelpers.defaultParams.copy(port = testPort)
        testCreateRequest(
            api = defaultAPI,
            params = params,
            client = testClient(
                requestInspector = {
                    assertEquals(it.url.port, testPort)
                    called = true
                }
            )
        )
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

        testCreateRequest(
            api = defaultAPI,
            httpRequestBuilder = builder,
            client = testClient(
                requestInspector = {
                    assertTrue(it.headers.contains(name))
                    assertEquals(it.headers[name], value)
                    called = true
                }
            )
        )

        assertTrue(called)
    }

    /**
     * CREATE Functional Tests
     * These tests check that the StandardMethods 'Create' will construct the right
     * kind of object(s) for different responses, and will call the right callbacks.
     */

    /**
     * Test the the success callback is called with the correct data transformed from the HTTP Response
     */
    @KtorExperimentalAPI
    @Test
    fun testCreateSuccess() {
        var successLambdaCalls = 0
        var failLambdaCalls = 0
        val returnObject = GenericTestObject.random().toJsonString()
        val responseHeaders = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
        val client = testClient(
            content = returnObject,
            status = HttpStatusCode.Created,
            headers = responseHeaders
        )

        testCreateRequest(
            api = defaultAPI,
            client = client,
            success = {
                synchronized(successLambdaCalls) {
                    successLambdaCalls++
                }
                // Data Object returned should be equivalent to the one generated
                assertEquals(it.data.toJsonString(), returnObject)
                // Correct status type
                assertEquals(it.serverStatus, SaferMeApiStatus.CREATED)

                // Response object captures all the headers in the response
                assertEquals(it.responseHeaders, responseHeaders.toMap())
            }, failure = {
            synchronized(failLambdaCalls) { failLambdaCalls++ }
        }
        )

        // Ensure callbacks called the correct number of times
        assertEquals(successLambdaCalls, 1)
        assertEquals(failLambdaCalls, 0)
    }

    /**
     * Test the the success callback is called with the correct data transformed from the HTTP Response
     */
    @KtorExperimentalAPI
    @Test
    fun testCreateSuccessOther200() {
        var successLambdaCalls = 0
        var failLambdaCalls = 0
        val returnObject = GenericTestObject.random()
        val responseHeaders = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
        val client = testClient(
            content = returnObject.toJsonString(),
            status = HttpStatusCode.ResetContent,
            headers = responseHeaders
        )

        testCreateRequest(
            api = defaultAPI,
            client = client,
            success = {
                synchronized(successLambdaCalls) {
                    successLambdaCalls++
                }
                // Data Object returned should be equivalent to the one generated
                assertEquals(it.data.toJsonString(), returnObject.toJsonString())
                // Correct status type
                assertEquals(it.serverStatus, SaferMeApiStatus.OTHER_200)

                // Response object captures all the headers in the response
                assertEquals(it.responseHeaders, responseHeaders.toMap())
            },
            failure = {
                synchronized(failLambdaCalls) { failLambdaCalls++ }
            },
            item = returnObject
        )

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
//    @Test
    fun testCreateNonExceptionFailure() {
        // mock android log
        mockkStatic(Log::class)
        every { Log.v(any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        // Client response data:
        val responseHeaders = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
        val returnObject = GenericTestObject.random()
        val status = HttpStatusCode.UnprocessableEntity

        // Keep a count of how many times the success and fail handlers are called
        var successLambdaCalls = 0
        var failLambdaCalls = 0

        val client = testClient(
            headers = responseHeaders,
            content = returnObject.toJsonString(),
            status = status
        )

        testCreateRequest(
            api = defaultAPI,
            client = client,
            success = { synchronized(successLambdaCalls) { successLambdaCalls++ } },
            failure = {
                synchronized(failLambdaCalls) { failLambdaCalls++ }

                // We should get a SaferMeApiError Class
                assertEquals(it::class, SaferMeApiError::class)
                val error = it as SaferMeApiError

                // Correct status code
                assertEquals(error.serverStatus, SaferMeApiStatus.statusForCode(HttpStatusCode.UnprocessableEntity.value))

                // Correct response headers
                assertEquals(error.responseHeaders, responseHeaders.toMap())
            }
        )

        // Ensure callbacks called the correct number of times
        assertEquals(successLambdaCalls, 0)
        assertEquals(failLambdaCalls, 1)
    }

    // Call the Create method and test the result is correct

    /**
     * Test that a thrown exception will call the failure callback and provide the exception
     */
    @KtorExperimentalAPI
    @Test
    fun testCreateExceptionFailure() {
        // Keep a count of how many times the success and fail handlers are called

        var successLambdaCalls = 0
        var failLambdaCalls = 0

        val errorMessage = "A Test Error Message"

        val client = testClient(requestInspector = { throw Exception(errorMessage) })

        testCreateRequest(
            api = defaultAPI,
            client = client,
            success = { synchronized(successLambdaCalls) { successLambdaCalls++ } },
            failure = {
                synchronized(failLambdaCalls) { failLambdaCalls++ }

                // We should get a SaferMeApiError Class
                assertEquals(it.message, errorMessage)
            }
        )

        // Ensure callbacks called the correct number of times
        assertEquals(successLambdaCalls, 0)
        assertEquals(failLambdaCalls, 1)
    }
}
