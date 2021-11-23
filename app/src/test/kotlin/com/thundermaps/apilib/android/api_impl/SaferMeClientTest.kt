package com.thundermaps.apilib.android.api_impl

import junit.framework.TestCase
import org.junit.Test

internal class SaferMeClientTest {
    private val androidClient = AndroidClient()
    private val saferMeClient = SaferMeClientImpl(androidClient)

    @Test
    fun defaultParams() {
        val params = saferMeClient.defaultParams()
        TestCase.assertEquals(0, params.customRequestHeaders.size)
        TestCase.assertNull(params.credentials)
        TestCase.assertEquals("public-api.thundermaps.com", params.host)
        TestCase.assertNull(params.port)
        TestCase.assertEquals(4, params.api_version)
    }
}
