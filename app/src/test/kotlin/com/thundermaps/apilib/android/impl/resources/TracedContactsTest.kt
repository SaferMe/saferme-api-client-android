package com.thundermaps.apilib.android.impl.resources

import android.util.Log
import com.thundermaps.apilib.android.api.com.thundermaps.apilib.android.logging.ELog
import com.thundermaps.apilib.android.api.resources.TracedContact
import com.thundermaps.apilib.android.api.resources.TracedContacts
import com.thundermaps.apilib.android.impl.AndroidClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.util.KtorExperimentalAPI
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkObject
import io.mockk.mockkStatic
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class TracedContactsTest {

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
     * Test that calling this method will cause a valid call to StandardMethods.create
     * The tests in [StandardCreateTest] cover nearly all functionality - we only need to consider
     * that it build an object of the correct type when successful
     */
    @KtorExperimentalAPI
    @Test
    fun testCreateSuccess() {
        var tracedContacts = ArrayList<TracedContact>()
        tracedContacts.add(TracedContact("1", "bluetooth", "123", "", 3))
        val returnObject = "{\"message\":\"accepted\"}"
        val responseHeaders =
            headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
        var count = 0
        var inspectCalled = false
        val expectedPath = "/api/v0/traced_contacts"

        val client = TestHelpers.testClient(
            content = returnObject,
            status = HttpStatusCode.Accepted,
            headers = responseHeaders,
            // ensure the correct path is used
            requestInspector = {
                TestCase.assertEquals(it.url.encodedPath, expectedPath)
                inspectCalled = true
            }
        )
        every {
            defaultAPI.client(any())
        } answers {
            Pair(client, HttpRequestBuilder())
        }

        runBlocking {
            TracedContactsImpl(defaultAPI).create(
                TestHelpers.defaultParams, TracedContacts(tracedContacts),
                {
                    TestCase.assertNotNull(it.data)
                    synchronized(count) { count++ }
                }, {
                TestCase.fail("Failure block should not be called")
            }
            )
        }

        TestCase.assertTrue(inspectCalled)
    }
}
