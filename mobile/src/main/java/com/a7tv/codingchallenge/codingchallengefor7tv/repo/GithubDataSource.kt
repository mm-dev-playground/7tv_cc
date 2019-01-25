package com.a7tv.codingchallenge.codingchallengefor7tv.repo

import android.util.Log
import androidx.paging.PageKeyedDataSource
import com.a7tv.codingchallenge.codingchallengefor7tv.model.GitHubUser
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.http.HttpClientInterface
import com.a7tv.codingchallenge.codingchallengefor7tv.util.LinkHeaderParser
import com.a7tv.codingchallenge.codingchallengefor7tv.util.typeclasses.Try
import io.reactivex.schedulers.Schedulers
import java.net.URL

class GithubDataSource(private val client: HttpClientInterface) : PageKeyedDataSource<Long, GitHubUser>() {

    override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<Long, GitHubUser>) {
        client.getJsonFrom(
                URL(GITHUB_API_BASE_URL + GITHUB_API_USERS_ENDPOINT + createSinceParam(0L))
        ).map {
            it.flatMap { answer ->
                LinkHeaderParser().getNextId(answer).map {
                    GitHubUser.jsonListAdapter.fromJson(answer.jsonString)!! to it // TODO not null checked parse result!
                }
            }
        }.subscribeOn(Schedulers.io())
                .subscribe(
                        { result ->
                            when (result) {
                                is Try.Success -> callback.onResult(result.value.first, null, result.value.second)
                            }
                        },
                        { e -> Log.e(javaClass.simpleName, e.toString()) }
                )
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, GitHubUser>) {
        Log.d(javaClass.simpleName, "load after called")
        client.getJsonFrom(
                URL(GITHUB_API_BASE_URL + GITHUB_API_USERS_ENDPOINT + createSinceParam(params.key))
        ).map {
            it.flatMap { answer ->
                LinkHeaderParser().getNextId(answer).map {
                    GitHubUser.jsonListAdapter.fromJson(answer.jsonString)!! to it // TODO not null checked parse result!
                }
            }
        }.subscribeOn(Schedulers.io())
                .subscribe(
                        { result ->
                            when (result) {
                                is Try.Success -> callback.onResult(result.value.first, result.value.second)
                            }
                        },
                        { e -> Log.e(javaClass.simpleName, e.toString()) }
                )

    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, GitHubUser>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private companion object {
        const val GITHUB_API_BASE_URL = "https://api.github.com"
        const val GITHUB_API_USERS_ENDPOINT = "/users"
        fun createSinceParam(userId: Long) = "?since=$userId"
    }

}