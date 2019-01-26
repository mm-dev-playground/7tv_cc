package com.a7tv.codingchallenge.codingchallengefor7tv.util

import com.a7tv.codingchallenge.codingchallengefor7tv.model.GitHubUserId
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.http.HttpGetAnswer
import com.a7tv.codingchallenge.codingchallengefor7tv.util.typeclasses.Try

class LinkHeaderParser {

    private companion object {
        const val LINK_HEADER_KEY = "Link"
        const val LINK_HEADER_VALUE_ID_DELIMITER = ">;"
        const val LINK_HEADER_VALUE_SINCE_INDICATOR = "?since="
    }

    fun getNextId(httpGetAnswer: HttpGetAnswer): Try<GitHubUserId> {
        val headerValues = httpGetAnswer.headers[LINK_HEADER_KEY]
        return when (headerValues) {
            null -> Try.Failure(LinkHeaderParseException(LinkHeaderParseException.KEY_NOT_PRESENT, null))
            else -> {
                when (headerValues.size) {
                    1 -> parseIdFromString(headerValues[0])
                    else -> Try.Failure(
                            LinkHeaderParseException(LinkHeaderParseException.WRONG_SIZE,
                                    headerValues.joinToString())
                    )
                }
            }
        }
    }

    private fun parseIdFromString(value: String) =
            try {
                val substringUntilSemiColon = value.substring(
                        0, value.indexOf(LINK_HEADER_VALUE_ID_DELIMITER)
                )
                val idString = substringUntilSemiColon.substring(
                        substringUntilSemiColon.indexOf(LINK_HEADER_VALUE_SINCE_INDICATOR)
                                + LINK_HEADER_VALUE_SINCE_INDICATOR.length
                )
                Try.just(GitHubUserId(idString.toLong()))
            } catch (e: Exception) {
                when (e) {
                    is StringIndexOutOfBoundsException, is NumberFormatException -> Try.Failure(
                            LinkHeaderParseException(LinkHeaderParseException.NO_ID_FOUND, value)
                    )
                    else -> Try.Failure(e)
                }
            }

}