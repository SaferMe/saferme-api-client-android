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
import com.thundermaps.apilib.android.api.resources.CategoryResource
import com.thundermaps.apilib.android.api.responses.models.Category
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
class CategoryResourceImplTest {
    private val androidClient: AndroidClient = mock()
    private val resultHandler: ResultHandler = ResultHandler()
    private val gson = Gson()

    private lateinit var categoryResource: CategoryResource

    @Before
    fun setUp() {
        categoryResource = CategoryResourceImpl(androidClient, resultHandler, gson)
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(androidClient)
    }

    @Test
    fun verifyGetChannelsSuccess() = runBlockingTest {
        var inspectCalled = false
        val client = TestHelpers.testClient(
            content = CATEGORIES_SUCCESS_RESPONSE,
            status = HttpStatusCode.OK,
            headers = responseHeaders,
            requestInspector = {
                assertEquals(CATEGORIES_PATH, it.url.encodedPath)
                assertEquals(HttpMethod.Get, it.method)
                inspectCalled = true
            }
        )

        whenever(androidClient.client(any())) doReturn Pair(client, HttpRequestBuilder())

        val result = categoryResource.getCategory(
            defaultParameters,
            CHANNEL_ID
        )

        verifyAndroidClient()
        assertTrue(result.isSuccess)
        assertTrue(inspectCalled)

        val categories = result.getNullableData()
        assertNotNull(categories)
        assertEquals(10, categories?.size)

        val firstCategory = categories?.firstOrNull()
        assertEquals(category, firstCategory)
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
                    CATEGORIES_PATH,
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

        val result = categoryResource.getCategory(defaultParameters, channelId = CHANNEL_ID)

        verifyAndroidClient()
        assertTrue(result.isError)
        val error =
            ((result as Result.Error).exception as ResponseException).responseError
        assertEquals(
            "bad_credentials",
            error.errorCodes?.base?.firstOrNull()?.error
        )
        assertTrue(inspectCalled)
    }

    private fun verifyAndroidClient() {
        val parameterCaptor = argumentCaptor<RequestParameters>()
        verify(androidClient).client(parameterCaptor.capture())
        val requestParameters = parameterCaptor.firstValue
        assertEquals(
            4,
            requestParameters.api_version
        )
        assertEquals(
            saferMeCredentials,
            requestParameters.credentials
        )
    }

    companion object {
        private const val CHANNEL_ID = 6688
        private val responseHeaders =
            headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
        private const val APPLICATION_ID = "com.thundermaps.saferme"
        private val CATEGORIES_PATH =
            "/api/v4/channels/$CHANNEL_ID/categories?fields=${
                CategoryResource.CATEGORY_FIELDS.joinToString(
                    ","
                )
            }"
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
        private val category = Category(
            193524,
            "Test hazard",
            "Report Category",
            0,
            "aqua",
            85371,
            null,
            "default"
        )
        private val CATEGORIES_SUCCESS_RESPONSE =
            """
                [
                  {
                    "id": 193524,
                    "name": "Test hazard",
                    "label_name": "Report Category",
                    "depth": 0,
                    "pin_color": "aqua",
                    "position": 85371,
                    "parent_id": null,
                    "pin_appearance":"default"
                  },
                  {
                    "id": 193573,
                    "name": "House repair",
                    "label_name": "Secondary Category",
                    "depth": 1,
                    "pin_color": "green",
                    "position": 2,
                    "parent_id": 193524,
                    "pin_appearance":"lightbulb"
                  },
                  {
                    "id": 193576,
                    "name": "Construction",
                    "label_name": "Secondary Category",
                    "depth": 1,
                    "pin_color": "yellow",
                    "position": 1,
                    "parent_id": 193524,
                    "pin_appearance":"question"
                  },
                  {
                    "id": 193577,
                    "name": "House",
                    "label_name": "Tertiary Category",
                    "depth": 2,
                    "pin_color": "slate",
                    "position": 3,
                    "parent_id": 193576,
                    "pin_appearance":"geo"
                  },
                  {
                    "id": 193578,
                    "name": "Road",
                    "label_name": "Tertiary Category",
                    "depth": 2,
                    "pin_color": "blue-violet",
                    "position": 2,
                    "parent_id": 193576,
                    "pin_appearance":"notes"
                    
                  },
                  {
                    "id": 193579,
                    "name": "Bridge",
                    "label_name": "Tertiary Category",
                    "depth": 2,
                    "pin_color": "aqua",
                    "position": 1,
                    "parent_id": 193576,
                    "pin_appearance":"broken"
                  },
                  {
                    "id": 193580,
                    "name": "House 1",
                    "label_name": "Tertiary Category",
                    "depth": 2,
                    "pin_color": "green",
                    "position": 1,
                    "parent_id": 193573,
                    "pin_appearance":"police"
                  },
                  {
                    "id": 193581,
                    "name": "House 2",
                    "label_name": "Tertiary Category",
                    "depth": 2,
                    "pin_color": "red",
                    "position": 2,
                    "parent_id": 193573,
                    "pin_appearance":"fire"
                  },
                  {
                    "id": 193582,
                    "name": "House 12",
                    "label_name": null,
                    "depth": 3,
                    "pin_color": "tm-green",
                    "position": 3,
                    "parent_id": 193580,
                    "pin_appearance":"hazard"
                  },
                  {
                    "id": 193583,
                    "name": "House 11",
                    "label_name": null,
                    "depth": 3,
                    "pin_color": "green",
                    "position": 2,
                    "parent_id": 193580,
                    "pin_appearance":"house"
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
