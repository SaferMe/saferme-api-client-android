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
import com.thundermaps.apilib.android.api.requests.models.UpdateEmailNotificationEnableBody
import com.thundermaps.apilib.android.api.requests.models.UpdateNameBody
import com.thundermaps.apilib.android.api.requests.models.UpdatePasswordBody
import com.thundermaps.apilib.android.api.requests.models.UpdateProfileBody
import com.thundermaps.apilib.android.api.resources.MeResource
import com.thundermaps.apilib.android.api.responses.models.Avatar
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.ResultHandler
import com.thundermaps.apilib.android.api.responses.models.UserDetails
import com.thundermaps.apilib.android.impl.AndroidClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
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
        val addressBody = UpdateAddressBody("New address")
        verifyUpdatedType(true, verifyBody = {
            assertEquals(gson.toJson(addressBody), it)
            assertTrue(it.contains(addressBody.address))
        }) {
            meResource.updateAddress(defaultParameters, addressBody)
        }
    }

    @Test
    fun verifyUpdateAddressError() {
        val addressBody = UpdateAddressBody("New address")
        verifyUpdatedType(false, {
            assertEquals(gson.toJson(addressBody), it)
            assertTrue(it.contains(addressBody.address))
        }) {
            meResource.updateAddress(defaultParameters, addressBody)
        }
    }

    @Test
    fun verifyUpdatePasswordSuccess() {
        val passwordBody = UpdatePasswordBody("49304390934", "439f09t34909")
        verifyUpdatedType(true, {
            assertEquals(gson.toJson(passwordBody), it)
            assertTrue(it.contains(passwordBody.currentPassword))
            assertTrue(it.contains(passwordBody.newPassword))
        }) {
            meResource.updatePassword(defaultParameters, passwordBody)
        }
    }

    @Test
    fun verifyUpdatePasswordError() {
        val passwordBody = UpdatePasswordBody("49304390934", "439f09t34909")
        verifyUpdatedType(false, {
            assertEquals(gson.toJson(passwordBody), it)
            assertTrue(it.contains(passwordBody.currentPassword))
            assertTrue(it.contains(passwordBody.newPassword))
        }) {
            meResource.updatePassword(defaultParameters, passwordBody)
        }
    }

    @Test
    fun verifyUpdateContactNumberSuccess() {
        val body = UpdateContactNumberBody("49304390934")
        verifyUpdatedType(true, {
            assertEquals(gson.toJson(body), it)
            assertTrue(it.contains(body.contactNumber))
        }) {
            meResource.updateContactNumber(
                defaultParameters,
                body
            )
        }
    }

    @Test
    fun verifyUpdateContactNumberError() {
        val body = UpdateContactNumberBody("49304390934")
        verifyUpdatedType(false, {
            assertEquals(gson.toJson(body), it)
            assertTrue(it.contains(body.contactNumber))
        }) {
            meResource.updateContactNumber(
                defaultParameters,
                body
            )
        }
    }

    @Test
    fun verifyUpdateEmailSuccess() {
        val body = EmailBody("test@gmail.com")
        verifyUpdatedType(true, {
            assertEquals(gson.toJson(body), it)
            assertTrue(it.contains(body.email))
        }) {
            meResource.updateEmail(defaultParameters, body)
        }
    }

    @Test
    fun verifyUpdateEmailError() {
        val body = EmailBody("test@gmail.com")
        verifyUpdatedType(false, {
            assertEquals(gson.toJson(body), it)
            assertTrue(it.contains(body.email))
        }) {
            meResource.updateEmail(defaultParameters, body)
        }
    }

    @Test
    fun verifyUpdateNameSuccess() {
        val body = UpdateNameBody("First name", "last name")
        verifyUpdatedType(true, {
            assertEquals(gson.toJson(body), it)
            assertTrue(it.contains(body.firstName))
            assertTrue(it.contains(body.lastName))
        }) {
            meResource.updateName(defaultParameters, body)
        }
    }

    @Test
    fun verifyUpdateNameError() {
        val body = UpdateNameBody("First name", "last name")
        verifyUpdatedType(false, {
            assertEquals(gson.toJson(body), it)
            assertTrue(it.contains(body.firstName))
            assertTrue(it.contains(body.lastName))
        }) {
            meResource.updateName(defaultParameters, body)
        }
    }

    @Test
    fun verifyUpdateProfileSuccess() {
        val body = UpdateProfileBody("First name", "last name", "email", "contact")
        verifyUpdatedTypeWithUserId(true, {
            assertEquals(gson.toJson(body), it)
            assertTrue(it.contains(body.firstName))
            assertTrue(it.contains(body.lastName))
            assertTrue(it.contains(body.email))
            assertTrue(it.contains(body.contactNumber))
        }) {
            meResource.updateUserProfile(defaultParameters, USER_ID, body)
        }
    }

    @Test
    fun verifyUpdateProfileError() {
        val body = UpdateProfileBody("First name", "last name", "email", "contact")
        verifyUpdatedTypeWithUserId(false, {
            assertEquals(gson.toJson(body), it)
            assertTrue(it.contains(body.firstName))
            assertTrue(it.contains(body.lastName))
            assertTrue(it.contains(body.email))
            assertTrue(it.contains(body.contactNumber))
        }) {
            meResource.updateUserProfile(defaultParameters, USER_ID, body)
        }
    }

    @Test
    fun verifyUpdateEmailNotificationEnabled() {
        val updateEmailNotificationEnableBody = UpdateEmailNotificationEnableBody(true)
        verifyUpdatedTypeWithUserId(true, verifyBody = {
            assertEquals(gson.toJson(updateEmailNotificationEnableBody), it)
        }) {
            meResource.updateEmailNotificationEnabled(
                defaultParameters,
                USER_ID,
                updateEmailNotificationEnableBody
            )
        }
    }

    @Test
    fun verifyUpdateEmailNotificationEnabledError() {
        val updateEmailNotificationEnableBody = UpdateEmailNotificationEnableBody(true)
        verifyUpdatedTypeWithUserId(false, {
            assertEquals(gson.toJson(updateEmailNotificationEnableBody), it)
        }) {
            meResource.updateEmailNotificationEnabled(
                defaultParameters,
                USER_ID,
                updateEmailNotificationEnableBody
            )
        }
    }

    private fun verifyUpdatedType(
        isSuccess: Boolean,
        verifyBody: (body: String) -> Unit,
        invoke: suspend () -> Result<Unit>
    ) = runBlockingTest {
        var inspectCalled = false
        val client = TestHelpers.testClient(
            content = "",
            status = if (isSuccess) HttpStatusCode.NoContent else HttpStatusCode.Forbidden,
            headers = responseHeaders,
            requestInspector = {
                assertEquals("$PATH", it.url.encodedPath)
                assertEquals(HttpMethod.Patch, it.method)
                verifyBody((it.body as TextContent).text)
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

    private fun verifyUpdatedTypeWithUserId(
        isSuccess: Boolean,
        verifyBody: (body: String) -> Unit,
        invoke: suspend () -> Result<Unit>
    ) = runBlockingTest {
        var inspectCalled = false
        val client = TestHelpers.testClient(
            content = "",
            status = if (isSuccess) HttpStatusCode.NoContent else HttpStatusCode.Forbidden,
            headers = responseHeaders,
            requestInspector = {
                assertEquals("$PATH_WITHOUT_ME$USER_ID", it.url.encodedPath)
                assertEquals(HttpMethod.Patch, it.method)
                verifyBody((it.body as TextContent).text)
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
                assertEquals("$PATH?fields=personal_account_option", it.url.encodedPath)
                assertEquals(HttpMethod.Get, it.method)
                inspectCalled = true
            }
        )

        whenever(androidClient.client(any())) doReturn Pair(client, HttpRequestBuilder())

        val userDetailsResult = meResource.getUserDetails(defaultParameters)

        verifyAndroidClient()

        assertTrue(userDetailsResult.isSuccess)
        assertNotNull(userDetailsResult.getNullableData())
        val userDetails = userDetailsResult.getNullableData()!!
        assertEquals(mockUserDetails.firstName, userDetails.firstName)
        assertEquals(mockUserDetails.lastName, userDetails.lastName)
        assertEquals(mockUserDetails.email, userDetails.email)
        assertEquals(mockUserDetails.address, userDetails.address)
        assertEquals(mockUserDetails.id, userDetails.id)
        assertEquals(mockUserDetails.emailNotificationEnabled, userDetails.emailNotificationEnabled)
        assertEquals(mockUserDetails.personalAccountOption, userDetails.personalAccountOption)
        assertEquals(mockUserDetails.acceptedTermsVersion, userDetails.acceptedTermsVersion)
        assertEquals(mockUserDetails.contactNumber, userDetails.contactNumber)

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
                assertEquals("$PATH?fields=personal_account_option", it.url.encodedPath)
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
        private const val PATH_WITHOUT_ME = "/api/v4/users/"
        private const val TEST_KEY = "Test Key"
        private const val TEST_INSTALL = "Install App"
        private const val TEST_APP = APPLICATION_ID
        private const val TEST_TEAM = "Test Team"
        private const val TEST_CLIENT_UUID = "Client UUID"
        private const val USER_ID = "12345"
        private val saferMeCredentials =
            SaferMeCredentials(TEST_KEY, TEST_INSTALL, TEST_APP, TEST_TEAM, TEST_CLIENT_UUID)
        private val defaultParameters = RequestParameters(
            customRequestHeaders = HashMap(),
            credentials = saferMeCredentials,
            host = Staging.servers.first(),
            port = null,
            api_version = 4
        )
        private val mockUserDetails = UserDetails(
            acceptedTermsVersion = 4,
            address = null,
            contactNumber = "32903290592",
            email = "abc@gmail.com",
            id = 85459L,
            firstName = "John",
            lastName = "User2",
            avatar = Avatar(
                "missing/avatars/mini.png",
                "missing/avatars/small.png",
                "missing/avatars/medium.png",
                "missing/avatars/large.png",
                "missing/avatars/huge.png"
            ),
            emailNotificationEnabled = null,
            personalAccountOption = null
        )
        private val USER_DETAIL_RESPONSE = """
            {
              "id": 85459,
              "first_name": "John",
              "last_name": "User2",
              "email": "abc@gmail.com",
              "avatar": {
                "mini": "missing/avatars/mini.png",
                "small": "missing/avatars/small.png",
                "medium": "missing/avatars/medium.png",
                "large": "missing/avatars/large.png",
                "huge": "missing/avatars/huge.png"
              },
              "contact_number": "32903290592",
              "accepted_terms_version": 4
            }
        """.trimIndent()
    }
}
