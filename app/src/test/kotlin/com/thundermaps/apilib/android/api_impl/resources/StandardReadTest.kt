package com.thundermaps.apilib.android.api_impl.resources

import android.util.Log
import com.thundermaps.apilib.android.api.requests.SaferMeApiError
import com.thundermaps.apilib.android.api.requests.SaferMeApiStatus
import com.thundermaps.apilib.android.api_impl.AndroidClient
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
import junit.framework.Assert.assertTrue
import junit.framework.TestCase
import org.junit.Before
import org.junit.Test

class StandardReadTest {

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
     * These test check that the StandardMethods 'Read' will construct the right
     * request when given various options
     */

    /** Test for correct HTTP Method Call **/
    @KtorExperimentalAPI
    @Test
    fun testReadHTTPMethod() {
        var called = false
        TestHelpers.testReadRequest(
            api = defaultAPI,
            client = TestHelpers.testClient(requestInspector = {
                TestCase.assertEquals(it.method, HttpMethod.Get)
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
    fun testReadHost() {
        var called = false
        val testHost = "TestHostString"
        val params = TestHelpers.defaultParams.copy(host = testHost)
        TestHelpers.testReadRequest(
            api = defaultAPI,
            client = TestHelpers.testClient(requestInspector = {
                TestCase.assertEquals(it.url.host, testHost)
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
    fun testReadPath() {
        val testPath = "Some/Test/Path"
        val version = Random().nextInt(999)
        val params = TestHelpers.defaultParams.copy(api_version = version)
        val expectedPath = "/api/v$version/$testPath" // '/api/v' prefix automatically applied
        TestHelpers.testReadRequest(
            api = defaultAPI,
            path = testPath,
            params = params,
            client = TestHelpers.testClient(requestInspector = {
                TestCase.assertEquals(it.url.encodedPath, expectedPath)
            }
            ))
    }

    /**
     * Test the correct port is used
     */
    @KtorExperimentalAPI
    @Test
    fun testReadPort() {
        var called = false
        val testPort = Random().nextInt(65535)
        val params = TestHelpers.defaultParams.copy(port = testPort)
        TestHelpers.testReadRequest(
            api = defaultAPI,
            params = params,
            client = TestHelpers.testClient(requestInspector = {
                TestCase.assertEquals(it.url.port, testPort)
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

        TestHelpers.testReadRequest(
            api = defaultAPI,
            httpRequestBuilder = builder,
            client = TestHelpers.testClient(requestInspector = {
                TestCase.assertTrue(it.headers.contains(name))
                TestCase.assertEquals(it.headers[name], value)
                called = true
            }
            ))
        assertTrue(called)
    }

    /**
     * READ Functional Tests
     * These tests check that the StandardMethods 'Read' will construct the right
     * kind of object(s) for different responses, and will call the right callbacks.
     */

    /**
     * Test the the success callback is called with the correct data transformed from the HTTP Response
     */
    @KtorExperimentalAPI
    @Test
    fun testReadSuccess() {
        var successLambdaCalls = 0
        var failLambdaCalls = 0
        val returnObject = GenericTestObject.random().toJsonString()
        val responseHeaders = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))

        val client = TestHelpers.testClient(
            content = returnObject,
            status = HttpStatusCode.OK,
            headers = responseHeaders
        )

        TestHelpers.testReadRequest(
            api = defaultAPI,
            client = client,
            success = {
                synchronized(successLambdaCalls) {
                    successLambdaCalls++
                }

                // Data Object returned should be equivalent to the one generated
                TestCase.assertEquals(it.data.toJsonString(), returnObject)

                // Correct status type
                TestCase.assertEquals(it.serverStatus, SaferMeApiStatus.OK)

                // Response object captures all the headers in the response
                TestCase.assertEquals(it.responseHeaders, responseHeaders.toMap())
            }, failure = {
                synchronized(failLambdaCalls) { failLambdaCalls++ }
            })

        // Ensure callbacks called the correct number of times
        TestCase.assertEquals(successLambdaCalls, 1)
        TestCase.assertEquals(failLambdaCalls, 0)
    }

    /**
     * Test that a valid request with an unexpected response generates the correct
     * API Exception with useful data. this is non-exceptional because no actual Exception
     * is involved, just an unexpected response
     */
    @KtorExperimentalAPI
    @Test
    fun testNonExceptionFailure() {
        // Client response data:
        val responseHeaders = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
        val returnObject = GenericTestObject.random()
        val status = HttpStatusCode.NotFound

        // Keep a count of how many times the success and fail handlers are called
        var successLambdaCalls = 0
        var failLambdaCalls = 0

        val client = TestHelpers.testClient(
            headers = responseHeaders,
            content = returnObject.toJsonString(),
            status = status
        )

        TestHelpers.testReadRequest(
            api = defaultAPI,
            client = client,
            success = { synchronized(successLambdaCalls) { successLambdaCalls++ } },
            failure = {
                synchronized(failLambdaCalls) { failLambdaCalls++ }

                // We should get a SaferMeApiError Class
                TestCase.assertEquals(it::class, SaferMeApiError::class)
                val error = it as SaferMeApiError

                // Correct status code
                TestCase.assertEquals(
                    error.serverStatus,
                    SaferMeApiStatus.statusForCode(HttpStatusCode.NotFound.value)
                )

                // Correct response headers
                TestCase.assertEquals(error.responseHeaders, responseHeaders.toMap())
            })

        // Ensure callbacks called the correct number of times
        TestCase.assertEquals(successLambdaCalls, 0)
        TestCase.assertEquals(failLambdaCalls, 1)
    }

    /**
     * Test that a thrown exception will call the failure callback and provide the exception
     */
    @KtorExperimentalAPI
    @Test
    fun testExceptionFailure() {
        // Keep a count of how many times the success and fail handlers are called

        var successLambdaCalls = 0
        var failLambdaCalls = 0

        val errorMessage = "A Test Error Message"

        val client = TestHelpers.testClient(requestInspector = { throw Exception(errorMessage) })

        TestHelpers.testReadRequest(
            api = defaultAPI,
            client = client,
            success = { synchronized(successLambdaCalls) { successLambdaCalls++ } },
            failure = {
                synchronized(failLambdaCalls) { failLambdaCalls++ }

                // We should get a SaferMeApiError Class
                TestCase.assertEquals(it.message, errorMessage)
            })

        // Ensure callbacks called the correct number of times
        TestCase.assertEquals(successLambdaCalls, 0)
        TestCase.assertEquals(failLambdaCalls, 1)
    }
}
