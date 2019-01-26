package com.a7tv.codingchallenge.codingchallengefor7tv.repo.data

import android.annotation.SuppressLint
import com.a7tv.codingchallenge.codingchallengefor7tv.model.GitHubUser
import com.a7tv.codingchallenge.codingchallengefor7tv.model.GitHubUserId
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.http.HttpClientInterface
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.http.HttpGetAnswer
import com.a7tv.codingchallenge.codingchallengefor7tv.util.LinkHeaderParser
import com.a7tv.codingchallenge.codingchallengefor7tv.util.typeclasses.Try
import io.reactivex.Scheduler
import java.net.URL

class GitHubAllUsersDataStream(
        override val onSuccess: (Pair<List<GitHubUser>, GitHubUserId>) -> Unit,
        override val onFailure: (Throwable) -> Unit,
        override val onException: (Throwable) -> Unit,
        private val client: HttpClientInterface,
        private val scheduler: Scheduler
) : DataStream<Pair<List<GitHubUser>, GitHubUserId>, URL> {

    private val linkHeaderParser = LinkHeaderParser()

    @SuppressLint("CheckResult")
    // single subscription return value can be neglected, see http://reactivex.io/documentation/single.html
    override fun execute(input: URL) {
        client.getJsonFrom(input)
                .map { answerTry ->
                    parseUserListFromJson(answerTry)
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

    private fun parseUserListFromJson(answerTry: Try<HttpGetAnswer>) =
            answerTry.flatMap { answer ->
                linkHeaderParser.getNextUserId(answer).flatMap { parsedId ->
                    val parsingResult = GitHubUser.jsonListAdapter.fromJson(answer.jsonString)
                    when (parsingResult) {
                        null -> Try.Failure(IllegalArgumentException("Cannot parse json: ${answer.jsonString}"))
                        else -> Try.Success(parsingResult to parsedId)
                    }
                }
            }

}