package com.thundermaps.apilib.android.impl.resources

import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.thundermaps.apilib.android.api.SaferMeCredentials
import com.thundermaps.apilib.android.api.com.thundermaps.env.Staging
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.resources.NotificationResource
import com.thundermaps.apilib.android.api.responses.models.Notification
import com.thundermaps.apilib.android.api.responses.models.ResponseException
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.ResultHandler
import com.thundermaps.apilib.android.impl.AndroidClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
@KtorExperimentalAPI
class NotificationResourceImplTest {
    private val androidClient: AndroidClient = mock()
    private val resultHandler: ResultHandler = ResultHandler()
    private val gson = Gson()
    private lateinit var notificationResource: NotificationResource

    @Before
    fun setUp() {
        notificationResource = NotificationResourceImpl(androidClient, resultHandler, gson)
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(androidClient)
    }

    @Test
    fun getNotifications() = runBlockingTest {
        var inspectCalled = false
        val client = TestHelpers.testClient(
            content = NOTIFICATION_SUCCESS_RESPONSE,
            status = HttpStatusCode.OK,
            headers = responseHeaders,
            requestInspector = {
                assertEquals(NOTIFICATIONS_PATH_WITH_PARAMETERS, it.url.encodedPath)
                assertEquals(HttpMethod.Get, it.method)
                inspectCalled = true
            }
        )

        whenever(androidClient.client(any())) doReturn Pair(client, HttpRequestBuilder())

        val result = notificationResource.getNotifications(defaultParameters)

        verifyAndroidClient(4)
        assertTrue(result.isSuccess)
        assertTrue(inspectCalled)

        val notifications = result.getNullableData()

        assertNotNull(notifications)
        assertEquals(10, notifications?.size)

        val firstExpectedNotification = Notification(
            id = 465119304,
            archivedAt = null,
            archived = false,
            createdAt = "2022-01-18T15:35:59.888+13:00",
            read = false,
            reportId = 373401781,
            reportUuid = "9ea93984-759a-4fb9-8419-a7a6379c8f8e",
            userId = 92536
        )

        val firstNotification = notifications?.firstOrNull()

        assertEquals(firstExpectedNotification, firstNotification)
        assertEquals(firstExpectedNotification.id, firstNotification?.id)
        assertEquals(firstExpectedNotification.archived, firstNotification?.archived)
        assertEquals(firstExpectedNotification.archivedAt, firstNotification?.archivedAt)
        assertEquals(firstExpectedNotification.createdAt, firstNotification?.createdAt)
        assertEquals(firstExpectedNotification.read, firstNotification?.read)
        assertEquals(firstExpectedNotification.reportId, firstNotification?.reportId)
        assertEquals(firstExpectedNotification.reportUuid, firstNotification?.reportUuid)
        assertEquals(firstExpectedNotification.userId, firstNotification?.userId)
    }

    @Test
    fun getNotificationsError() = runBlockingTest {
        var inspectCalled = false
        val client = TestHelpers.testClient(
            content = BAD_CREDENTIAL_ERROR_RESPONSE,
            status = HttpStatusCode.Unauthorized,
            headers = responseHeaders,
            requestInspector = {
                assertEquals(NOTIFICATIONS_PATH, it.url.encodedPath)
                assertEquals(HttpMethod.Get, it.method)
                inspectCalled = true
            }
        )

        whenever(androidClient.client(any())) doReturn Pair(client, HttpRequestBuilder())

        val result =
            notificationResource.getNotifications(defaultParameters.copy(parameters = null))

        verifyAndroidClient(4)
        assertTrue(result.isError)
        val error = ((result as Result.Error).exception as ResponseException).responseError
        assertEquals("bad_credentials", error.errorCodes?.base?.firstOrNull()?.error)
        assertTrue(inspectCalled)
    }

    private fun verifyAndroidClient(expectedVersion: Int) {
        val parameterCaptor = argumentCaptor<RequestParameters>()
        verify(androidClient).client(parameterCaptor.capture())
        val requestParameters = parameterCaptor.firstValue
        assertEquals(expectedVersion, requestParameters.api_version)
        assertEquals(saferMeCredentials, requestParameters.credentials)
    }

