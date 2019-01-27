package com.a7tv.codingchallenge.codingchallengefor7tv.repo.data

import android.annotation.SuppressLint
import com.a7tv.codingchallenge.codingchallengefor7tv.model.GitHubUserProfile
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.http.HttpClientInterface
import com.a7tv.codingchallenge.codingchallengefor7tv.util.typeclasses.Try
import io.reactivex.Scheduler
import java.net.URL

class GitHubProfileDataStream(
        override val onSuccess: (GitHubUserProfile) -> Unit,
        override val onFailure: (Throwable) -> Unit,
        override val onException: (Throwable) -> Unit,
        private val client: HttpClientInterface,
        private val scheduler: Scheduler
) : DataStream<GitHubUserProfile, URL> {

    @SuppressLint("CheckResult")
    // single subscription return value can be neglected, see http://reactivex.io/documentation/single.html
    override fun execute(input: URL) {
        client.getJsonFrom(input)
                .map { answerTry ->
                    answerTry.flatMap { answer ->
                        GitHubUserProfile.jsonAdapter.fromJson(answer.jsonString).run {
                            when (this) {
                                null -> Try.Failure(IllegalArgumentException("Cannot parse json: ${answer.jsonString}"))
                                else -> Try.just(this)
                            }
                        }
                    }
                }
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

}