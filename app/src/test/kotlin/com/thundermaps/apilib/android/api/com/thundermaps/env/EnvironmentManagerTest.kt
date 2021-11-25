package com.thundermaps.apilib.android.api.com.thundermaps.env

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class EnvironmentManagerTest {
    @Test
    fun verifyStagingBuildDefaultEnvironment() {
        val environmentManager = EnvironmentManager()
        assertEquals(Staging, environmentManager.environment)
        assertTrue(environmentManager.isStaging())
    }

    @Test
    fun verifyUpdateToLiveEnvironment() {
        val environmentManager = EnvironmentManager()
        assertEquals(Staging, environmentManager.environment)
        assertTrue(environmentManager.isStaging())

        environmentManager.updateEnvironment(Live)
        assertEquals(Live, environmentManager.environment)
        assertFalse(environmentManager.isStaging())
    }

    @Test
    fun verifyUpdateToLiveThenToStagingEnvironment() {
        val environmentManager = EnvironmentManager()
        assertEquals(Staging, environmentManager.environment)
        assertTrue(environmentManager.isStaging())

        environmentManager.updateEnvironment(Live)
        assertEquals(Live, environmentManager.environment)
        assertFalse(environmentManager.isStaging())

        environmentManager.updateEnvironment(Staging)
        assertEquals(Staging, environmentManager.environment)
        assertTrue(environmentManager.isStaging())
    }
}
