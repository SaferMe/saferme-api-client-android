package com.thundermaps.apilib.android.impl

import com.thundermaps.apilib.android.api.SaferMeCredentials
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.impl.resources.TestHelpers
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.HttpHeaders
import io.ktor.util.AttributeKey
import io.ktor.util.Attributes
import io.ktor.util.KtorExperimentalAPI
import java.util.Random
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

@KtorExperimentalAPI
class AndroidClientTest {
    private lateinit var androidClient: AndroidClient
    @Before
    fun setUp() {
        androidClient = AndroidClient()
    }
    @Test
    fun clientChangesWithCredentials() {
        val initialParams = defaultParameters.copy(
            credentials = saferMeCredentials
        )
        val subsequentParams = initialParams.copy(
            credentials = saferMeCredentials.copy(ApiKey = "Another Key")
        )

        val (client1, request1) = androidClient.client(initialParams)

        // Calls to client with the same credentials should return the same object
        val (client2, request2) = androidClient.client(initialParams)
        assertEquals(client1, client2)
        assertEquals(request1, request2)

        // Calls to client with different credentials should return a different object
        val (client3, request3) = androidClient.client(subsequentParams)
        assertEquals(client1, client3)
        assertNotEquals(request1, request3)
    }

    /**
     * Test that the right headers are added to a request template when calling client()
     */
    @KtorExperimentalAPI
    @Test
    fun clientTemplateHeaders() {
        val paramsWithTeam = defaultParameters.copy(
            credentials = saferMeCredentials
        )

        val paramsWithoutTeam = defaultParameters.copy(
            credentials = saferMeCredentials.copy(TeamId = null)
        )

        val builderWithTeam = androidClient.client(paramsWithTeam).second
        builderWithTeam.verifyRequestBuilderWithCredentialHavingTeam(saferMeCredentials)
        builderWithTeam.verifyAcceptType()

        val builderWithoutTeam = androidClient.client(paramsWithoutTeam).second
        builderWithoutTeam.verifyAcceptType()
        assertFalse(builderWithoutTeam.headers.contains("X-TeamID"))
    }

    private fun HttpRequestBuilder.verifyAcceptType() {
        assertEquals(headers["Accept"], "application/json, text/plain, */*")
    }

    private fun HttpRequestBuilder.verifyRequestBuilderWithCredentialHavingTeam(
        saferMeCredentials: SaferMeCredentials
    ) {
        assertEquals(headers[HttpHeaders.Authorization], "Token token=${saferMeCredentials.ApiKey}")
        assertEquals(headers[HeaderType.xInstallationId], saferMeCredentials.InstallationId)
        assertEquals(headers[HeaderType.xAppId], saferMeCredentials.AppId)
        assertEquals(headers[HeaderType.xTeamId], saferMeCredentials.TeamId)
    }

    /**
     * Test that custom headers are correctly applied
     */
    @Test
    fun clientTemplateCustomHeadersWithCredential() {
        val testHeaders = HashMap<String, String>()
        val random = Random()

        // Nice little random string generator
        val nextString: () -> String =
            { TestHelpers.randomString((random.nextInt(20) + 1).toLong()) }

        for (i in 1..5) {
            testHeaders[nextString()] = nextString()
        }

        val paramsWithCustomHeaders = defaultParameters.copy(
            customRequestHeaders = testHeaders,
            credentials = saferMeCredentials
        )
        val builder = androidClient.client(paramsWithCustomHeaders).second
        builder.verifyRequestBuilderWithCredentialHavingTeam(
            saferMeCredentials
        )
        builder.verifyAcceptType()
        for (entry in testHeaders.entries) {
            assertEquals(entry.value, builder.headers[entry.key])
        }
    }

    @Test
    fun clientTemplateCustomHeadersWithNullCredential() {
        val testHeaders = HashMap<String, String>()
        val random = Random()

        // Nice little random string generator
        val nextString: () -> String =
            { TestHelpers.randomString((random.nextInt(20) + 1).toLong()) }

        for (i in 1..5) {
            testHeaders[nextString()] = nextString()
        }

        val paramsWithCustomHeaders = defaultParameters.copy(
            customRequestHeaders = testHeaders
        )
        val builder = androidClient.client(paramsWithCustomHeaders).second

        for (entry in testHeaders.entries) {
            assertEquals(entry.value, builder.headers[entry.key])
        }
    }

    @Test
    fun clientFeatures() {
        val client = androidClient.client(defaultParameters).first

        // The key to the feature list is internal, so we are going to have to break the rules a
        // little and assume some implementation details...
        val topAttributes = client.attributes

        // Assumes features are the first element of the config attributes
        @Suppress("UNCHECKED_CAST")
        val featureKey = topAttributes.allKeys[0] as AttributeKey<Attributes>
        val featureAttributes = topAttributes[featureKey]

        // Json serializer installed
        assertTrue(featureAttributes.contains(JsonFeature.key))
        val featureVal = featureAttributes[JsonFeature.key]

        // It is correct type
        assertTrue(featureVal.serializer is GsonSerializer)
    }

    companion object {
        private const val DEFAULT_API_ENDPOINT = "public-api.thundermaps.com"

        private const val TEST_KEY = "Test Key"
        private const val TEST_INSTALL = "Test Install"
        private const val TEST_APP = "Test App"
        private const val TEST_TEAM = "Test Team"
        private const val TEST_CLIENT_UUID = "client uuid"
        private val saferMeCredentials = SaferMeCredentials(TEST_KEY, TEST_INSTALL, TEST_APP, TEST_TEAM, TEST_CLIENT_UUID)
        private val defaultParameters = RequestParameters(
            customRequestHeaders = HashMap(),
            credentials = null,
            host = DEFAULT_API_ENDPOINT,
            port = null,
            api_version = 4
        )
    }
}
