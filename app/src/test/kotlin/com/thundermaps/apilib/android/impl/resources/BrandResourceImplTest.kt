package com.thundermaps.apilib.android.impl.resources

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
import com.thundermaps.apilib.android.api.resources.BrandResource
import com.thundermaps.apilib.android.api.responses.models.Brand
import com.thundermaps.apilib.android.api.responses.models.Layer
import com.thundermaps.apilib.android.api.responses.models.LayerSource
import com.thundermaps.apilib.android.api.responses.models.PasswordRequirements
import com.thundermaps.apilib.android.api.responses.models.StrengthLevels
import com.thundermaps.apilib.android.impl.AndroidClient
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.fullPath
import io.ktor.http.headersOf
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@KtorExperimentalAPI
@ExperimentalCoroutinesApi
class BrandResourceImplTest {
    private val androidClient: AndroidClient = mock()
    private val environmentManager: EnvironmentManager = mock {
        on { environment } doReturn Staging
    }

    private lateinit var brandResource: BrandResource

    @Before
    fun setUp() {
        brandResource = BrandResourceImpl(androidClient, environmentManager)
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(androidClient, environmentManager)
    }

    @Test
    fun verifyGetBrandSuccess() = runTest {
        var inspectCalled = false
        val client = TestHelpers.testClient(
            content = BRAND_RESPONSE,
            status = HttpStatusCode.OK,
            headers = responseHeaders,
            requestInspector = {
                assertEquals(
                    PATH,
                    it.url.fullPath
                )
                assertEquals(HttpMethod.Get, it.method)
                inspectCalled = true
            }
        )

        whenever(androidClient.build(any(), any(), any())) doReturn Pair(
            client,
            AndroidClient.getRequestBuilder(
                (brandResource as BrandResourceImpl).createParameters(APPLICATION_ID), BrandResourceImpl.PATH, HttpMethod.Get
            )
        )

        val brandResult = brandResource.getBrand(APPLICATION_ID)

        verifyAndroidClient()
        verify(environmentManager).environment
        assertTrue(brandResult.isSuccess)
        val brandValue = brandResult.getNullableData()

        assertEquals(brand, brandValue)
        assertTrue(inspectCalled)
    }

    private fun verifyAndroidClient() {
        val parameterCaptor = argumentCaptor<RequestParameters>()
        verify(androidClient).build(parameterCaptor.capture(), any(), any())
        val requestParameters = parameterCaptor.firstValue
        assertEquals(4, requestParameters.api_version)
        assertNull(requestParameters.credentials)
        assertEquals(Staging.servers.first(), requestParameters.host)
        assertEquals(
            APPLICATION_ID,
            requestParameters.customRequestHeaders["X-AppID"]
        )
    }

    companion object {
        private const val APPLICATION_ID = "com.thundermaps.saferme"
        private val PATH = "/api/v4/branded_app?fields=${BrandResourceImpl.BRAND_FIELDS.joinToString("%2C")}"
        private val responseHeaders =
            headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
        private val strengthLevels = StrengthLevels(100, 10000, 1000000)
        private val passwordRequirements = PasswordRequirements(8, strengthLevels)
        private val layerSource = LayerSource(
            "raster",
            listOf("https://tiles-a.data-cdn.linz.govt.nz/services;key=678ce305be144c0d810e69a8c26a163e/tiles/v4/set=4702/EPSG:3857/{z}/{x}/{y}.png")
        )
        private val layer = Layer(
            "linz-waikato",
            "raster",
            layerSource,
            10f,
            24f,
            "mapbox-mapbox-satellite",
            "satellite"
        )
        private val brand = Brand(
            appId = "com.thundermaps.saferme",
            appUrl = "https://play.google.com/store/apps/details?id=com.thundermaps.saferme",
            defaultZoom = 15f,
            defaultLocation = "POINT (174.7772 -41.2889)",
            disableSignUp = true,
            enableLocationWarning = true,
            latestVersion = "2.10.3",
            layerTitleJson = listOf(layer),
            name = "SaferMe",
            isOneTapReporting = true,
            passwordRequirements = passwordRequirements,
            isSafetyApp = true
        )
        private val BRAND_RESPONSE = """
            {
              "android_url": "https://play.google.com/store/apps/details?id=com.thundermaps.saferme",
              "bundle_id": "com.thundermaps.saferme",
              "custom_user_fields": [],
              "default_location": "POINT (174.7772 -41.2889)",
              "default_zoom": 15,
              "disable_sign_up": true,
              "enable_location_warning": true,
              "ios_url": "https://itunes.apple.com/nz/app/saferme/id1149135115?mt=8",
              "latest_version": "2.10.3",
              "layer_tilejson": [
                {
                  "id": "linz-waikato",
                  "type": "raster",
                  "source": {
                    "type": "raster",
                    "tiles": [
                      "https://tiles-a.data-cdn.linz.govt.nz/services;key=678ce305be144c0d810e69a8c26a163e/tiles/v4/set=4702/EPSG:3857/{z}/{x}/{y}.png"
                    ]
                  },
                  "minzoom": 10,
                  "maxzoom": 24,
                  "based_layer_id": "mapbox-mapbox-satellite",
                  "style_name": "satellite"
                }
              ],
              "name": "SaferMe",
              "oauth_providers": [],
              "one_tap_reporting": true,
              "password_requirements": {
                "minimum_length": 8,
                "strength_levels": { "weak": 100, "medium": 10000, "strong": 1000000 }
              },
              "required_password_entropy": 50,
              "safety_app": true
            }
        """.trimIndent()
    }
}
