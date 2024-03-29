package com.thundermaps.apilib.android.api.responses.models

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.thundermaps.apilib.android.api.fromJsonString
import com.thundermaps.apilib.android.api.readJsonFile
import com.thundermaps.apilib.android.impl.AndroidClient.Companion.gsonSerializer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ParseReportsTest {
    private val appContext: Context by lazy { InstrumentationRegistry.getInstrumentation().targetContext }

    @Test
    fun parserReports() {
        val reports =
            gsonSerializer.fromJsonString<List<Report>>(appContext.readJsonFile("reports.json"))

        assertNotNull(reports)
        assertEquals(10, reports.size)
    }
}
