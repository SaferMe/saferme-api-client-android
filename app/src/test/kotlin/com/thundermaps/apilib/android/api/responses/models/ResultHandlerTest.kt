package com.thundermaps.apilib.android.api.responses.models

import android.content.res.Resources
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ResultHandlerTest {
    private val resultHandler: ResultHandler = ResultHandler()

    @Test
    fun `handle success with an integer value`() {
        val state = resultHandler.handleSuccess(5)
        assertTrue(state.isSuccess)
        assertEquals(5, state.getNullableData())
    }

    @Test
    fun `handle an exception`() {
        val exception = Resources.NotFoundException()
        val state = resultHandler.handleException<Int>(exception)
        assertTrue(state.isError)
        assertEquals(exception, (state as Result.Error).exception)
    }
}
