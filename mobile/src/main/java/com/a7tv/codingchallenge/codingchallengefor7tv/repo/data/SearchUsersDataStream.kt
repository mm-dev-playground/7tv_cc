package com.a7tv.codingchallenge.codingchallengefor7tv.repo.data

import android.annotation.SuppressLint
import com.a7tv.codingchallenge.codingchallengefor7tv.model.GitHubPageId
import com.a7tv.codingchallenge.codingchallengefor7tv.model.GitHubSearchResult
import com.a7tv.codingchallenge.codingchallengefor7tv.model.GitHubUser
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.http.HttpClientInterface
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.http.HttpGetAnswer
import com.a7tv.codingchallenge.codingchallengefor7tv.util.LinkHeaderParser
import com.a7tv.codingchallenge.codingchallengefor7tv.util.typeclasses.Try
import io.reactivex.Scheduler
import java.net.URL

class SearchUsersDataStream(
        private val client: HttpClientInterface,
        private val scheduler: Scheduler,
        override val onFailure: (Throwable) -> Unit,
        override val onException: (Throwable) -> Unit
) : DataStream<Pair<List<GitHubUser>, GitHubPageId>, URL> {

    private val linkHeaderParser = LinkHeaderParser()

    @SuppressLint("CheckResult")
    // single subscription return value can be neglected, see http://reactivex.io/documentation/single.html
    override fun execute(input: URL, onSuccess: (Pair<List<GitHubUser>, GitHubPageId>) -> Unit) {
        client.getJsonFrom(input)
                .map { answerTry ->
                    answerTry.flatMap { answer ->
                        parseResultFromAnswer(answer)
                    }
                }
                .map { resultTry ->
                    resultTry.map { (searchResult, nextPageId) ->
                        searchResult.items to nextPageId
                    }
                }
                .subscribeOn(scheduler)
                .subscribe(
                        { result ->
                            when (result) {
                                is Try.Success -> onSuccess(result.value)
                                is Try.Failure -> onFailure(result.error)
                            }
                        },
                        { e -> onException(e) }
                )
    }

    private fun parseResultFromAnswer(answer: HttpGetAnswer): Try<Pair<GitHubSearchResult, GitHubPageId>> {
        val parsingResult = GitHubSearchResult.jsonAdapter.fromJson(answer.jsonString)
        return when (parsingResult) {
            null -> Try.Failure(IllegalArgumentException("Cannot parse json: ${answer.jsonString}"))
            else -> {
                if (parsingResult.totalCount == parsingResult.items.size) {
                    Try.Success(parsingResult to GitHubPageId(-1))
                } else {
                    linkHeaderParser.getNextPage(answer).flatMap { parsedNextPage ->
                        Try.Success(parsingResult to parsedNextPage)
                    }
                }
            }
        }
    }

}