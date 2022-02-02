package com.thundermaps.apilib.android.impl.resources

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.requests.models.MapboxFeature
import com.thundermaps.apilib.android.api.resources.ShapeResource
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@KtorExperimentalAPI
@ExperimentalCoroutinesApi
class ShapeResourceImplTest {
    private val androidClient: AndroidClient = mock()
    private val resultHandler: ResultHandler = ResultHandler()
    private lateinit var shapeResource: ShapeResource

    @Before
    fun setUp() {
        shapeResource = ShapeResourceImpl(androidClient, resultHandler)
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(androidClient)
    }

    @Test
    fun getFeaturesSuccess() = runBlockingTest {
        var inspectCalled = false
        val content = "{\"feature\": \"Some data\"}"
        val client = TestHelpers.testClient(
            content = content,
            status = HttpStatusCode.OK,
            headers = responseHeaders,
            requestInspector = {
                assertEquals(path, it.url.encodedPath)
                assertEquals(HttpMethod.Get, it.method)
                inspectCalled = true
            }
        )

        whenever(androidClient.client(any())) doReturn Pair(client, HttpRequestBuilder())

        val result = shapeResource.getShape(mapboxFeature)

        verifyAndroidClient()
        assertTrue(result.isSuccess)
        assertTrue(inspectCalled)
        assertEquals(content, result.getNullableData())
    }

    @Test
    fun getFeaturesError() = runBlockingTest {
        var inspectCalled = false
        val content = ""
        val client = TestHelpers.testClient(
            content = content,
            status = HttpStatusCode.NotFound,
            headers = responseHeaders,
            requestInspector = {
                assertEquals(path, it.url.encodedPath)
                assertEquals(HttpMethod.Get, it.method)
                inspectCalled = true
            }
        )

        whenever(androidClient.client(any())) doReturn Pair(client, HttpRequestBuilder())

        val result = shapeResource.getShape(mapboxFeature)

        verifyAndroidClient()
        assertTrue(result.isError)
        assertTrue(inspectCalled)
        assertEquals(ShapeResourceImpl.ERROR_MESSAGE, (result as Result.Error).exception.message)
    }

    private fun verifyAndroidClient() {
        val parameterCaptor = argumentCaptor<RequestParameters>()
        verify(androidClient).client(parameterCaptor.capture())
        val requestParameters = parameterCaptor.firstValue
        assertEquals(1, requestParameters.api_version)
    }

    companion object {
        private val mapboxFeature = MapboxFeature("abc", "layers", "abd")
        private val responseHeaders =
            headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
        private val path =
            "datasets/v1/${mapboxFeature.mapboxUser}/${mapboxFeature.mapboxDatasetId}/features?access_token=${mapboxFeature.mapboxAccessToken}"
    }
}
