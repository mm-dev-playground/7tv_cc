package com.a7tv.codingchallenge.codingchallengefor7tv.repo

import com.a7tv.codingchallenge.codingchallengefor7tv.repo.http.HttpClientInterface
import java.net.URL

class GithubDataSource(private val client: HttpClientInterface) {

    private companion object {
        const val GITHUB_API_BASE_URL = "https://api.github.com"
        const val GITHUB_API_USERS_ENDPOINT = "/users"
        fun createSinceParam(userId: Int) = "?since=$userId"
    }

    private var nextUserIdPointer = 0

    fun getNextUserDataset() =
            client.getJsonFrom(
                    URL(GITHUB_API_BASE_URL + GITHUB_API_USERS_ENDPOINT + createSinceParam(nextUserIdPointer))
            ).doOnEvent { httpAnswerTry, _ ->
                httpAnswerTry.map { answer ->
                   answer.headers["Link"]?.forEach {
                       println(it)
                   }
                }
            }

}