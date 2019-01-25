package com.a7tv.codingchallenge.codingchallengefor7tv.util.typeclasses

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class TryTest {

    @Test
    fun `test map on success`() {
        val startValue = 5
        val beforeMap = Try.just(startValue)

        val mappingFunction = { i: Int -> i * 2 }
        val afterMap = beforeMap.map(mappingFunction)

        assertTrue(afterMap is Try.Success)
        afterMap as Try.Success
        assertEquals(mappingFunction(startValue), afterMap.value)
    }

    @Test
    fun `test map on failure`() {
        val expectedError = IllegalArgumentException("Some Error")
        val beforeMap = Try.Failure(expectedError)

        val mappingFunction = { i: Int -> i * 2 }
        val afterMap = beforeMap.map(mappingFunction)

        assertTrue(afterMap is Try.Failure)
        afterMap as Try.Failure
        assertEquals(expectedError, afterMap.error)
    }

}