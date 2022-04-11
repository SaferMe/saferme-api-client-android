package com.thundermaps.apilib.android.impl.resources

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.thundermaps.apilib.android.api.SaferMeCredentials
import com.thundermaps.apilib.android.api.com.thundermaps.env.Staging
import com.thundermaps.apilib.android.api.requests.RequestParameters
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
@OptIn(KtorExperimentalAPI::class)
class UploadAuthorizationTest {
    private val androidClient: AndroidClient = mock()
    private val resultHandler: ResultHandler = ResultHandler()
    private val gson = AndroidClient.gsonSerializer

    private lateinit var attachmentResource: AttachmentResourceImpl

    @Before
    fun setup() {
        attachmentResource = AttachmentResourceImpl(androidClient, resultHandler, gson)
    }

    @After
    fun teardown() {
        verifyNoMoreInteractions(androidClient)
    }

    @Test
    fun `verify get upload authorization success`() = runBlockingTest {
        var inspectCalled = false
        val client = TestHelpers.testClient(
            content = uploadAuthorizationResponseSuccess,
            status = HttpStatusCode.OK,
            headers = responseHeaders,
            requestInspector = {
                assertEquals(
                    UPLOAD_PATH,
                    it.url.encodedPath
                )
                assertEquals(
                    HttpMethod.Get,
                    it.method
                )
                inspectCalled = true
            }
        )
        val requestBuilder = HttpRequestBuilder()

        val urlBuilder = AndroidClient.baseUrlBuilder(defaultParameters)

        val result = attachmentResource.getUploadAuthentication(urlBuilder, client, requestBuilder)

        assertEquals(uploadAuthorizationResponse, result.getNullableData())

        assertTrue(inspectCalled)
    }

    companion object {
        private val responseHeaders =
            headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
        private const val UPLOAD_PATH =
            "/api/v4/file_attachments/upload_authorization?content_type=image/png"

        private const val APPLICATION_ID = "com.thundermaps.saferme"
        private const val TEST_KEY = "Test Key"
        private const val TEST_INSTALL = "Install App"
        private const val TEST_APP = APPLICATION_ID
        private const val TEST_TEAM = "Test Team"
        private const val TEST_CLIENT_UUID = "client uuid"
        private val saferMeCredentials =
            SaferMeCredentials(TEST_KEY, TEST_INSTALL, TEST_APP, TEST_TEAM, TEST_CLIENT_UUID)
        private val defaultParameters = RequestParameters(
            customRequestHeaders = emptyMap(),
            credentials = saferMeCredentials,
            host = Staging.servers.first(),
            port = null,
            api_version = 4
        )

        private const val key = "async_uploads/9c457cb6-cc9a-4e6b-9b07-d50de3c45fb9"
        private const val policy = "eyJleHBpcmF0aW9uIjoiM"
        private const val amzSignature =
            "58af91e9a564683a14c0a05c70d1c7bff7e448361ce9cf525c04a114a323a93201"
        private const val uploadUrl = "https://test-uploads.s3.amazonaws.com"

        private val uploadAuthorizationResponse = UploadAuthorizationResponse(
            UploadAuthorization(
                key = key,
                keyPrefix = "$key/",
                url = uploadUrl,
                fields = AuthorizationFields(
                    key = key,
                    successActionStatus = "201",
                    contentType = "image/png",
                    policy = policy,
                    amzCredential = "AKIAICPNAEAPUN54KXIA/20220410/us-east-1/s3/aws4_request",
                    amzSignature = amzSignature,
                    amzAlgorithm = "AWS4-HMAC-SHA256",
                    amzDate = "20220410T233530Z"
                )
            )
        )
        private const val newResponse = """
            {"upload_authorization":{"method":"POST","key":"async_uploads/301ac994-356a-46d4-94a9-ed494c2fc9c4","key_prefix":"async_uploads/301ac994-356a-46d4-94a9-ed494c2fc9c4/","url":"https://thundermaps-uploads.s3.amazonaws.com","fields":{"key":"async_uploads/301ac994-356a-46d4-94a9-ed494c2fc9c4","success_action_status":"201","Content-Type":"image/png","policy":"eyJleHBpcmF0aW9uIjoiMjAyMi0wNC0xMVQxMjozNDoxMloiLCJjb25kaXRpb25zIjpbeyJidWNrZXQiOiJ0aHVuZGVybWFwcy11cGxvYWRzIn0sWyJzdGFydHMtd2l0aCIsIiRrZXkiLCJhc3luY191cGxvYWRzLzMwMWFjOTk0LTM1NmEtNDZkNC05NGE5LWVkNDk0YzJmYzljNCJdLFsic3RhcnRzLXdpdGgiLCIkQ29udGVudC1UeXBlIiwiIl0seyJzdWNjZXNzX2FjdGlvbl9zdGF0dXMiOiIyMDEifSx7IkNvbnRlbnQtVHlwZSI6ImltYWdlL3BuZyJ9LHsieC1hbXotY3JlZGVudGlhbCI6IkFLSUFJQ1BOQUVBUFVONTRLWElBLzIwMjIwNDExL3VzLWVhc3QtMS9zMy9hd3M0X3JlcXVlc3QifSx7IngtYW16LWFsZ29yaXRobSI6IkFXUzQtSE1BQy1TSEEyNTYifSx7IngtYW16LWRhdGUiOiIyMDIyMDQxMVQxMTM0MTJaIn1dfQ==","x-amz-credential":"AKIAICPNAEAPUN54KXIA/20220411/us-east-1/s3/aws4_request","x-amz-algorithm":"AWS4-HMAC-SHA256","x-amz-date":"20220411T113412Z","x-amz-signature":"5e35d7fb8c1f8d8e175fb2e33dd758b7e757811b1601e1e992a09b92f60ccf5a"},"required_fields":["Content-Type","file"]}}
        """
        private const val uploadAuthorizationResponseSuccess = """
            {
              "upload_authorization": {
                "method": "POST",
                "key": "$key",
                "key_prefix": "$key/",
                "url": "$uploadUrl",
                "fields": {
                  "key": "$key",
                  "success_action_status": "201",
                  "Content-Type": "image/png",
                  "policy": "$policy",
                  "x-amz-credential": "AKIAICPNAEAPUN54KXIA/20220410/us-east-1/s3/aws4_request",
                  "x-amz-algorithm": "AWS4-HMAC-SHA256",
                  "x-amz-date": "20220410T233530Z",
                  "x-amz-signature": $amzSignature
                },
                "required_fields": [
                  "Content-Type",
                  "file"
                ]
              }
            }
        """
    }
}
