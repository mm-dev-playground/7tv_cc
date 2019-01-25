package com.a7tv.codingchallenge.codingchallengefor7tv.util

import com.a7tv.codingchallenge.codingchallengefor7tv.repo.http.HttpGetAnswer
import com.a7tv.codingchallenge.codingchallengefor7tv.util.typeclasses.Try

class LinkHeaderParser {

    private companion object {
        const val LINK_HEADER_KEY = "Link"
    }

    fun getNextId(httpGetAnswer: HttpGetAnswer): Try<Int> {
        val headerValues = httpGetAnswer.headers[LINK_HEADER_KEY]
        return when (headerValues) {
            null -> Try.Failure(LinkHeaderParseException(LinkHeaderParseException.KEY_NOT_PRESENT, null))
            else -> {
                when (headerValues.size) {
                    1 -> parseIdFromString(headerValues[0])
                    else -> Try.Failure(
                            LinkHeaderParseException(LinkHeaderParseException.Reason.WRONG_SIZE,
                                    headerValues.joinToString())
                    )
                }
            }
        }
    }

    private fun parseIdFromString(value: String): Try<Int> {
        return try {

            val substringUntilSemiColon = value.substring(0, value.indexOf(">;"))
            val idString = substringUntilSemiColon.substring(
                    substringUntilSemiColon.indexOf("?since=") + "?since=".length
            )
            Try.just(idString.toInt())
        } catch (e: Exception) {
            when (e) {
                is StringIndexOutOfBoundsException, is NumberFormatException -> Try.Failure(
                        LinkHeaderParseException(LinkHeaderParseException.Reason.NO_ID_FOUND, value)
                )
                else -> Try.Failure(e)
            }
        }
    }

}