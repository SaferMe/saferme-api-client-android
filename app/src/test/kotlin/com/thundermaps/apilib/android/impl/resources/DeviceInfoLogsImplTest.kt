package com.thundermaps.apilib.android.impl.resources

import com.thundermaps.apilib.android.api.com.thundermaps.apilib.android.logging.ELog
import com.thundermaps.apilib.android.api.resources.DeviceInfoLog
import com.thundermaps.apilib.android.api.resources.DeviceInfoLogs
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
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class DeviceInfoLogsImplTest {

    @MockK
    lateinit var defaultAPI: AndroidClient

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockkObject(ELog)
        every { ELog.i(any(), any()) } just Runs
        every { ELog.w(any(), any()) } just Runs
        every { ELog.e(any()) } just Runs
    }

    /**
     * Test that calling this method will cause a valid call to StandardMethods.create
     * The tests in [StandardCreateTest] cover nearly all functionality - we only need to consider
     * that it build an object of the correct type when successful
     */
    @KtorExperimentalAPI
    @Test
    fun testCreateSuccess() {
        val scans = ArrayList<String>()
        scans.add("123")
        scans.add("1234")
        var deviceInfoLogs = DeviceInfoLogs("android 7", "samsung galaxy s3", scans)
        val deviceInfoLog = DeviceInfoLog(deviceInfoLogs)
        val returnObject = "{\"message\":\"accepted\"}"
        val responseHeaders =
            headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
        var count = 0
        var inspectCalled = false
        val expectedPath = "/api/v0/device_info_logs"

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
            DeviceInfoLogsImpl(defaultAPI).create(
                TestHelpers.defaultParams, deviceInfoLog,
                {
                    TestCase.assertNotNull(it.data)
                    TestCase.assertEquals(it.data, deviceInfoLog)
                    synchronized(count) { count++ }
                }, {
                TestCase.fail("Failure block should not be called")
            }
            )
        }

        TestCase.assertTrue(inspectCalled)
    }
}
