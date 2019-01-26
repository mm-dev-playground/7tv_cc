package com.a7tv.codingchallenge.codingchallengefor7tv.util

import com.a7tv.codingchallenge.codingchallengefor7tv.model.GitHubPageId
import com.a7tv.codingchallenge.codingchallengefor7tv.model.GitHubUserId
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.http.HttpGetAnswer
import com.a7tv.codingchallenge.codingchallengefor7tv.util.typeclasses.Try

class LinkHeaderParser {

    companion object {
        const val FINAL_ID = -1L // returned in case no "next" section is given but header is valid
        private const val LINK_HEADER_KEY = "Link"
        private const val LINK_HEADER_VALUE_LINK_DELIMITER = ">;"
        private const val LINK_HEADER_VALUE_SINCE_INDICATOR = "?since="
        private const val LINK_HEADER_VALUE_PAGE_INDICATOR = "&page="
    }

    private fun <T> validateHeader(httpGetAnswer: HttpGetAnswer,
                                   onValidHeader: (String) -> Try<T>): Try<T> {
        val headerValues = httpGetAnswer.headers[LINK_HEADER_KEY]
        return when (headerValues) {
            null -> Try.Failure(LinkHeaderParseException(LinkHeaderParseException.KEY_NOT_PRESENT, null))
            else -> {
                when (headerValues.size) {
                    1 -> onValidHeader(headerValues[0])
                    else -> Try.Failure(
                            LinkHeaderParseException(LinkHeaderParseException.WRONG_SIZE,
                                    headerValues.joinToString())
                    )
                }
            }
        }
    }

    fun getNextUserId(httpGetAnswer: HttpGetAnswer): Try<GitHubUserId> =
            validateHeader(httpGetAnswer) {
                parseIdFromString(it)
            }

    fun getNextPage(httpGetAnswer: HttpGetAnswer): Try<GitHubPageId> =
            validateHeader(httpGetAnswer) {
                parsePageFromString(it)
            }

    private fun parseIdFromString(value: String) =
            parseFromString(value, LINK_HEADER_VALUE_SINCE_INDICATOR) { userId ->
                GitHubUserId(userId)
            }

    private fun parsePageFromString(value: String) =
            parseFromString(value, LINK_HEADER_VALUE_PAGE_INDICATOR) { pageNr ->
                GitHubPageId(pageNr)
            }

    private fun <T> parseFromString(value: String, valueDelimiter: String, onValueParsed: (Long) -> T): Try<T> {
        return try {
            // get rel="next" section
            val nextSections = value.splitToSequence(",")
                    .filter { it.contains("rel=\"next\"") }
                    .toList()
            when (nextSections.isEmpty()) {
                true -> Try.just(onValueParsed(FINAL_ID))
                else -> parseIdFromNextSection(nextSections, value, valueDelimiter, onValueParsed)
            }
        } catch (e: Exception) {
            when (e) {
                is StringIndexOutOfBoundsException, is NumberFormatException -> Try.Failure(
                        LinkHeaderParseException(LinkHeaderParseException.NO_ID_FOUND, value)
                )
                else -> Try.Failure(e)
            }
        }
    }

    private fun <T> parseIdFromNextSection(nextSections: List<String>, value: String,
                                           valueDelimiter: String, onValueParsed: (Long) -> T): Try<T> {
        val nextSection = nextSections.first()
        val substringUntilSemiColon = nextSection.substring(
                0, nextSection.indexOf(LINK_HEADER_VALUE_LINK_DELIMITER)
        )
        val idString = substringUntilSemiColon.substring(
                substringUntilSemiColon.indexOf(valueDelimiter) + valueDelimiter.length
        )
        return Try.just(onValueParsed(idString.toLong()))
    }

}