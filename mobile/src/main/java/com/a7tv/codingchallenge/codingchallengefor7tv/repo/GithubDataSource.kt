package com.a7tv.codingchallenge.codingchallengefor7tv.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.a7tv.codingchallenge.codingchallengefor7tv.model.GitHubUser
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.http.HttpClientInterface
import com.a7tv.codingchallenge.codingchallengefor7tv.util.LinkHeaderParser
import com.a7tv.codingchallenge.codingchallengefor7tv.util.typeclasses.Try
import io.reactivex.schedulers.Schedulers
import java.net.URL

class GithubDataSource(private val client: HttpClientInterface) : PageKeyedDataSource<Long, GitHubUser>() {

    object State {
        const val ERROR = -1
        const val INIT = 0
        const val LOADING = 1
        const val LOADED = 2
    }

    val stateLiveData: LiveData<Int> = MutableLiveData<Int>()

    private companion object {
        const val GITHUB_API_BASE_URL = "https://api.github.com"
        const val GITHUB_API_USERS_ENDPOINT = "/users"
        fun createSinceParam(userId: Long) = "?since=$userId"
    }

    private fun signalInitialLoading() {
        (stateLiveData as MutableLiveData).postValue(State.INIT)
    }

    private fun signalLoadingSuccessful() {
        (stateLiveData as MutableLiveData).postValue(State.LOADED)
    }

    private fun signalLoadingError() {
        (stateLiveData as MutableLiveData).postValue(State.ERROR)
    }

    private fun signalLoading() {
        (stateLiveData as MutableLiveData).postValue(State.LOADING)
    }

    override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<Long, GitHubUser>) {
        signalInitialLoading()
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
                                is Try.Success -> {
                                    signalLoadingSuccessful()
                                    callback.onResult(result.value.first, null, result.value.second)
                                }
                                is Try.Failure -> {
                                    Log.e(javaClass.simpleName, result.error.toString())
                                    signalLoadingError()
                                }
                            }
                        },
                        { e ->
                            signalLoadingError()
                            Log.e(javaClass.simpleName, e.toString())
                        }
                )
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, GitHubUser>) {
        signalLoading()
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
                                is Try.Success -> {
                                    signalLoadingSuccessful()
                                    callback.onResult(result.value.first, result.value.second)
                                }
                                is Try.Failure -> {
                                    Log.e(javaClass.simpleName, result.error.toString())
                                    signalLoadingError()
                                }
                            }
                        },
                        { e ->
                            signalLoadingError()
                            Log.e(javaClass.simpleName, e.toString())
                        }
                )

    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, GitHubUser>) {
        // ignore
    }

}