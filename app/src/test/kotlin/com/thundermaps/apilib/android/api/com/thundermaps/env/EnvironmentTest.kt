package com.thundermaps.apilib.android.api.com.thundermaps.env

import org.junit.Assert.assertEquals
import org.junit.Test

class EnvironmentTest {
    @Test
    fun `verify Live environment`() {
        val live: Environment = Live

        assertEquals(4, live.servers.size)
        live.servers.forEachIndexed { index, value ->
            assertEquals("api${index + 1}.prod.saferme.io", value)
        }
    }

    @Test
    fun `verify Staging environment`() {
        val staging: Environment = Staging
        assertEquals(2, staging.servers.size)
        assertEquals("api.staging.saferme.io", staging.servers.firstOrNull())
        assertEquals("api1.staging.saferme.io", staging.servers.lastOrNull())
    }
}
