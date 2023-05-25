package com.thundermaps.apilib.android.impl.resources

import android.util.Log
import com.google.gson.Gson
import com.thundermaps.apilib.android.api.com.thundermaps.apilib.android.logging.ELog
import com.thundermaps.apilib.android.api.com.thundermaps.env.Staging
import com.thundermaps.apilib.android.api.responses.models.Report
import com.thundermaps.apilib.android.api.responses.models.ResultHandler
import com.thundermaps.apilib.android.impl.AndroidClient
import com.thundermaps.apilib.android.impl.resources.ReportImpl.Companion.FIELDS_PARAM
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
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ReportImplTest {
    private val reportTestPayload =
        "{\"id\":204202330, \"uuid\":\"204202330\",\"account_id\":16801,\"account_name\":\"TESTS FROM BOLIVIA\",\"address\":\"26 Willeston Street, Wellington Central, Wellington 6011, New Zealand\",\"assignee_id\":44920,\"assignment_due_at\":null,\"categories_title\":\"TESTS FROM BOLIVIA\",\"category_id\":162032,\"description\":null,\"hidden_fields\":[],\"is_anonymous\":false,\"is_deletable_by\":true,\"is_hazard\":true,\"is_manageable_by\":true,\"iso_created_at\":\"2019-09-09T09:54:33+12:00\",\"location\":{\"latitude\":-41.286484022453,\"longitude\":174.776353997708},\"report_state_id\":55347,\"risk_assessable_by\":true,\"risk_assessment\":{\"id\":7165,\"user_id\":45095,\"user_image\":\"https://userfiles.thundermaps.com/paperclipped/users-avatars/45095/small-e045666c4427eef37bb9e4271e5dc26a179417c7.jpg\",\"user_short_name\":\"pedro t.\",\"eliminated\":false,\"likelihood\":{\"key\":\"l5\",\"label\":\"Almost Certain\",\"value\":5},\"severity\":{\"key\":\"s5\",\"label\":\"Catastrophic\",\"value\":5},\"risk_level\":{\"score\":25,\"label\":\"Catastrophic\",\"color\":\"#ff001c\"},\"comment\":\"\",\"created_at\":\"2019-09-09T09:54:33+12:00\"},\"risk_control_editable_by\":true,\"risk_control_id\":2474,\"risk_level\":{\"score\":25,\"label\":\"Catastrophic\",\"color\":\"#ff001c\"},\"shape_id\":null,\"state_is_editable_by\":true,\"title\":\"dan\",\"updated_at\":\"2021-01-19T14:51:37.943+13:00\",\"user_id\":45095,\"form_fields\":[{\"id\":96720,\"label\":\"Signature\",\"key\":\"f_16801_36_10\",\"field_type\":\"Signature\",\"form_order\":9,\"data\":{},\"mandatory\":false,\"visibility\":\"public\",\"field_visibility\":\"public\",\"value\":[],\"editable\":true},{\"id\":92925,\"label\":\"Category\",\"key\":\"f_16801_16_5\",\"field_type\":\"Category\",\"form_order\":7,\"data\":{},\"mandatory\":false,\"visibility\":\"public\",\"field_visibility\":\"public\",\"value\":162032,\"editable\":true},{\"id\":85729,\"label\":\"Summary\",\"key\":\"f_16801_1_1\",\"field_type\":\"ShortTextBox\",\"form_order\":0,\"data\":{},\"mandatory\":true,\"visibility\":\"public\",\"field_visibility\":\"public\",\"value\":\"dan\",\"editable\":true},{\"id\":92924,\"label\":\"test dropdown\",\"key\":\"f_16801_15_4\",\"field_type\":\"DropDown\",\"form_order\":6,\"data\":{\"multi_select\":false,\"options\":[{\"label\":\"test1\",\"value\":\"f_16801_15_4_15_1\",\"enabled\":true,\"multi_option_id\":92924,\"display_order\":0,\"is_default\":true},{\"label\":\"test2\",\"value\":\"f_16801_15_4_22_2\",\"enabled\":true,\"multi_option_id\":92924,\"display_order\":1,\"is_default\":false},{\"label\":\"the big test3\",\"value\":\"f_16801_15_4_23_3\",\"enabled\":true,\"multi_option_id\":92924,\"display_order\":2,\"is_default\":false}]},\"mandatory\":false,\"visibility\":\"public\",\"field_visibility\":\"public\",\"value\":\"f_16801_15_4_15_1\",\"editable\":true},{\"id\":85732,\"label\":\"Risk Matrix\",\"key\":\"f_risk_matrix_5e5f211909e1fa0f2e9d\",\"field_type\":\"RiskMatrix\",\"form_order\":1,\"data\":{\"likelihoods\":[{\"key\":\"l1\",\"label\":\"Rare\",\"value\":1},{\"key\":\"l2\",\"label\":\"Unlikely\",\"value\":2},{\"key\":\"l3\",\"label\":\"Possible\",\"value\":3},{\"key\":\"l4\",\"label\":\"Likely\",\"value\":4},{\"key\":\"l5\",\"label\":\"Almost Certain\",\"value\":5}],\"severities\":[{\"key\":\"s1\",\"label\":\"Superficial\",\"value\":1},{\"key\":\"s2\",\"label\":\"Minor\",\"value\":2},{\"key\":\"s3\",\"label\":\"Moderate\",\"value\":3},{\"key\":\"s4\",\"label\":\"Major\",\"value\":4},{\"key\":\"s5\",\"label\":\"Catastrophic\",\"value\":5}],\"risk_levels\":[{\"limit\":3,\"label\":\"Low\",\"color\":\"#18a45a\"},{\"limit\":9,\"label\":\"Moderate\",\"color\":\"#f49b20\"},{\"limit\":14,\"label\":\"High\",\"color\":\"#f4661f\"},{\"limit\":25,\"label\":\"Catastrophic\",\"color\":\"#ff001c\"}]},\"mandatory\":false,\"visibility\":\"public\",\"field_visibility\":\"public\",\"value\":null,\"editable\":true},{\"id\":93616,\"label\":\"Drop Down\",\"key\":\"f_16801_32_8\",\"field_type\":\"DropDown\",\"form_order\":8,\"data\":{\"multi_select\":false,\"options\":[{\"label\":\"label1\",\"value\":\"f_16801_32_8_32_1\",\"enabled\":true,\"multi_option_id\":93616,\"display_order\":0,\"is_default\":false},{\"label\":\"label2\",\"value\":\"f_16801_32_8_32_2\",\"enabled\":true,\"multi_option_id\":93616,\"display_order\":1,\"is_default\":true},{\"label\":\"label 3\",\"value\":\"f_16801_32_8_32_3\",\"enabled\":true,\"multi_option_id\":93616,\"display_order\":2,\"is_default\":false}]},\"mandatory\":false,\"visibility\":\"public\",\"field_visibility\":\"private\",\"value\":\"f_16801_32_8_32_2\",\"editable\":true},{\"id\":85731,\"label\":\"Description\",\"key\":\"f_16801_1_3\",\"field_type\":\"LongTextBox\",\"form_order\":3,\"data\":{},\"mandatory\":false,\"visibility\":\"public\",\"field_visibility\":\"public\",\"value\":null,\"editable\":true},{\"id\":92938,\"label\":\"Description\",\"key\":\"f_16801_17_6\",\"field_type\":\"LongTextBox\",\"form_order\":5,\"data\":{},\"mandatory\":false,\"visibility\":\"public\",\"field_visibility\":\"public\",\"value\":null,\"editable\":true},{\"id\":85730,\"label\":\"Photo\",\"key\":\"f_16801_1_2\",\"field_type\":\"Image\",\"form_order\":2,\"data\":{},\"mandatory\":false,\"visibility\":\"public\",\"field_visibility\":\"public\",\"value\":[],\"editable\":true}]}"

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
        val id = 204202330
        val responseHeaders =
            headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
        var count = 0
        var inspectCalled = false
        val expectedPath = "/api/v0/reports"

        val client = TestHelpers.testClient(
            content = reportTestPayload,
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

        runTest {
            ReportImpl(defaultAPI, resultHandler, gson).create(
                TestHelpers.defaultParams, Report(),
                {
                    assertEquals(it.data.id, id)
                    synchronized(count) { count++ }
                }, {
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
        val id = 204202330
        val returnObject = "{\"uuid\":\"$id\", \"id\": 2}"
        val responseHeaders =
            headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
        var count = 0
        var inspectCalled = false
        val expectedPath = "/api/v0/reports/$id?$FIELDS_PARAM"

        val client = TestHelpers.testClient(
            content = reportTestPayload,
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

        runTest {
            ReportImpl(defaultAPI, resultHandler, gson).read(
                TestHelpers.defaultParams,
                Report(id = id, uuid = id.toString()),
                {
                    assertEquals(it.data.uuid, id.toString())

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
        val requestItem = Report(id = 1, uuid = "1")
        val returnContent = "{}" // V4 Task API returns an empty object on success
        val responseHeaders =
            headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
        var count = 0
        var inspectCalled = false
        val expectedPath = "/api/v0/reports/${requestItem.uuid}?$FIELDS_PARAM"

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

        runTest {
            ReportImpl(defaultAPI, resultHandler, gson).update(
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
        val returnJson = "[{\"id\": 1, \"uuid\":123\"},{\"id\": 2, \"uuid\":\"123\"}]"
        val responseHeaders =
            headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
        var count = 0
        var inspectCalled = false
        val expectedPath = "/api/v0/reports?$FIELDS_PARAM"

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

        runTest {
            ReportImpl(defaultAPI, resultHandler, gson).index(
                TestHelpers.defaultParams,
                {
                    val actualList = it.data
                    assertEquals(2, actualList.size)
                    assertEquals(1, actualList[0].id)
                    assertEquals(2, actualList[1].id)
                    synchronized(count) { count++ }
                }, {
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
    fun testDeleteSuccess() {
        val requestItem = Report(id = 1, uuid = "123")
        val returnContent = "{}" // V4 Task API returns an empty object on success
        val responseHeaders =
            headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
        var count = 0
        var inspectCalled = false
        val expectedPath = "/api/v0/reports/${requestItem.uuid}"

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

        runTest {
            ReportImpl(defaultAPI, resultHandler, gson).delete(
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

    @KtorExperimentalAPI
    @Test
    fun testReadDeletedResourceSuccess() {
        val uuid = "test-uuid"
        val type = "type"
        val typeReport = "report"
        val deletedAfter = "deleted_after"
        val deletedAfterDate = "2022-08-25T10:45:35.081+13:00"
        val returnObject = "{\"deleted_resources\":[{\"uuid\":\"test-uuid\"}]}"
        val responseHeaders =
            headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
        var inspectCalled = false
        val expectedPath = "/api/v0/deleted_resources?$type=$typeReport"

        val client = TestHelpers.testClient(
            content = returnObject,
            status = HttpStatusCode.OK,
            headers = responseHeaders,
            requestInspector = {
                TestCase.assertEquals(it.url.fullPath, expectedPath)
                inspectCalled = true
            }
        )

        every {
            defaultAPI.build(any(), any(), any())
        } answers {
            Pair(
                client,
                AndroidClient.getRequestBuilder(
                    TestHelpers.defaultParams.copy(
                        host = Staging.servers.first(),
                        parameters = mapOf(type to typeReport)
                    ),
                    ReportImpl.DELETED_REPORT_PATH,
                    HttpMethod.Get
                )
            )
        }

        runBlocking {
            val clientsResult = ReportImpl(defaultAPI, resultHandler, gson).getReportsDeletedAfter(
                TestHelpers.defaultParams.copy(
                    host = Staging.servers.first(),
                    customRequestHeaders = mapOf(type to typeReport, deletedAfter to deletedAfterDate)
                )
            )
            assertTrue(clientsResult.isSuccess)
            val clients = clientsResult.getNullableData()
            assertNotNull(clients)
            TestCase.assertEquals(clients!!.deletedResource[0].uuid, uuid)
        }
        TestCase.assertTrue(inspectCalled)
    }
}
