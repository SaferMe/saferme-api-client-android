package com.thundermaps.apilib.android.api.responses.models

import org.junit.Assert
import org.junit.Test

class ResultTest {

    @Test
    fun `test Initial state before and after converting`() {
        val initialState = Result.Initial
        initialState.verify()

        val convertedState = initialState.convert {} as Result.Initial
        convertedState.verify()
    }

    @Test
    fun `test Success state`() {
        val successState = Result.Success(5)
        successState.verify()
        Assert.assertEquals(5, successState.getNullableData())

        val convertedState = successState.convert { listOf(it) } as Result.Success<List<Int>>
        convertedState.verify()
        Assert.assertEquals(listOf(5), convertedState.getNullableData())
    }

    @Test
    fun `test Loading state has data`() {
        val loadingState = Result.Loading(5)
        loadingState.verify()
        Assert.assertEquals(5, loadingState.getNullableData())

        val convertedState = loadingState.convert { listOf(it) } as Result.Loading<List<Int>>
        convertedState.verify()
        Assert.assertEquals(listOf(5), convertedState.getNullableData())
    }

    @Test
    fun `test Loading state has no data`() {
        val loadingState = Result.Loading(null)
        loadingState.verify()
        Assert.assertNull(loadingState.getNullableData())

        val convertedState = loadingState.convert { } as Result.Loading<*>
        convertedState.verify()
        Assert.assertNull(convertedState.getNullableData())
    }

    @Test
    fun `test Error state has data`() {
        val exception = Exception("Error")
        val errorState = Result.Error(5, exception)
        errorState.verify()
        Assert.assertEquals(5, errorState.getNullableData())

        val convertedState = errorState.convert { listOf(it) } as Result.Error<List<Int>>
        convertedState.verify()
        Assert.assertEquals(listOf(5), convertedState.getNullableData())
    }

    @Test
    fun `test Error state has no data`() {
        val exception = Exception("Error")
        val errorState = Result.Error(null, exception)
        errorState.verify()
        Assert.assertNull(errorState.getNullableData())

        val convertedState = errorState.convert { } as Result.Error<*>
        convertedState.verify()
        Assert.assertNull(convertedState.getNullableData())
    }

    private fun <T : Any> Result<T>.verify() {
        when (this) {
            is Result.Initial -> {
                Assert.assertFalse(isSuccess)
                Assert.assertFalse(isLoading)
                Assert.assertFalse(isError)
            }
            is Error -> {
                Assert.assertFalse(isSuccess)
                Assert.assertFalse(isLoading)
                Assert.assertTrue(isError)
            }
            is Result.Success -> {
                Assert.assertTrue(isSuccess)
                Assert.assertFalse(isLoading)
                Assert.assertFalse(isError)
            }
            is Result.Loading -> {
                Assert.assertFalse(isSuccess)
                Assert.assertTrue(isLoading)
                Assert.assertFalse(isError)
            }
            else -> {
            }
        }
    }
}