    companion object {
        private val responseHeaders =
            headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
        private const val APPLICATION_ID = "com.thundermaps.saferme"
        private const val NOTIFICATIONS_PATH = "/api/v4/notifications"
        private const val NOTIFICATIONS_PATH_WITH_PARAMETERS =
            "/api/v4/notifications?archived=false&orderby=report.created_at%20desc&fields=report_state,assignee,viewer_count,comment_count,categories_title,hidden_fields,is_hazard,risk_level"
        private const val TEST_KEY = "Test Key"
        private const val TEST_INSTALL = "Install App"
        private const val TEST_APP = APPLICATION_ID
        private const val TEST_TEAM = "Test Team"
        private const val TEST_CLIENT_UUID = "client uuid"
        private val saferMeCredentials =
            SaferMeCredentials(TEST_KEY, TEST_INSTALL, TEST_APP, TEST_TEAM, TEST_CLIENT_UUID)
        private val defaultParameters = RequestParameters(
            customRequestHeaders = HashMap(),
            credentials = saferMeCredentials,
            host = Staging.servers.first(),
            port = null,
            api_version = 4,
            parameters = hashMapOf(
                "archived" to "false",
                "fields" to "report_state,assignee,viewer_count,comment_count,categories_title,hidden_fields,is_hazard,risk_level",
                "orderby" to "report.created_at%20desc"
            )
        )
        private val NOTIFICATION_SUCCESS_RESPONSE = """
            [
              {
                "id": 465119304,
                "archived_at": null,
                "archived": false,
                "created_at": "2022-01-18T15:35:59.888+13:00",
                "read": false,
                "report_id": 373401781,
                "report_uuid": "9ea93984-759a-4fb9-8419-a7a6379c8f8e",
                "user_id": 92536
              },
              {
                "id": 457203252,
                "archived_at": null,
                "archived": false,
                "created_at": "2021-10-12T10:54:38.766+13:00",
                "read": true,
                "report_id": 373338902,
                "report_uuid": "b6f2f429-06cf-4e7e-9290-1c36a6534ce7",
                "user_id": 92536
              },
              {
                "id": 457201104,
                "archived_at": null,
                "archived": false,
                "created_at": "2021-10-12T10:47:56.303+13:00",
                "read": true,
                "report_id": 373338895,
                "report_uuid": "3869f401-e29f-46b0-bdc8-aecec8962b93",
                "user_id": 92536
              },
              {
                "id": 457196080,
                "archived_at": null,
                "archived": false,
                "created_at": "2021-10-12T10:26:21.909+13:00",
                "read": true,
                "report_id": 373338851,
                "report_uuid": "c644ba66-dd7f-4821-93c4-c8fdaf6f9278",
                "user_id": 92536
              },
              {
                "id": 457136756,
                "archived_at": null,
                "archived": false,
                "created_at": "2021-10-11T16:57:53.492+13:00",
                "read": true,
                "report_id": 373337893,
                "report_uuid": "ef66084e-0d73-4fa9-9a73-863f628ece4b",
                "user_id": 92536
              },
              {
                "id": 457125173,
                "archived_at": null,
                "archived": false,
                "created_at": "2021-10-11T12:57:42.762+13:00",
                "read": true,
                "report_id": 373337837,
                "report_uuid": "1bfa2c35-fbcd-4227-85f6-894e601bfaff",
                "user_id": 92536
              },
              {
                "id": 457123881,
                "archived_at": null,
                "archived": false,
                "created_at": "2021-10-11T12:33:03.811+13:00",
                "read": true,
                "report_id": 373337818,
                "report_uuid": "ef938efb-0255-4164-ba3b-33d515dc9357",
                "user_id": 92536
              },
              {
                "id": 457123001,
                "archived_at": null,
                "archived": false,
                "created_at": "2021-10-11T12:17:12.135+13:00",
                "read": true,
                "report_id": 373337791,
                "report_uuid": "22026f71-cf15-4ee8-82e7-1d8798b83a23",
                "user_id": 92536
              },
              {
                "id": 457117946,
                "archived_at": null,
                "archived": false,
                "created_at": "2021-10-11T11:01:33.072+13:00",
                "read": true,
                "report_id": 373337754,
                "report_uuid": "0d643a89-41f6-4526-bfc7-8d5114a1880e",
                "user_id": 92536
              },
              {
                "id": 457117853,
                "archived_at": null,
                "archived": false,
                "created_at": "2021-10-11T10:45:14.239+13:00",
                "read": true,
                "report_id": 373337748,
                "report_uuid": "5391c2f3-816e-4b7e-aae9-a8b8f194f36f",
                "user_id": 92536
              }
            ]
        """.trimIndent()

        private val BAD_CREDENTIAL_ERROR_RESPONSE =
            """
                {
                   "errors":[
                      "Please enter your email and password"
                   ],
                   "failures":{
                      "base":[
                         "Please enter your email and password"
                      ]
                   },
                   "error_codes":{
                      "base":[
                         {
                            "error":"bad_credentials"
                         }
                      ]
                   }
                }
            """.trimIndent()
    }
}
