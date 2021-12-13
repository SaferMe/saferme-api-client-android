package com.thundermaps.apilib.android.impl.resources

import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.thundermaps.apilib.android.api.com.thundermaps.env.EnvironmentManager
import com.thundermaps.apilib.android.api.com.thundermaps.env.Staging
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.requests.models.EmailBody
import com.thundermaps.apilib.android.api.requests.models.SessionBody
import com.thundermaps.apilib.android.api.requests.models.User
import com.thundermaps.apilib.android.api.resources.SessionsResource
import com.thundermaps.apilib.android.api.responses.models.ErrorCode
import com.thundermaps.apilib.android.api.responses.models.ErrorCodes
import com.thundermaps.apilib.android.api.responses.models.Failures
import com.thundermaps.apilib.android.api.responses.models.ResponseError
import com.thundermaps.apilib.android.api.responses.models.ResponseException
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.ResultHandler
import com.thundermaps.apilib.android.api.responses.models.Sessions
import com.thundermaps.apilib.android.api.responses.models.isAccountLocked
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@KtorExperimentalAPI
@ExperimentalCoroutinesApi
class SessionsImplTest {
    private val androidClient: AndroidClient = mock()
    private val environmentManager: EnvironmentManager = mock {
        on { environment } doReturn Staging
    }
    private val resultHandler: ResultHandler = ResultHandler()
    private val gson = Gson()

    private lateinit var sessions: SessionsResource

    @Before
    fun setUp() {
        sessions = SessionsImpl(androidClient, environmentManager, resultHandler, gson)
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(androidClient, environmentManager)
    }

    @Test
    fun verifyStagingTrue() {
        whenever(environmentManager.isStaging()) doReturn true

        val isStaging = sessions.isStaging()

        assertTrue(isStaging)
        verify(environmentManager).isStaging()
    }

    @Test
    fun verifyStagingFalse() {
        whenever(environmentManager.isStaging()) doReturn false

        val isStaging = sessions.isStaging()

        assertFalse(isStaging)
        verify(environmentManager).isStaging()
    }

    @Test
    fun verifyLoginSuccess() = runBlockingTest {
        var inspectCalled = false
        val client = TestHelpers.testClient(
            content = LOGIN_SUCCESS_RESPONSE,
            status = HttpStatusCode.OK,
            headers = responseHeaders,
            requestInspector = {
                assertEquals(LOGIN_PATH, it.url.encodedPath)
                assertEquals(HttpMethod.Post, it.method)
                val bodyText = (it.body as TextContent).text
                assertEquals(gson.toJson(sessionBody), bodyText)
                assertTrue(bodyText.contains(sessionBody.user.email))
                assertTrue(bodyText.contains(sessionBody.user.password))
                inspectCalled = true
            }
        )

        whenever(androidClient.client(any())) doReturn Pair(client, HttpRequestBuilder())

        val loginResult = sessions.login(sessionBody, APPLICATION_ID)

        verifyAndroidClient()

        verify(environmentManager).environment

        assertTrue(loginResult.isSuccess)
        assertNotNull(loginResult.getNullableData())
        val sessions = loginResult.getNullableData()!!
        val expectedTeamId = 49033L
        assertEquals(sessionsMock.copy(teamId = expectedTeamId), sessions)
        assertEquals(sessionsMock.apiKey, sessions.apiKey)
        assertFalse(sessions.consentRequired)
        assertEquals(expectedTeamId, sessions.teamId)
        assertEquals(sessionsMock.userId, sessions.userId)
        assertFalse(sessions.personalAccountOption)
        assertFalse(sessions.profileDetailsPending)
        assertFalse(sessions.passwordUpdatePending)
        assertTrue(inspectCalled)
    }

    @Test
    fun verifyLoginErrorBadCredential() = runBlockingTest {
        var inspectCalled = false
        val client = TestHelpers.testClient(
            content = LOGIN_BAD_CREDENTIAL_ERROR_RESPONSE,
            status = HttpStatusCode.Unauthorized,
            headers = responseHeaders,
            requestInspector = {
                assertEquals(LOGIN_PATH, it.url.encodedPath)
                assertEquals(HttpMethod.Post, it.method)
                inspectCalled = true
            }
        )

        whenever(androidClient.client(any())) doReturn Pair(client, HttpRequestBuilder())

        val loginResult = sessions.login(sessionBody, APPLICATION_ID)

        verifyAndroidClient()

        verify(environmentManager).environment

        assertTrue(loginResult.isError)
        val error = ((loginResult as Result.Error).exception as ResponseException).responseError
        assertEquals("bad_credentials", error.errorCodes?.base?.firstOrNull()?.error)
        assertFalse(error.isAccountLocked())
        assertEquals(badCredentialsResponseError, error)
        assertTrue(inspectCalled)
    }

    @Test
    fun verifyLoginErrorLockedAccount() = runBlockingTest {
        var inspectCalled = false
        val client = TestHelpers.testClient(
            content = LOGIN_LOCKED_ACCOUNT_ERROR_RESPONSE,
            status = HttpStatusCode.Forbidden,
            headers = responseHeaders,
            requestInspector = {
                assertEquals(LOGIN_PATH, it.url.encodedPath)
                assertEquals(HttpMethod.Post, it.method)
                inspectCalled = true
            }
        )

        whenever(androidClient.client(any())) doReturn Pair(client, HttpRequestBuilder())

        val loginResult = sessions.login(sessionBody, APPLICATION_ID)

        verifyAndroidClient()
        verify(environmentManager).environment
        assertTrue(loginResult.isError)
        val error = ((loginResult as Result.Error).exception as ResponseException).responseError
        assertTrue(error.isAccountLocked())
        assertEquals(lockedResponseError, error)
        assertTrue(inspectCalled)
    }

