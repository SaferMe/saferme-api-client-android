package com.thundermaps.apilib.android.api_impl

import com.thundermaps.apilib.android.api.SaferMeCredentials
import com.thundermaps.apilib.android.api_impl.resources.TestHelpers
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.util.AttributeKey
import io.ktor.util.Attributes
import io.ktor.util.KtorExperimentalAPI
import java.util.Random
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlin.collections.HashMap
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test

class AndroidClientTest {

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun defaultParams() {
        val params = AndroidClient().defaultParams()
        assertEquals(0, params.customRequestHeaders.size)
        assertNull(params.credentials)
        assertEquals("public-api.thundermaps.com", params.host)
        assertNull(params.port)
        assertEquals(4, params.api_version)
    }

    @io.ktor.util.KtorExperimentalAPI
    @Test fun clientChangesWithCredentials() {
        val subject = AndroidClient()
        val initialParams = subject.defaultParams().copy(credentials = SaferMeCredentials("key", "id", "app", null, ""))
        val subsequentParams = initialParams.copy(credentials = initialParams.credentials!!.copy(ApiKey = "Another Key"))

        val client1 = subject.client(initialParams).first

        // Calls to client with the same credentials should return the same object
        val client2 = subject.client(initialParams).first
        assertEquals(client1, client2)

        // Calls to client with different credentials should return a different object
        val client3 = subject.client(subsequentParams).first
        assertThat(client3, not(client2))
    }

    /**
     * Test that the right headers are added to a request template when calling client()
     */
    @KtorExperimentalAPI
    @Test fun clientTemplateHeaders() {
        val subject = AndroidClient()
        val testKey = "Test Key"
        val testInstall = "Test Install"
        val testApp = "Test App"
        val testTeam = "Test Team"
        val testClientUuid = "client uuid"

        val paramsWithTeam = subject.defaultParams().copy(credentials =
            SaferMeCredentials(testKey, testInstall, testApp, testTeam, testClientUuid))

        val paramsWithoutTeam = subject.defaultParams().copy(credentials =
        SaferMeCredentials(testKey, testInstall, testApp, null, testClientUuid))

        val builderWithTeam = AndroidClient().client(paramsWithTeam).second
        assertEquals(builderWithTeam.headers["Authorization"], "Token token=$testKey")
        assertEquals(builderWithTeam.headers["X-InstallationID"], testInstall)
        assertEquals(builderWithTeam.headers["X-AppID"], testApp)
        assertEquals(builderWithTeam.headers["X-TeamID"], testTeam)

        val builderWithoutTeam = AndroidClient().client(paramsWithoutTeam).second
        assertFalse(builderWithoutTeam.headers.contains("X-TeamID"))
    }

    /**
     * Test that custom headers are correctly applied
     */
    @KtorExperimentalAPI
    @Test fun clientTemplateCustomHeaders() {
        val testHeaders = HashMap<String, String>()
        val random = Random()

        // Nice little random string generator
        val nextString: () -> String = { TestHelpers.randomString((random.nextInt(20) + 1).toLong()) }

        for (i in 1..5) {
            testHeaders[nextString()] = nextString()
        }

        val subject = AndroidClient()
        val paramsWithCustomHeaders = subject.defaultParams().copy(customRequestHeaders = testHeaders)
        val builder = subject.client(paramsWithCustomHeaders).second

        for (entry in testHeaders.entries) {
            assertEquals(entry.value, builder.headers[entry.key])
        }
    }

    @io.ktor.util.KtorExperimentalAPI
    @Test fun clientFeatures() {
        val subject = AndroidClient()
        val client = subject.client(subject.defaultParams()).first

        // The key to the feature list is internal, so we are going to have to break the rules a
        // little and assume some implementation details...
        val topAttributes = client.attributes

        // Assumes features are the first element of the config attributes
        @Suppress("UNCHECKED_CAST") val featureKey = topAttributes.allKeys[0] as AttributeKey<Attributes>
        val featureAttributes = topAttributes[featureKey]

        // Json serializer installed
        assertTrue(featureAttributes.contains(JsonFeature.key))
        val featureVal = featureAttributes[JsonFeature.key]

        // It is correct type
        assertTrue(featureVal.serializer is GsonSerializer)
    }
}
