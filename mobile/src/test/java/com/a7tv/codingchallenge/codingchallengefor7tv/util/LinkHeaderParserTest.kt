package com.a7tv.codingchallenge.codingchallengefor7tv.util

import com.a7tv.codingchallenge.codingchallengefor7tv.repo.http.HttpGetAnswer
import com.a7tv.codingchallenge.codingchallengefor7tv.util.typeclasses.Try
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class LinkHeaderParserTest {

    @Test
    fun `valid user id header gets correctly parsed`() {
        val parser = LinkHeaderParser()
        val expectedNextId = 46L
        val apiAnswer = HttpGetAnswer(
                mapOf("Link" to listOf("<https://api.github.com/users?since=$expectedNextId>; rel=\"next\", <https://api.github.com/users{?since}>; rel=\"first\"")),
                "Foo Json"
        )
        val parsedId = parser.getNextUserId(apiAnswer)
        assertTrue(parsedId is Try.Success)
        parsedId as Try.Success
        assertEquals(expectedNextId, parsedId.value.value)
    }

    @Test
    fun `valid page id head gets correctly parsed`() {
        val parser = LinkHeaderParser()
        val expectedNextPageId = 3L
        val apiAnswer = HttpGetAnswer(
                mapOf("Link" to listOf("<https://api.github.com/search/users?q=mat&page=1>; rel=\"prev\", <https://api.github.com/search/users?q=mat&page=$expectedNextPageId>; rel=\"next\", <https://api.github.com/search/users?q=mat&page=34>; rel=\"last\", <https://api.github.com/search/users?q=mat&page=1>; rel=\"first\"")),
                "Foo Json"
        )
        val parsedId = parser.getNextPage(apiAnswer)
        assertTrue(parsedId is Try.Success)
        parsedId as Try.Success
        assertEquals(expectedNextPageId, parsedId.value.number)
    }

    @Test
    fun `final id reported on no header present`() {
        val parser = LinkHeaderParser()
        val apiAnswer = HttpGetAnswer(emptyMap(), "Foo Json")
        val parsedId = parser.getNextUserId(apiAnswer)

        assertTrue(parsedId is Try.Success)
        parsedId as Try.Success
        assertEquals(LinkHeaderParser.FINAL_ID, parsedId.value.value)
    }

    @Test
    fun `parser not crashing on multiple link keys present`() {
        val parser = LinkHeaderParser()
        val multipleLinkKeys = listOf("Value1", "Value2")
        val apiAnswer = HttpGetAnswer(
                mapOf("Link" to multipleLinkKeys), "Foo Json")
        val parsedId = parser.getNextUserId(apiAnswer)

        val expectedException = LinkHeaderParseException(LinkHeaderParseException.WRONG_SIZE,
                multipleLinkKeys.joinToString())
        assertTrue(parsedId is Try.Failure)
        parsedId as Try.Failure
        assertEquals(expectedException, parsedId.error)
    }

    @Test
    fun `parser not crashing on no parseable input`() {
        val parser = LinkHeaderParser()
        val invalidKeyValue = listOf("No proper content")
        val apiAnswer = HttpGetAnswer(
                mapOf("Link" to invalidKeyValue), "Foo Json")
        val parsedId = parser.getNextUserId(apiAnswer)

        val expectedException = LinkHeaderParseException(LinkHeaderParseException.INVALID_HEADER_CONTENT,
                invalidKeyValue.joinToString())
        assertTrue(parsedId is Try.Failure)
        parsedId as Try.Failure
        assertEquals(expectedException, parsedId.error)
    }

    @Test
    fun `parser not crashing on no invalid id`() {
        val parser = LinkHeaderParser()
        val invalidKeyValue = listOf("\"<https://api.github.com/users?since=NOT_A_NUMBER>; rel=\\\"next\\\"")
        val apiAnswer = HttpGetAnswer(
                mapOf("Link" to invalidKeyValue), "Foo Json")
        val parsedId = parser.getNextUserId(apiAnswer)

        val expectedException = LinkHeaderParseException(LinkHeaderParseException.INVALID_HEADER_CONTENT,
                invalidKeyValue.joinToString())
        assertTrue(parsedId is Try.Failure)
        parsedId as Try.Failure
        assertEquals(expectedException, parsedId.error)
    }

}