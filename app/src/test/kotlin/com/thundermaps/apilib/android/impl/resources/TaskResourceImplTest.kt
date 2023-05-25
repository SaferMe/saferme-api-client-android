package com.thundermaps.apilib.android.impl.resources

import android.util.Log
import com.google.gson.Gson
import com.thundermaps.apilib.android.api.com.thundermaps.apilib.android.logging.ELog
import com.thundermaps.apilib.android.api.com.thundermaps.env.Staging
import com.thundermaps.apilib.android.api.resources.Task
import com.thundermaps.apilib.android.api.responses.models.ResultHandler
import com.thundermaps.apilib.android.impl.AndroidClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.fullPath
import io.ktor.http.headersOf
import io.ktor.util.KtorExperimentalAPI
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkObject
import io.mockk.mockkStatic
import junit.framework.Assert.assertNotNull
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class TaskResourceImplTest {

    @MockK
    lateinit var defaultAPI: AndroidClient

    private val resultHandler: ResultHandler = ResultHandler()
    private val gson = Gson()

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
        val uuid = "test-uuid"
        val returnObject = "{\"uuid\":\"$uuid\"}"
        val responseHeaders =
            headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
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
            TaskResourceImpl(defaultAPI, resultHandler, gson).create(
                TestHelpers.defaultParams,
                Task(),
                {
                    assertEquals(it.data.uuid, uuid)
                    synchronized(count) { count++ }
                },
                {
                    fail("Failure block should not be called")
                }
            )
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
        val responseHeaders =
            headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
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
            TaskResourceImpl(defaultAPI, resultHandler, gson).read(
                TestHelpers.defaultParams,
                Task(uuid = uuid),
                {
                    assertEquals(it.data.uuid, uuid)
                    synchronized(count) { count++ }
                },
                {
                    fail("Failure block should not be called")
                }
            )
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
        val responseHeaders =
            headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
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
            TaskResourceImpl(defaultAPI, resultHandler, gson).update(
                TestHelpers.defaultParams,
                requestItem,
                {
                    // return value should be the same object
                    assertTrue(it.data === requestItem)
                    synchronized(count) { count++ }
                },
                {
                    fail("Failure block should not be called")
                }
            )
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
        val responseHeaders =
            headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
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
            TaskResourceImpl(defaultAPI, resultHandler, gson).index(
                TestHelpers.defaultParams,
                {
                    val actualList = it.data
                    assertEquals(2, actualList.size)
                    assertEquals("test-one", actualList[0].uuid)
                    assertEquals("test-two", actualList[1].uuid)
                    synchronized(count) { count++ }
                }, {
                fail("Failure block should not be called")
            }
            )
        }

        assertEquals(1, count)
        assertTrue(inspectCalled)
    }

    @KtorExperimentalAPI
    @Test
    fun testDeleteSuccess() {
        val requestItem = Task(uuid = "123")
        val returnContent = "{}" // V4 Task API returns an empty object on success
        val responseHeaders =
            headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
        var count = 0
        var inspectCalled = false
        val expectedPath = "/api/v0/tasks/${requestItem.uuid}"

        val client = TestHelpers.testClient(
            content = returnContent,
            status = HttpStatusCode.Accepted,
            headers = responseHeaders,
            // ensure the correct path is used
            requestInspector = {
                Assert.assertEquals(it.url.encodedPath, expectedPath)
                inspectCalled = true
            }
        )

        every {
            defaultAPI.client(any())
        } answers {
            Pair(client, HttpRequestBuilder())
        }

        runTest {
            TaskResourceImpl(defaultAPI, resultHandler, gson).delete(
                TestHelpers.defaultParams,
                requestItem,
                {
                    // return value should be the same object
                    Assert.assertTrue(it.data === requestItem)
                    synchronized(count) { count++ }
                },
                {
                    Assert.fail("Failure block should not be called")
                }
            )
        }

        Assert.assertEquals(1, count)
        Assert.assertTrue(inspectCalled)
    }

    @KtorExperimentalAPI
    @Test
    fun testMarkAsIncomplete() {
        val requestItem = Task(uuid = "Random-Task")
        val returnContent = "{}" // V4 Task API returns an empty object on success
        val responseHeaders =
            headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
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
            TaskResourceImpl(
                defaultAPI,
                resultHandler,
                gson
            ).markAsInComplete(
                TestHelpers.defaultParams,
                "Random-Task",
                {
                    // return value should be the same object
                    synchronized(count) { count++ }
                },
                {
                    fail("Failure block should not be called")
                }
            )
        }

        assertEquals(1, count)
        assertTrue(inspectCalled)
    }

    @KtorExperimentalAPI
    @Test
    fun testReadDeletedResourceSuccess() {
        val uuid = "test-uuid"
        val type = "type"
        val typeTask = "task"
        val deletedAfter = "deleted_after"
        val deletedAfterDate = "2022-08-25T10:45:35.081+13:00"
        val returnObject = "{\"deleted_resources\":[{\"uuid\":\"test-uuid\"}]}"
        val responseHeaders =
            headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
        var inspectCalled = false
        val expectedPath = "/api/v0/deleted_resources?$type=$typeTask"

        val client = TestHelpers.testClient(
            content = returnObject,
            status = HttpStatusCode.OK,
            headers = responseHeaders,
            requestInspector = {
                assertEquals(it.url.fullPath, expectedPath)
                inspectCalled = true
            }
        )

        every {
            defaultAPI.build(any(), any(), any())
        } answers {
            Pair(
                client,
                AndroidClient.getRequestBuilder(TestHelpers.defaultParams.copy(parameters = mapOf(type to typeTask)), TaskResourceImpl.DELETED_TASK_PATH, HttpMethod.Get)
            )
        }

        runBlocking {
            val clientsResult = TaskResourceImpl(defaultAPI, resultHandler, gson).getTasksDeletedAfter(
                TestHelpers.defaultParams.copy(
                    host = Staging.servers.first(),
                    customRequestHeaders = mapOf(type to typeTask, deletedAfter to deletedAfterDate)
                )
            )
            Assert.assertTrue(clientsResult.isSuccess)
            val clients = clientsResult.getNullableData()
            assertNotNull(clients)
            assertEquals(clients!!.deletedResource[0].uuid, uuid)
        }
        assertTrue(inspectCalled)
    }
}
