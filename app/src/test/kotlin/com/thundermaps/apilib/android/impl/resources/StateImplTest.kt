package com.thundermaps.apilib.android.impl.resources

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
import com.thundermaps.apilib.android.api.requests.SaferMeApiError
import com.thundermaps.apilib.android.api.resources.StateResource
import com.thundermaps.apilib.android.api.responses.models.ReportState
import com.thundermaps.apilib.android.impl.AndroidClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StateImplTest {
    private val androidClient: AndroidClient = mock()
    private lateinit var stateResource: StateResource

    @Before
    fun setup() {
        stateResource = StateImpl(androidClient)
    }

    @Before
    fun tearDown() {
        verifyNoMoreInteractions(androidClient)
    }

    @Test
    fun getReportStateSuccess() = runBlockingTest {
        var inspectCalled = false
        val client = TestHelpers.testClient(
            content = ONE_STATE_RESPONSE_SUCCESS,
            status = HttpStatusCode.OK,
            headers = responseHeaders,
            requestInspector = {
                assertEquals("/api/v4/${StateImpl.STATE_PATH}/$stateId&fields=updated_at", it.url.encodedPath)
                assertEquals(HttpMethod.Get, it.method)
                inspectCalled = true
            }
        )

        whenever(androidClient.client(any())) doReturn Pair(client, HttpRequestBuilder())
        val state = ReportState(id = stateId)
        stateResource.read(defaultParameters, state, {
            val stateResult = it.data
            assertEquals(30123, stateResult.channelId)
            assertEquals("097da7e6-b93a-5390-a44f-e251d3fb0c6f", stateResult.uuid)
        }, {
            fail("Failure block should not be called")
        })

        verifyAndroidClient(4)
        assertTrue(inspectCalled)
    }

    @Test
    fun getReportStateError() = runBlockingTest {
        var inspectCalled = false
        val client = TestHelpers.testClient(
            content = BAD_CREDENTIAL_ERROR_RESPONSE,
            status = HttpStatusCode.Unauthorized,
            headers = responseHeaders,
            requestInspector = {
                assertEquals("/api/v4/${StateImpl.STATE_PATH}/$stateId&fields=updated_at", it.url.encodedPath)
                assertEquals(HttpMethod.Get, it.method)
                inspectCalled = true
            }
        )

        whenever(androidClient.client(any())) doReturn Pair(client, HttpRequestBuilder())
        val state = ReportState(id = stateId)
        stateResource.read(defaultParameters, state, {
        }, {
            val isSaferMeApiError = it is SaferMeApiError
            assertTrue(isSaferMeApiError)
        })

        verifyAndroidClient(4)
        assertTrue(inspectCalled)
    }

    private fun verifyAndroidClient(expectedVersion: Int) {
        val parameterCaptor = argumentCaptor<RequestParameters>()
        verify(androidClient).client(parameterCaptor.capture())
        val requestParameters = parameterCaptor.firstValue
        assertEquals(
            expectedVersion,
            requestParameters.api_version
        )
        assertEquals(
            saferMeCredentials,
            requestParameters.credentials
        )
    }

    companion object {
        private const val TEAM_ID = 6688L
        private const val STATE_PATH = ""
        private val responseHeaders =
            headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
        private const val APPLICATION_ID = "com.thundermaps.saferme"
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
            api_version = 4
        )

        private const val stateId = 122706

        private val ONE_STATE_RESPONSE_SUCCESS = """
           {
              "id": $stateId,
              "uuid": "097da7e6-b93a-5390-a44f-e251d3fb0c6f",
              "account_id": 30123,
              "name": "In Review",
              "slug": null,
              "position": 2,
              "loudness": "invisible",
              "notify": "admins_only",
              "timeout": 0,
              "visibility": "admins_only",
              "editability": "admins_only",
              "assignable": true,
              "default_assignee_id": null,
              "assignment_due_timeout": 0,
              "assign_to_supervisor": false,
              "auto_archive": false
            }
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
