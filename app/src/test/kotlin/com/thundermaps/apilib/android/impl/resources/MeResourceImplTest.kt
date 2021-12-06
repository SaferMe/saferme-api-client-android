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
import com.thundermaps.apilib.android.api.requests.models.EmailBody
import com.thundermaps.apilib.android.api.requests.models.UpdateAddressBody
import com.thundermaps.apilib.android.api.requests.models.UpdateContactNumberBody
import com.thundermaps.apilib.android.api.requests.models.UpdateNameBody
import com.thundermaps.apilib.android.api.requests.models.UpdatePasswordBody
import com.thundermaps.apilib.android.api.resources.MeResource
import com.thundermaps.apilib.android.api.responses.models.ResultHandler
import com.thundermaps.apilib.android.api.responses.models.Result
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
class MeResourceImplTest {
    private val androidClient: AndroidClient = mock()
    private val resultHandler: ResultHandler = ResultHandler()
    private val gson = Gson()
    private lateinit var meResource: MeResource

    @Before
    fun setUp() {
        meResource = MeResourceImpl(androidClient, resultHandler, gson)
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(androidClient)
    }

    @Test
    fun verifyUpdateAddressSuccess() {
        verifyUpdatedType(true) {
            meResource.updateAddress(defaultParameters, UpdateAddressBody("New address"))
        }
    }

    @Test
    fun verifyUpdateAddressError() {
        verifyUpdatedType(false) {
            meResource.updateAddress(defaultParameters, UpdateAddressBody("New address"))
        }
    }

    @Test
    fun verifyUpdatePasswordSuccess() {
        verifyUpdatedType(true) {
            meResource.updatePassword(defaultParameters, UpdatePasswordBody(currentPassword = "49304390934", newPassword = "439f09t34909"))
        }
    }

    @Test
    fun verifyUpdatePasswordError() {
        verifyUpdatedType(false) {
            meResource.updatePassword(defaultParameters, UpdatePasswordBody(currentPassword = "49304390934", newPassword = "439f09t34909"))
        }
    }

    @Test
    fun verifyUpdateContactNumberSuccess() {
        verifyUpdatedType(true) {
            meResource.updateContactNumber(defaultParameters, UpdateContactNumberBody("49304390934"))
        }
    }

    @Test
    fun verifyUpdateContactNumberError() {
        verifyUpdatedType(false) {
            meResource.updateContactNumber(defaultParameters, UpdateContactNumberBody("49304390934"))
        }
    }

    @Test
    fun verifyUpdateEmailSuccess() {
        verifyUpdatedType(true) {
            meResource.updateEmail(defaultParameters, EmailBody("test@gmail.com"))
        }
    }

    @Test
    fun verifyUpdateEmailError() {
        verifyUpdatedType(false) {
            meResource.updateEmail(defaultParameters, EmailBody("test@gmail.com"))
        }
    }

    @Test
    fun verifyUpdateNameSuccess() {
        verifyUpdatedType(true) {
            meResource.updateName(defaultParameters, UpdateNameBody("First name", "last name"))
        }
    }

    @Test
    fun verifyUpdateNameError() {
        verifyUpdatedType(false) {
            meResource.updateName(defaultParameters, UpdateNameBody("First name", "last name"))
        }
    }

    private fun verifyUpdatedType(isSuccess: Boolean, invoke: suspend () -> Result<Unit>) = runBlockingTest {
        var inspectCalled = false
        val client = TestHelpers.testClient(
            content = "",
            status = if (isSuccess) HttpStatusCode.NoContent else HttpStatusCode.Forbidden,
            headers = responseHeaders,
            requestInspector = {
                assertEquals(PATH, it.url.encodedPath)
                assertEquals(HttpMethod.Patch, it.method)
                inspectCalled = true
            }
        )

        whenever(androidClient.client(any())) doReturn Pair(client, HttpRequestBuilder())

        val result = invoke()

        verifyAndroidClient()

        if (isSuccess) {
            assertTrue(result.isSuccess)
        } else {
            assertTrue(result.isError)
        }

        assertTrue(inspectCalled)
    }

    @Test
    fun verifyGetDetailsSuccess() = runBlockingTest {
        var inspectCalled = false
        val client = TestHelpers.testClient(
            content = USER_DETAIL_RESPONSE,
            status = HttpStatusCode.OK,
            headers = responseHeaders,
            requestInspector = {
                assertEquals("${PATH}?fields=personal_account_option", it.url.encodedPath)
                assertEquals(HttpMethod.Get, it.method)
                inspectCalled = true
            }
        )

        whenever(androidClient.client(any())) doReturn Pair(client, HttpRequestBuilder())

        val userDetailsResult = meResource.getUserDetails(defaultParameters)

        verifyAndroidClient()

        assertTrue(userDetailsResult.isSuccess)
        assertNotNull(userDetailsResult.getNullableData())
        val userDetails = userDetailsResult.getNullableData()
        assertEquals("liem.vo+user2@safer.me", userDetails?.email)
        assertEquals("Liem", userDetails?.firstName)

        assertTrue(inspectCalled)
    }

    @Test
    fun verifyGetDetailsError() = runBlockingTest {
        var inspectCalled = false
        val client = TestHelpers.testClient(
            content = "",
            status = HttpStatusCode.BadGateway,
            headers = responseHeaders,
            requestInspector = {
                assertEquals("${PATH}?fields=personal_account_option", it.url.encodedPath)
                assertEquals(HttpMethod.Get, it.method)
                inspectCalled = true
            }
        )

        whenever(androidClient.client(any())) doReturn Pair(client, HttpRequestBuilder())

        val userDetailsResult = meResource.getUserDetails(defaultParameters)

        verifyAndroidClient()

        assertTrue(userDetailsResult.isError)

        assertTrue(inspectCalled)
    }

    private fun verifyAndroidClient(expectedVersion: Int = 4) {
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
        private const val PATH = "/api/v4/users/me"
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
        private val USER_DETAIL_RESPONSE = """
            {
              "id": 85459,
              "first_name": "Liem",
              "last_name": "User2",
              "email": "liem.vo+user2@safer.me",
              "avatar": {
                "mini": "missing/avatars/mini.png",
                "small": "missing/avatars/small.png",
                "medium": "missing/avatars/medium.png",
                "large": "missing/avatars/large.png",
                "huge": "missing/avatars/huge.png"
              },
              "accepted_terms_version": 4
            }
        """.trimIndent()
    }
}