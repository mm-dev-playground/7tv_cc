package com.a7tv.codingchallenge.codingchallengefor7tv.util.typeclasses

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class OptionTest {

    @Test
    fun `test map on some`() {
        val startValue = 5
        val beforeMap = Option.just(startValue)

        val mappingFunction = { i: Int -> i * 2 }
        val afterMap = beforeMap.map(mappingFunction)

        assertTrue(afterMap is Option.Some)
        afterMap as Option.Some
        assertEquals(mappingFunction(startValue), afterMap.value)
    }

    @Test
    fun `test map on None`() {
        val beforeMap = Option.None

        val mappingFunction = { i: Int -> i * 2 }
        val afterMap = beforeMap.map(mappingFunction)

        assertTrue(afterMap is Option.None)
    }

}