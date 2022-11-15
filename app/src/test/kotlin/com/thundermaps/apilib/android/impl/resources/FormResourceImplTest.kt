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
import com.thundermaps.apilib.android.api.fromJsonString
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.resources.FormResource
import com.thundermaps.apilib.android.api.responses.models.FormField
import com.thundermaps.apilib.android.api.responses.models.ResponseException
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.ResultHandler
import com.thundermaps.apilib.android.impl.AndroidClient
import com.thundermaps.apilib.android.impl.AndroidClient.Companion.gsonSerializer
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
class FormResourceImplTest {
    private val androidClient: AndroidClient = mock()
    private val resultHandler: ResultHandler = ResultHandler()

    private lateinit var formResource: FormResource

    @Before
    fun setUp() {
        formResource = FormResourceImpl(androidClient, resultHandler, gsonSerializer)
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(androidClient)
    }

    @Test
    fun verifyGetFormSuccess() = runBlockingTest {
        var inspectCalled = false
        val client = TestHelpers.testClient(
            content = FORM_SUCCESS_RESPONSE,
            status = HttpStatusCode.OK,
            headers = responseHeaders,
            requestInspector = {
                assertEquals(
                    FORM_PATH,
                    it.url.encodedPath
                )
                assertEquals(
                    HttpMethod.Get,
                    it.method
                )
                inspectCalled = true
            }
        )

        whenever(androidClient.client(any())) doReturn Pair(client, HttpRequestBuilder())

        val result = formResource.getForm(defaultParameters, CHANNEL_ID)

        verifyAndroidClient()
        assertTrue(result.isSuccess)
        assertTrue(inspectCalled)

        val form = result.getNullableData()
        assertNotNull(form)
        assertEquals(1, form!!.id)
        assertEquals("Cleaner streams Form", form.name)
        assertEquals(52, form.version)
        assertEquals(gsonSerializer.fromJsonString<List<FormField>>(FIELDS), form.fields)
    }

    @Test
    fun verifyGetFormError() = runBlockingTest {
        var inspectCalled = false
        val client = TestHelpers.testClient(
            content = BAD_CREDENTIAL_ERROR_RESPONSE,
            status = HttpStatusCode.Unauthorized,
            headers = responseHeaders,
            requestInspector = {
                assertEquals(
                    FORM_PATH,
                    it.url.encodedPath
                )
                assertEquals(
                    HttpMethod.Get,
                    it.method
                )
                inspectCalled = true
            }
        )

        whenever(androidClient.client(any())) doReturn Pair(client, HttpRequestBuilder())

        val result = formResource.getForm(defaultParameters, CHANNEL_ID)

        verifyAndroidClient()
        assertTrue(result.isError)
        val error = ((result as Result.Error).exception as ResponseException).responseError
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
        private val FORM_PATH =
            "/api/v4/channels/$CHANNEL_ID/form"
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

        private val FIELDS = """
            [
                {
                  "id":13,
                  "label":"Category",
                  "key":"f_1_36_13",
                  "field_type":"Category",
                  "form_order":0,
                  "data":"",
                  "mandatory":null,
                  "visibility":"public",
                  "field_visibility":"public",
                  "value":null,
                  "editable":true
                },
                {
                  "id":14,
                  "label":"A Check Box field",
                  "key":"f_1_38_14",
                  "field_type":"CheckBox",
                  "form_order":1,
                  "data":"{\"multi_select\":true,\"options\":[{\"label\":\"First Option\",\"value\":\"f_1_38_14_38_1\",\"enabled\":true,\"multi_option_id\":null,\"display_order\":0,\"is_default\":false},{\"label\":\"Another Option\",\"value\":\"f_1_38_14_39_2\",\"enabled\":true,\"multi_option_id\":null,\"display_order\":1,\"is_default\":false}]}",
                  "mandatory":null,
                  "visibility":"public",
                  "field_visibility":"public",
                  "value":null,
                  "editable":true
                },
                {
                  "id":17,
                  "label":"Drop Down",
                  "key":"f_1_48_17",
                  "field_type":"DropDown",
                  "form_order":2,
                  "data":"{\"multi_select\":false,\"options\":[{\"label\":\"Drop down option one\",\"value\":\"f_1_48_17_48_1\",\"enabled\":true,\"multi_option_id\":null,\"display_order\":0,\"is_default\":false},{\"label\":\"And the last option of the drop down\",\"value\":\"f_1_48_17_50_3\",\"enabled\":true,\"multi_option_id\":null,\"display_order\":1,\"is_default\":false}]}",
                  "mandatory":null,
                  "visibility":"public",
                  "field_visibility":"public",
                  "value":null,
                  "editable":true
                },
                {
                  "id":15,
                  "label":"Date and time field",
                  "key":"f_1_42_15",
                  "field_type":"DateAndTime",
                  "form_order":3,
                  "data":"",
                  "mandatory":null,
                  "visibility":"public",
                  "field_visibility":"public",
                  "value":null,
                  "editable":true
                },
                {
                  "id":12,
                  "label":"Description",
                  "key":"f_1_35_12",
                  "field_type":"Description",
                  "form_order":4,
                  "data":"",
                  "mandatory":null,
                  "visibility":"public",
                  "field_visibility":"public",
                  "value":null,
                  "editable":true
                },
                {
                  "id":11,
                  "label":"Short Text Box",
                  "key":"f_1_29_11",
                  "field_type":"ShortTextBox",
                  "form_order":5,
                  "data":"",
                  "mandatory":true,
                  "visibility":"public",
                  "field_visibility":"public",
                  "value":null,
                  "editable":true
                },
                {
                  "id":6,
                  "label":"Amazing pictures",
                  "key":"f_1_8_6",
                  "field_type":"Image",
                  "form_order":6,
                  "data":"",
                  "mandatory":null,
                  "visibility":"public",
                  "field_visibility":"public",
                  "value":null,
                  "editable":true
                },
                {
                  "id":9,
                  "label":"Awesome files",
                  "key":"f_1_18_9",
                  "field_type":"FileUpload",
                  "form_order":7,
                  "data":"",
                  "mandatory":null,
                  "visibility":"public",
                  "field_visibility":"public",
                  "value":null,
                  "editable":true
                },
                {
                  "id":10,
                  "label":"Important files",
                  "key":"f_1_19_10",
                  "field_type":"FileUpload",
                  "form_order":8,
                  "data":"",
                  "mandatory":null,
                  "visibility":"public",
                  "field_visibility":"public",
                  "value":null,
                  "editable":true
                }
              ]
        """.trimIndent()
        private val FORM_SUCCESS_RESPONSE =
            """
                {
                  "id":1,
                  "name":"Cleaner streams Form",
                  "version":52,
                  "fields": $FIELDS
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
