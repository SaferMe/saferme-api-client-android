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
import com.thundermaps.apilib.android.api.resources.ChannelResource
import com.thundermaps.apilib.android.api.responses.models.Avatar
import com.thundermaps.apilib.android.api.responses.models.Channel
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

@KtorExperimentalAPI
@ExperimentalCoroutinesApi
class ChannelResourceImplTest {
    private val androidClient: AndroidClient = mock()
    private val resultHandler: ResultHandler = ResultHandler()
    private val gson = Gson()

    private lateinit var channelResource: ChannelResource

    @Before
    fun setUp() {
        channelResource = ChannelResourceImpl(androidClient, resultHandler, gson)
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(androidClient)
    }

    @Test
    fun verifyGetChannelsSuccess() = runBlockingTest {
        var inspectCalled = false
        val client = TestHelpers.testClient(
            content = CHANNELS_SUCCESS_RESPONSE,
            status = HttpStatusCode.OK,
            headers = responseHeaders,
            requestInspector = {
                assertEquals(CHANNELS_PATH, it.url.encodedPath)
                assertEquals(HttpMethod.Get, it.method)
                inspectCalled = true
            }
        )

        whenever(androidClient.client(any())) doReturn Pair(client, HttpRequestBuilder())

        val result = channelResource.getChannels(defaultParameters, TEAM_ID)

        verifyAndroidClient(4)
        assertTrue(result.isSuccess)
        assertTrue(inspectCalled)

        val channels = result.getNullableData()
        assertNotNull(channels)
        assertEquals(2, channels?.size)

        assertEquals(channel, channels?.first())
    }

    @Test
    fun verifyChannelsError() = runBlockingTest {
        var inspectCalled = false
        val client = TestHelpers.testClient(
            content = BAD_CREDENTIAL_ERROR_RESPONSE,
            status = HttpStatusCode.Unauthorized,
            headers = responseHeaders,
            requestInspector = {
                assertEquals(
                    CHANNELS_PATH,
                    it.url.encodedPath
                )
                assertEquals(
                    HttpMethod.Get,
                    it.method
                )
                inspectCalled = true
            }
        )

        whenever(
            androidClient.client(
                any()
            )
        ) doReturn Pair(client, HttpRequestBuilder())

        val result =
            channelResource.getChannels(defaultParameters, teamId = TEAM_ID)

        verifyAndroidClient(4)
        assertTrue(result.isError)
        val error =
            ((result as Result.Error).exception as ResponseException).responseError
        assertEquals(
            "bad_credentials",
            error.errorCodes?.base?.firstOrNull()?.error
        )
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
        private val responseHeaders =
            headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
        private const val APPLICATION_ID = "com.thundermaps.saferme"
        private const val CHANNELS_PATH =
            "/api/v4/teams/$TEAM_ID/channels?fields=${ChannelResource.DEFAULT_FIELDS}"
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

        private val channel = Channel(
            id = 30108L,
            allowPublicViewers = false,
            allowPublicComments = true,
            allowUserDeleteOwnReports = false,
            areNewReportsAnonymous = true,
            isBannersEnabled = false,
            categoryId = 193563L,
            description = "",
            isFormLocked = false,
            isHazardChannel = false,
            isAddonChannel = false,
            isDeletableBy = false,
            isManageableBy = true,
            isOperableBy = true,
            isReportableBy = true,
            lastReportDate = "2021-10-11T12:57:42+13:00",
            logo = Avatar(
                mini = "missing/logos/mini.png",
                small = "missing/logos/small.png",
                medium = "missing/logos/medium.png",
                large = "missing/logos/large.png",
                huge = "missing/logos/huge.png"
            ),
            memberCount = 2,
            moderated = false,
            name = "Anonymous report",
            reportsCount = 4,
            riskControlsEditablity = "admins_only",
            slug = "Anonymous report",
            standardChannel = true,
            teamId = TEAM_ID,
            tuneInCount = 1,
            pinUrls = null
        )

        private val CHANNELS_SUCCESS_RESPONSE =
            """
                [
                  {
                    "id": 30108,
                    "allow_public_comments": true,
                    "allow_public_viewers": false,
                    "allow_user_delete_own_reports": false,
                    "are_new_reports_anonymous": true,
                    "banners_enabled": false,
                    "category_id": 193563,
                    "description": "",
                    "form_locked": false,
                    "hazard_channel": false,
                    "is_addon_channel": false,
                    "is_deletable_by": false,
                    "is_manageable_by": true,
                    "is_operable_by": true,
                    "is_reportable_by": true,
                    "last_report_date": "2021-10-11T12:57:42+13:00",
                    "logo": {
                      "mini": "missing/logos/mini.png",
                      "small": "missing/logos/small.png",
                      "medium": "missing/logos/medium.png",
                      "large": "missing/logos/large.png",
                      "huge": "missing/logos/huge.png"
                    },
                    "member_count": 2,
                    "moderated": false,
                    "name": "Anonymous report",
                    "reports_count": 4,
                    "risk_controls_editability": "admins_only",
                    "slug": "Anonymous report",
                    "standard_channel": true,
                    "team_id": 6688,
                    "tune_in_count": 1
                  },
                  {
                    "id": 30124,
                    "allow_public_comments": true,
                    "allow_public_viewers": false,
                    "allow_user_delete_own_reports": false,
                    "are_new_reports_anonymous": false,
                    "banners_enabled": false,
                    "category_id": 193594,
                    "description": "Auto change state",
                    "form_locked": false,
                    "hazard_channel": true,
                    "is_addon_channel": false,
                    "is_deletable_by": false,
                    "is_manageable_by": true,
                    "is_operable_by": true,
                    "is_reportable_by": true,
                    "last_report_date": "2021-10-07T09:48:50+13:00",
                    "logo": {
                      "mini": "missing/logos/mini.png",
                      "small": "missing/logos/small.png",
                      "medium": "missing/logos/medium.png",
                      "large": "missing/logos/large.png",
                      "huge": "missing/logos/huge.png"
                    },
                    "member_count": 2,
                    "moderated": false,
                    "name": "Auto change state hazard",
                    "reports_count": 1,
                    "risk_controls_editability": "admins_only",
                    "slug": "Auto change state hazard",
                    "standard_channel": false,
                    "team_id": 6688,
                    "tune_in_count": 1
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
