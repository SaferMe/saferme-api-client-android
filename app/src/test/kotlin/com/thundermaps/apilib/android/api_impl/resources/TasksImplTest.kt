package com.thundermaps.apilib.android.api_impl.resources

import android.util.Log
import com.thundermaps.apilib.android.api.resources.Task
import com.thundermaps.apilib.android.api_impl.AndroidClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.util.KtorExperimentalAPI
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class TasksImplTest {

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
     * Test that calling this method will cause a valid call to StandardMethods.create
     * The tests in [StandardCreateTest] cover nearly all functionality - we only need to consider
     * that it build an object of the correct type when successful
     */
    @KtorExperimentalAPI
    @Test
    fun testCreateSuccess() {
        val uuid = "test-uuid"
        val returnObject = "{\"uuid\":\"$uuid\"}"
        val responseHeaders = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
        var count = 0
        var inspectCalled = false
        val expectedPath = "/api/v0/tasks"

        val client = TestHelpers.testClient(
            content = returnObject,
            status = HttpStatusCode.Created,
            headers = responseHeaders,
            // ensure the correct path is used
            requestInspector = {
                assertEquals(it.url.encodedPath, expectedPath)
                inspectCalled = true
            }
        )

        every {
            defaultAPI.client(any())
        } answers {
            Pair(client, HttpRequestBuilder())
        }

        runBlocking {
            TasksImpl(defaultAPI).create(TestHelpers.defaultParams, Task(),
                {
                    assertEquals(it.data.uuid, uuid)
                    synchronized(count) { count++ }
                }, {
                    fail("Failure block should not be called")
                })
        }

        assertEquals(1, count)
        assertTrue(inspectCalled)
    }

    /**
     * Test that calling this method will cause a valid call to StandardMethods.read
     * The tests in [StandardCreateTest] cover nearly all functionality - we only need to consider
     * that it build an object of the correct type when successful
     */
    @KtorExperimentalAPI
    @Test
    fun testReadSuccess() {
        val uuid = "test-uuid"
        val returnObject = "{\"uuid\":\"$uuid\"}"
        val responseHeaders = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
        var count = 0
        var inspectCalled = false
        val expectedPath = "/api/v0/tasks/$uuid"

        val client = TestHelpers.testClient(
            content = returnObject,
            status = HttpStatusCode.OK,
            headers = responseHeaders,
            requestInspector = {
                assertEquals(it.url.encodedPath, expectedPath)
                inspectCalled = true
            }
        )

        every {
            defaultAPI.client(any())
        } answers {
            Pair(client, HttpRequestBuilder())
        }

        runBlocking {
            TasksImpl(defaultAPI).read(TestHelpers.defaultParams, Task(uuid = uuid),
                {
                    assertEquals(it.data.uuid, uuid)
                    synchronized(count) { count++ }
                }, {
                    fail("Failure block should not be called")
                })
        }

        assertEquals(1, count)
        assertTrue(inspectCalled)
    }

    /**
     * Test that calling this method will cause a valid call to StandardMethods.update
     * The tests in [StandardCreateTest] cover nearly all functionality - we only need to consider
     * that it build an object of the correct type when successful
     */
    @KtorExperimentalAPI
    @Test
    fun testUpdateSuccess() {
        val requestItem = Task(uuid = "Random-Task")
        val returnContent = "{}" // V4 Task API returns an empty object on success
        val responseHeaders = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
        var count = 0
        var inspectCalled = false
        val expectedPath = "/api/v0/tasks/${requestItem.uuid}"

        val client = TestHelpers.testClient(
            content = returnContent,
            status = HttpStatusCode.Accepted,
            headers = responseHeaders,
            // ensure the correct path is used
            requestInspector = {
                assertEquals(it.url.encodedPath, expectedPath)
                inspectCalled = true
            }
        )

        every {
            defaultAPI.client(any())
        } answers {
            Pair(client, HttpRequestBuilder())
        }

        runBlocking {
            TasksImpl(defaultAPI).update(TestHelpers.defaultParams, requestItem,
                {
                    // return value should be the same object
                    assertTrue(it.data === requestItem)
                    synchronized(count) { count++ }
                }, {
                    fail("Failure block should not be called")
                })
        }

        assertEquals(1, count)
        assertTrue(inspectCalled)
    }

    /**
     * Test that calling this method will cause a valid call to StandardMethods.index
     * The tests in [StandardCreateTest] cover nearly all functionality - we only need to consider
     * that it build an list of objects of the correct type when successful
     */
    @KtorExperimentalAPI
    @Test
    fun testIndexSuccess() {
        val returnJson = "[{\"uuid\": \"test-one\"},{\"uuid\":\"test-two\"}]"
        val responseHeaders = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
        var count = 0
        var inspectCalled = false
        val expectedPath = "/api/v0/tasks"

        val client = TestHelpers.testClient(
            content = returnJson,
            status = HttpStatusCode.OK,
            headers = responseHeaders,
            requestInspector = {
                assertEquals(it.url.encodedPath, expectedPath)
                inspectCalled = true
            }

        )

        every {
            defaultAPI.client(any())
        } answers {
            Pair(client, HttpRequestBuilder())
        }

        runBlocking {
            TasksImpl(defaultAPI).index(TestHelpers.defaultParams,
                {
                    val actualList = it.data
                    assertEquals(2, actualList.size)
                    assertEquals("test-one", actualList[0].uuid)
                    assertEquals("test-two", actualList[1].uuid)
                    synchronized(count) { count++ }
                }, {
                    fail("Failure block should not be called")
                })
        }

        assertEquals(1, count)
        assertTrue(inspectCalled)
    }
}
