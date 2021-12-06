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
import com.thundermaps.apilib.android.api.resources.TeamResource
import com.thundermaps.apilib.android.api.responses.models.ResponseException
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.ResultHandler
import com.thundermaps.apilib.android.impl.AndroidClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TeamResourceImplTest {
    private val androidClient: AndroidClient = mock()
    private val resultHandler: ResultHandler = ResultHandler()
    private val gson = Gson()
    private lateinit var teamResource: TeamResource

    @Before
    fun setUp() {
        teamResource = TeamResourceImpl(androidClient, resultHandler, gson)
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(androidClient)
    }

    @Test
    fun verifyGetTeamsSuccess() = runBlockingTest {
        var inspectCalled = false
        val client = TestHelpers.testClient(
            content = TEAMS_SUCCESS_RESPONSE,
            status = HttpStatusCode.OK,
            headers = responseHeaders,
            requestInspector = {
                assertEquals(TEAMS_PATH, it.url.encodedPath)
                assertEquals(HttpMethod.Get, it.method)
                inspectCalled = true
            }
        )

        whenever(androidClient.client(any())) doReturn Pair(client, HttpRequestBuilder())

        val result = teamResource.getTeams(defaultParameters)

        verifyAndroidClient(4)
        assertTrue(result.isSuccess)
        assertTrue(inspectCalled)

        val teams = result.getNullableData()

        assertNotNull(teams)
        assertEquals(2, teams?.size)
        assertEquals(6141L, teams?.first()?.id)
    }

    @Test
    fun verifyGetTeamsError() = runBlockingTest {
        var inspectCalled = false
        val client = TestHelpers.testClient(
            content = BAD_CREDENTIAL_ERROR_RESPONSE,
            status = HttpStatusCode.Unauthorized,
            headers = responseHeaders,
            requestInspector = {
                assertEquals(TEAMS_PATH, it.url.encodedPath)
                assertEquals(HttpMethod.Get, it.method)
                inspectCalled = true
            }
        )

        whenever(androidClient.client(any())) doReturn Pair(client, HttpRequestBuilder())

        val result = teamResource.getTeams(defaultParameters)

        verifyAndroidClient(4)
        assertTrue(result.isError)
        val error = ((result as Result.Error).exception as ResponseException).responseError
        assertEquals("bad_credentials", error.errorCodes?.base?.firstOrNull()?.error)
        assertTrue(inspectCalled)
    }

    private fun verifyAndroidClient(expectedVersion: Int = 3) {
        val parameterCaptor = argumentCaptor<RequestParameters>()
        verify(androidClient).client(parameterCaptor.capture())
        val requestParameters = parameterCaptor.firstValue
        assertEquals(expectedVersion, requestParameters.api_version)
        assertEquals(saferMeCredentials, requestParameters.credentials)
    }

    companion object {
        private val responseHeaders = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
        private const val APPLICATION_ID = "com.thundermaps.saferme"
        private const val TEAMS_PATH = "/api/v4/teams"
        private const val TEST_KEY = "Test Key"
        private const val TEST_INSTALL = "Install App"
        private const val TEST_APP = APPLICATION_ID
        private const val TEST_TEAM = "Test Team"
        private const val TEST_CLIENT_UUID = "client uuid"
        private val saferMeCredentials = SaferMeCredentials(TEST_KEY, TEST_INSTALL, TEST_APP, TEST_TEAM, TEST_CLIENT_UUID)
        private val defaultParameters = RequestParameters(
            customRequestHeaders = HashMap(),
            credentials = saferMeCredentials,
            host = Staging.servers.first(),
            port = null,
            api_version = 4
        )

        private val TEAMS_SUCCESS_RESPONSE = """
            [
              {
                "id": 6141,
                "contact_tracing_enabled": false,
                "form_contact_tracing_enabled": false,
                "feature_tasks_enabled": true,
                "guests_enabled": false,
                "industry": "Test second organisation",
                "location": null,
                "name": "MT T32",
                "risk_register_enabled": true,
                "sso_required": false,
                "sso_team_id": "nb6g6xtcuia",
                "user_timeout": null,
                "wearables_enabled": false
              },
              {
                "id": 6129,
                "contact_tracing_enabled": false,
                "form_contact_tracing_enabled": false,
                "feature_tasks_enabled": true,
                "guests_enabled": false,
                "industry": null,
                "location": null,
                "name": "MT Test2",
                "risk_register_enabled": true,
                "sso_required": false,
                "sso_team_id": "en1owl6sq3c",
                "user_timeout": null,
                "wearables_enabled": false
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
