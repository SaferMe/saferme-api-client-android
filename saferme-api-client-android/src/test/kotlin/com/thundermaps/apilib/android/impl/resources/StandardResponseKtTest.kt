package com.thundermaps.apilib.android.impl.resources

import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.thundermaps.apilib.android.api.responses.models.ResponseException
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.ResultHandler
import com.thundermaps.apilib.android.api.responses.models.Sessions
import io.ktor.client.call.HttpClientCall
import io.ktor.client.response.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.io.ByteReadChannel
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@KtorExperimentalAPI
@ExperimentalCoroutinesApi
class StandardResponseKtTest {
    private val gson = Gson()
    private val statusMock: HttpStatusCode = mock()
    private val responseMock: HttpResponse = mock {
        on { status } doReturn statusMock
    }

    private val client: HttpClientCall = mock {
        on { response } doReturn responseMock
    }

    private lateinit var resultHandler: ResultHandler

    @Before
    fun setUp() {
        resultHandler = ResultHandler()
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(responseMock, statusMock)
    }

    @Test
    fun verifyProcessResultStatusOKSuccess() = runBlockingTest {
        whenever(statusMock.value).thenReturn(HttpStatusCode.OK.value)
        whenever(responseMock.content).thenReturn(ByteReadChannel(LOGIN_SUCCESS_RESPONSE))
        val result: Result<Sessions> = resultHandler.processResult(client, gson)

        assertTrue(result.isSuccess)
        val sessions = result.getNullableData()
        assertNotNull(sessions)

        verify(responseMock).status
        verify(responseMock).content
        verify(statusMock).value
    }

    @Test
    fun verifyProcessResultStatusOKError() = runBlockingTest {
        whenever(statusMock.value).thenReturn(HttpStatusCode.OK.value)
        whenever(responseMock.content).thenReturn(ByteReadChannel(""))
        val result: Result<Sessions> = resultHandler.processResult(client, gson)

        assertTrue(result.isError)

        verify(responseMock).status
        verify(responseMock).content
        verify(statusMock).value
    }

    @Test
    fun verifyProcessResultStatusOther200() = runBlockingTest {
        whenever(statusMock.value).thenReturn(HttpStatusCode.PartialContent.value)
        whenever(responseMock.content).thenReturn(ByteReadChannel(LOGIN_SUCCESS_RESPONSE))
        val result: Result<Sessions> = resultHandler.processResult(client, gson)

        assertTrue(result.isSuccess)
        val sessions = result.getNullableData()
        assertNotNull(sessions)

        verify(responseMock).status
        verify(responseMock).content
        verify(statusMock).value
    }

    @Test
    fun verifyProcessResultStatusNoContent() = runBlockingTest {
        whenever(statusMock.value).thenReturn(HttpStatusCode.NoContent.value)
        whenever(responseMock.content).thenReturn(ByteReadChannel(""))
        val result: Result<Unit> = resultHandler.processResult(client, gson)

        assertTrue(result.isSuccess)
        val unit = result.getNullableData()
        assertNotNull(unit)

        verify(responseMock).status
        verify(responseMock).content
        verify(statusMock).value
    }

    @Test
    fun verifyProcessResultSaferMeError() = runBlockingTest {
        whenever(statusMock.value).thenReturn(HttpStatusCode.Unauthorized.value)
        whenever(responseMock.content).thenReturn(ByteReadChannel(LOGIN_BAD_CREDENTIAL_ERROR_RESPONSE))
        val result: Result<Sessions> = resultHandler.processResult(client, gson)

        assertTrue(result.isError)
        val error = ((result as Result.Error).exception as ResponseException).responseError
        assertEquals("bad_credentials", error.errorCodes?.base?.firstOrNull()?.error)

        verify(responseMock).status
        verify(responseMock).content
        verify(statusMock).value
    }

    @Test
    fun verifyProcessResultOtherError() = runBlockingTest {
        whenever(statusMock.value).thenReturn(HttpStatusCode.UnsupportedMediaType.value)
        whenever(responseMock.content).thenReturn(ByteReadChannel("Unsupport"))
        val result: Result<Sessions> = resultHandler.processResult(client, gson)

        assertTrue(result.isError)

        verify(responseMock).status
        verify(responseMock).content
        verify(statusMock).value
    }

    companion object {
        private val LOGIN_SUCCESS_RESPONSE =
            """
                {
                    "auth_token":"823e0ea66f59cebeffaf969dd16d8929",
                    "consent_required":false,
                    "team_id":null,
                    "user_id":92536,
                    "personal_account_option":false,
                    "profile_details_pending":false,
                    "password_update_pending":false
                }
            """.trimIndent()
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
    }
}
