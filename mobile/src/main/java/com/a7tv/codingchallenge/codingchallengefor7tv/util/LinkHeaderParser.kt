package com.a7tv.codingchallenge.codingchallengefor7tv.util

import com.a7tv.codingchallenge.codingchallengefor7tv.model.GitHubPageId
import com.a7tv.codingchallenge.codingchallengefor7tv.model.GitHubUserId
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.http.HttpGetAnswer
import com.a7tv.codingchallenge.codingchallengefor7tv.util.typeclasses.Try
import java.util.regex.Pattern

/**
 * See https://tools.ietf.org/html/rfc5988#section-5 for comprehensive documentation of the link header spec.
 * This parser only recognizes the "next" link information specific for the Github API (https://developer.github.com/v3/guides/traversing-with-pagination/).
 */
class LinkHeaderParser {

    companion object {
        const val FINAL_ID = -1L // returned in case no "next" section is given but header is valid
        private const val LINK_HEADER_KEY = "Link"
        private const val LINK_HEADER_NEXT_REL = "rel=\"next\""

        private val valueSincePattern = Regex("\\?since=").toPattern()
        private val valuePagePattern = Regex("page=").toPattern()
        private val linkInfoRegex = Regex("(?<=rel=\").+?(?=\")")
        private val linkRegex = Regex("(?<=<).+?(?=>)")
        private val numbersRegex = Regex("\\d+")
    }

    fun getNextUserId(httpGetAnswer: HttpGetAnswer): Try<GitHubUserId> =
            verifyHeaderPresent(
                    httpGetAnswer,
                    { header -> parseIdFromString(header) },
                    { Try.just(GitHubUserId(FINAL_ID)) }
            )

    fun getNextPage(httpGetAnswer: HttpGetAnswer): Try<GitHubPageId> =
            verifyHeaderPresent(
                    httpGetAnswer,
                    { header -> parsePageFromString(header) },
                    { Try.just(GitHubPageId(FINAL_ID)) }
            )

    private fun <T> verifyHeaderPresent(httpGetAnswer: HttpGetAnswer,
                                        onHeaderPresent: (String) -> Try<T>,
                                        onNoHeaderPresent: () -> Try<T>
    ): Try<T> {
        val headerValues = httpGetAnswer.headers[LINK_HEADER_KEY]
        return when (headerValues) {
            null -> onNoHeaderPresent()
            else -> extractLinkInformation(headerValues, onHeaderPresent)
        }
    }

    private fun <T> extractLinkInformation(headerValues: List<String>,
                                           onHeaderPresent: (String) -> Try<T>
    ): Try<T> {
        return when (headerValues.size) {
            1 -> onHeaderPresent(headerValues[0])
            else -> Try.Failure(
                    LinkHeaderParseException(LinkHeaderParseException.WRONG_SIZE,
                            headerValues.joinToString()
                    )
            )
        }
    }

    private fun parseIdFromString(value: String) =
            parseFromString(value, valueSincePattern) { userId ->
                GitHubUserId(userId)
            }

    private fun parsePageFromString(value: String) =
            parseFromString(value, valuePagePattern) { pageNr ->
                GitHubPageId(pageNr)
            }

    private fun <T> parseFromString(value: String,
                                    valueDelimiter: Pattern,
                                    onValueParsed: (Long) -> T
    ): Try<T> {
        val links = value.splitToSequence(",").toList()
        return when (allListedLinksAreValid(links)) {
            true -> when (links.isEmpty()) {
                true -> Try.just(onValueParsed(FINAL_ID))
                else -> parseIdFromNextSection(links, valueDelimiter, onValueParsed)
            }
            false -> Try.Failure(
                    LinkHeaderParseException(LinkHeaderParseException.INVALID_HEADER_CONTENT, value)
            )
        }
    }

    private fun allListedLinksAreValid(links: List<String>) =
            links.all { link -> linkInfoRegex.containsMatchIn(link) }

    private fun <T> parseIdFromNextSection(nextSections: List<String>,
                                           valueDelimiter: Pattern,
                                           onValueParsed: (Long) -> T): Try<T> {
        val nextLinkInfo = nextSections.firstOrNull { it.contains(LINK_HEADER_NEXT_REL) }
        return when (nextLinkInfo) {
            null -> Try.just(onValueParsed(FINAL_ID)) // TODO no test coverage yet
            else -> extractRelNextLink(nextLinkInfo, valueDelimiter, onValueParsed)
        }
    }

    private fun <T> extractRelNextLink(nextLinkInfo: String,
                                       valueDelimiter: Pattern,
                                       onValueParsed: (Long) -> T): Try<T> {
        val relNextInformation = linkInfoRegex.find(nextLinkInfo)?.value
        return when (relNextInformation) {
            null -> Try.Failure(LinkHeaderParseException(
                    LinkHeaderParseException.INVALID_HEADER_CONTENT, nextLinkInfo) // TODO no test coverage yet
            )
            else -> extractRelNextId(nextLinkInfo, nextLinkInfo, valueDelimiter, onValueParsed)
        }
    }

    private fun <T> extractRelNextId(relNextInformation: String,
                                     nextLinkInfo: String,
                                     valueDelimiter: Pattern,
                                     onValueParsed: (Long) -> T
    ): Try<T> {
        val link = linkRegex.find(relNextInformation)?.value
        return when (link) {
            null -> Try.Failure(
                    LinkHeaderParseException(
                            LinkHeaderParseException.INVALID_HEADER_CONTENT, nextLinkInfo)) // TODO no test coverage yet
            else -> {
                val matcher = valueDelimiter.matcher(link)
                if (matcher.find()) {
                    val afterDelimiter = link.substring(matcher.end())
                    val id = numbersRegex.find(afterDelimiter)?.value
                    when (id) {
                        null -> Try.Failure(
                                LinkHeaderParseException(
                                        LinkHeaderParseException.INVALID_HEADER_CONTENT, nextLinkInfo)) // TODO no test coverage yet
                        else -> Try.just(onValueParsed(id.toLong()))
                    }
                } else {
                    Try.Failure(
                            LinkHeaderParseException(
                                    LinkHeaderParseException.INVALID_HEADER_CONTENT, nextLinkInfo)) // TODO no test coverage yet
                }
            }
        }
    }

}