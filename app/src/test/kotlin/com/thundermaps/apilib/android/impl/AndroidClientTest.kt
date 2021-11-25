package com.thundermaps.apilib.android.impl

import com.thundermaps.apilib.android.api.SaferMeCredentials
import com.thundermaps.apilib.android.impl.resources.TestHelpers
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.util.AttributeKey
import io.ktor.util.Attributes
import io.ktor.util.KtorExperimentalAPI
import io.mockk.mockk
import java.util.Random
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

class AndroidClientTest {
    
    @Before
    fun setUp() {
    }
    
    @After
    fun tearDown() {
    }
    
    private fun AndroidClient.defaultParams() = SaferMeClientImpl(this, mockk()).defaultParams()
    
    @io.ktor.util.KtorExperimentalAPI
    @Test
    fun clientChangesWithCredentials() {
        val subject = AndroidClient()
        val initialParams = subject.defaultParams()
            .copy(credentials = SaferMeCredentials("key", "id", "app", null, ""))
        val subsequentParams =
            initialParams.copy(credentials = initialParams.credentials!!.copy(ApiKey = "Another Key"))
        
        val (client1, request1) = subject.client(initialParams)
        
        // Calls to client with the same credentials should return the same object
        val (client2, request2) = subject.client(initialParams)
        assertEquals(client1, client2)
        assertEquals(request1, request2)
        
        // Calls to client with different credentials should return a different object
        val (client3, request3) = subject.client(subsequentParams)
        assertEquals(client1, client3)
        assertNotEquals(request1, request3)
    }
    
    /**
     * Test that the right headers are added to a request template when calling client()
     */
    @KtorExperimentalAPI
    @Test
    fun clientTemplateHeaders() {
        val subject = AndroidClient()
        val testKey = "Test Key"
        val testInstall = "Test Install"
        val testApp = "Test App"
        val testTeam = "Test Team"
        val testClientUuid = "client uuid"
        
        val paramsWithTeam = subject.defaultParams().copy(
            credentials =
            SaferMeCredentials(testKey, testInstall, testApp, testTeam, testClientUuid)
        )
        
        val paramsWithoutTeam = subject.defaultParams().copy(
            credentials =
            SaferMeCredentials(testKey, testInstall, testApp, null, testClientUuid)
        )
        
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
    @Test
    fun clientTemplateCustomHeaders() {
        val testHeaders = HashMap<String, String>()
        val random = Random()
        
        // Nice little random string generator
        val nextString: () -> String =
            { TestHelpers.randomString((random.nextInt(20) + 1).toLong()) }
        
        for (i in 1..5) {
            testHeaders[nextString()] = nextString()
        }
        
        val subject = AndroidClient()
        val paramsWithCustomHeaders = subject.defaultParams().copy(
            customRequestHeaders = testHeaders,
            credentials = SaferMeCredentials(
                "testKey",
                "testInstall",
                "testApp",
                "testTeam",
                "testClientUuid"
            )
        )
        val builder = subject.client(paramsWithCustomHeaders).second
        
        for (entry in testHeaders.entries) {
            assertEquals(entry.value, builder.headers[entry.key])
        }
    }
    
    @io.ktor.util.KtorExperimentalAPI
    @Test
    fun clientFeatures() {
        val subject = AndroidClient()
        val client = subject.client(subject.defaultParams()).first
        
        // The key to the feature list is internal, so we are going to have to break the rules a
        // little and assume some implementation details...
        val topAttributes = client.attributes
        
        // Assumes features are the first element of the config attributes
        @Suppress("UNCHECKED_CAST") val featureKey =
            topAttributes.allKeys[0] as AttributeKey<Attributes>
        val featureAttributes = topAttributes[featureKey]
        
        // Json serializer installed
        assertTrue(featureAttributes.contains(JsonFeature.key))
        val featureVal = featureAttributes[JsonFeature.key]
        
        // It is correct type
        assertTrue(featureVal.serializer is GsonSerializer)
    }
}
