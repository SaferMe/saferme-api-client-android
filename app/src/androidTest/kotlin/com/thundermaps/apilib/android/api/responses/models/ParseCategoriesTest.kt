package com.thundermaps.apilib.android.api.responses.models

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.thundermaps.apilib.android.api.fromJsonString
import com.thundermaps.apilib.android.api.readJsonFile
import com.thundermaps.apilib.android.impl.AndroidClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ParseCategoriesTest {
    private val appContext: Context by lazy { InstrumentationRegistry.getInstrumentation().targetContext }

    @Test
    fun parserCategories() {
        val categories =
            AndroidClient.gsonSerializer.fromJsonString<List<Category>>(appContext.readJsonFile("categories.json"))

        assertNotNull(categories)
        assertEquals(10, categories.size)
        assertNull(categories.first().parentId)
    }
}
