package com.a7tv.codingchallenge.codingchallengefor7tv.repo.data

import android.annotation.SuppressLint
import com.a7tv.codingchallenge.codingchallengefor7tv.model.GitHubPageId
import com.a7tv.codingchallenge.codingchallengefor7tv.model.GitHubRepository
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.http.HttpClientInterface
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.http.HttpGetAnswer
import com.a7tv.codingchallenge.codingchallengefor7tv.util.LinkHeaderParser
import com.a7tv.codingchallenge.codingchallengefor7tv.util.typeclasses.Try
import io.reactivex.Scheduler
import java.net.URL

class GitHubReposDataStream(
        private val client: HttpClientInterface,
        private val scheduler: Scheduler,
        override val onFailure: (Throwable) -> Unit,
        override val onException: (Throwable) -> Unit
) : DataStream<Pair<List<GitHubRepository>, GitHubPageId>, URL> {

    private val linkHeaderParser = LinkHeaderParser()

    @SuppressLint("CheckResult")
    // single subscription return value can be neglected, see http://reactivex.io/documentation/single.html
    override fun execute(input: URL, onSuccess: (Pair<List<GitHubRepository>, GitHubPageId>) -> Unit) {
        client.getJsonFrom(input)
                .map { answerTry ->
                    answerTry.flatMap { answer ->
                        parseResultFromAnswer(answer)
                    }
                }
                .map { resultTry ->
                    resultTry.map { (repoName, nextPageId) ->
                        repoName to nextPageId
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

    private fun parseResultFromAnswer(answer: HttpGetAnswer):
            Try<Pair<List<GitHubRepository>, GitHubPageId>> {
        val parsingResult = GitHubRepository.jsonListAdapter.fromJson(answer.jsonString)
        return when (parsingResult) {
            null -> Try.Failure(IllegalArgumentException("Cannot parse json: ${answer.jsonString}"))
            else -> {
                    linkHeaderParser.getNextPage(answer).flatMap { parsedNextPage ->
                        Try.Success(parsingResult to parsedNextPage)
                    }
            }
        }
    }


}