    @Test
    fun verifyLoginOtherError() = runBlockingTest {
        var inspectCalled = false
        val client = TestHelpers.testClient(
            content = "{}",
            status = HttpStatusCode.BadGateway,
            headers = responseHeaders,
            requestInspector = {
                assertEquals(LOGIN_PATH, it.url.encodedPath)
                assertEquals(HttpMethod.Post, it.method)
                inspectCalled = true
            }
        )

        whenever(androidClient.client(any())) doReturn Pair(client, HttpRequestBuilder())

        val loginResult = sessions.login(sessionBody, APPLICATION_ID)

        verifyAndroidClient()
        verify(environmentManager).environment
        assertTrue(loginResult.isError)
        assertTrue(inspectCalled)
    }

    @Test
    fun verifyRequestPasswordSuccess() = runBlockingTest {
        var inspectCalled = false
        val client = TestHelpers.testClient(
            content = REQUEST_PASSWORD_SUCCESS_RESPONSE,
            status = HttpStatusCode.OK,
            headers = responseHeaders,
            requestInspector = {
                assertEquals(RESET_PASSWORD_PATH, it.url.encodedPath)
                assertEquals(HttpMethod.Post, it.method)
                inspectCalled = true
            }
        )

        whenever(androidClient.client(any())) doReturn Pair(client, HttpRequestBuilder())

        val requestPasswordResult = sessions.requestPassword(emailBody, APPLICATION_ID)

        verifyAndroidClient(4)
        verify(environmentManager).environment
        assertTrue(requestPasswordResult.isSuccess)
        assertEquals(emailBody.email, requestPasswordResult.getNullableData())
        assertTrue(inspectCalled)
    }

    @Test
    fun verifyRequestError() = runBlockingTest {
        var inspectCalled = false
        val client = TestHelpers.testClient(
            content = "",
            status = HttpStatusCode.BadGateway,
            headers = responseHeaders,
            requestInspector = {
                assertEquals(RESET_PASSWORD_PATH, it.url.encodedPath)
                assertEquals(HttpMethod.Post, it.method)
                inspectCalled = true
            }
        )

        whenever(androidClient.client(any())) doReturn Pair(client, HttpRequestBuilder())

        val requestPasswordResult = sessions.requestPassword(emailBody, APPLICATION_ID)

        verifyAndroidClient(4)
        verify(environmentManager).environment
        assertTrue(requestPasswordResult.isError)
        assertTrue(inspectCalled)
    }

    private fun verifyAndroidClient(expectedVersion: Int = 3) {
        val parameterCaptor = argumentCaptor<RequestParameters>()
        verify(androidClient).client(parameterCaptor.capture())
        val requestParameters = parameterCaptor.firstValue
        assertEquals(expectedVersion, requestParameters.api_version)
        assertNull(requestParameters.credentials)
        assertEquals(Staging.servers.first(), requestParameters.host)
        assertEquals(APPLICATION_ID, requestParameters.customRequestHeaders["X-AppID"])
    }

    companion object {
        private const val APPLICATION_ID = "com.thundermaps.saferme"
        private const val LOGIN_PATH = "/api/v3/sessions"
        private const val RESET_PASSWORD_PATH = "/api/v4/reset_passwords/request_token"
        private val responseHeaders =
            headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
        private val sessionBody = SessionBody(User("test@gmail.com", "abce9w0"))
        private val sessionsMock = Sessions(
            apiKey = "823e0ea66f59cebeffaf969dd16d8929",
            consentRequired = false,
            teamId = null,
            userId = 92536,
            personalAccountOption = false,
            profileDetailsPending = false,
            passwordUpdatePending = false
        )
        private val LOGIN_SUCCESS_RESPONSE =
            """
                {
                    "auth_token":"823e0ea66f59cebeffaf969dd16d8929",
                    "consent_required":false,
                    "team_id":49033,
                    "user_id":92536,
                    "personal_account_option":false,
                    "profile_details_pending":false,
                    "password_update_pending":false
                }
            """.trimIndent()
        private val badCredentialsResponseError = ResponseError(
            errors = listOf("Please enter your email and password"),
            failures = Failures(listOf("Please enter your email and password")),
            errorCodes = ErrorCodes(listOf(ErrorCode("bad_credentials")))
        )
        private val LOGIN_BAD_CREDENTIAL_ERROR_RESPONSE =
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
        private val lockedResponseError = ResponseError(
            errors = listOf("Locked account"),
            failures = Failures(listOf("Your account has been locked")),
            errorCodes = ErrorCodes(listOf(ErrorCode("account_locked")))
        )
        private val LOGIN_LOCKED_ACCOUNT_ERROR_RESPONSE =
            """
                {
                   "errors":[
                      "Locked account"
                   ],
                   "failures":{
                      "base":[
                         "Your account has been locked"
                      ]
                   },
                   "error_codes":{
                      "base":[
                         {
                            "error":"account_locked"
                         }
                      ]
                   }
                }
            """.trimIndent()
        private val emailBody = EmailBody("test@gmail.com")
        private val REQUEST_PASSWORD_SUCCESS_RESPONSE = """
            {
                "email": ${emailBody.email}
            }
        """.trimIndent()
    }
}
