package com.thundermaps.apilib.android.api_impl.resources

import com.thundermaps.apilib.android.api.SaferMeCredentials
import com.thundermaps.apilib.android.api.requests.SaferMeApiStatus
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
        val returnObject = "{\n" +
            "    \"uuid\": \"$uuid\",\n" +
            "    \"assignee_id\": 12345,\n" +
            "    \"client_created_at\": \"2019-08-15T15:37:38.250+12:00\",\n" +
            "    \"completed_at\": \"2019-08-15T15:37:43.812+12:00\",\n" +
            "    \"completed_by_id\": 12345,\n" +
            "    \"creator_id\": 12345,\n" +
            "    \"description\": \"Hsdfdf ashad ashasd shd jsh\",\n" +
            "    \"report_id\": 187714,\n" +
            "    \"title\": \"Jjsas a ashhshs\"\n" +
            "}"
        val responseHeaders = headersOf( "Content-Type" to listOf(ContentType.Application.Json.toString()))
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

    /**
     * Test read against an actual server
     */
    @KtorExperimentalAPI
    @Test
    fun integrationReadTest() {
        val client = AndroidClient()
        runBlocking {
            client.Tasks.read(
                client.defaultParams().copy(
                    credentials =SaferMeCredentials(
                    //TODO read in from env?
                )),
                Task(uuid = ""), //TODO read in from env?
                {result ->
                    assertEquals(result.data.uuid, "1b15a902-9f8b-40d7-a51c-2bfff682b114")
                    assertEquals(result.serverStatus, SaferMeApiStatus.OK)
                },
                {exception -> assertTrue(false)}
            )
         }
    }

}
