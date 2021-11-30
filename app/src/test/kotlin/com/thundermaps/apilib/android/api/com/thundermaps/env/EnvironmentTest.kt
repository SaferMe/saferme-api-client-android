package com.thundermaps.apilib.android.api.com.thundermaps.env

import org.junit.Assert
import org.junit.Test

class EnvironmentTest {
    @Test
    fun `verify Live environment`() {
        val live: Environment = Live

        Assert.assertEquals(4, live.servers.size)
        live.servers.forEachIndexed { index, value ->
            Assert.assertEquals("api${index + 1}.thundermaps.com", value)
        }
    }

    @Test
    fun `verify Staging environment`() {
        val staging: Environment = Staging
        Assert.assertEquals(1, staging.servers.size)
        Assert.assertEquals("api.staging.saferme.io", staging.servers.firstOrNull())
    }
}
