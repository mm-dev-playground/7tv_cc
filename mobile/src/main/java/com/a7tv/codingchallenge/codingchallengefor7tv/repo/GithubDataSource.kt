package com.a7tv.codingchallenge.codingchallengefor7tv.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.a7tv.codingchallenge.codingchallengefor7tv.model.GitHubUser
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.http.HttpClientInterface
import io.reactivex.Scheduler
import java.net.URL

class GithubDataSource(private val client: HttpClientInterface,
                       private val httpRequestScheduler: Scheduler) : PageKeyedDataSource<Long, GitHubUser>() {

    object State {
        const val ERROR = -1
        const val INIT = 0
        const val LOADING = 1
        const val LOADED = 2
    }

    private companion object {
        const val GITHUB_API_BASE_URL = "https://api.github.com"
        const val GITHUB_API_USERS_ENDPOINT = "/users"
        fun createSinceParam(userId: Long) = "?since=$userId"
    }

    val stateLiveData: LiveData<Int> = MutableLiveData<Int>()

    override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<Long, GitHubUser>) {
        signalInitialLoading()
        GitHubUserDataStream(
                onSuccess = { (userList, nextId) ->
                    signalLoadingSuccessful()
                    callback.onResult(userList, null, nextId.value)
                },
                onFailure = { e ->
                    Log.e(javaClass.simpleName, e.toString())
                    signalLoadingError()
                },
                onException = { e ->
                    Log.e(javaClass.simpleName, e.toString())
                    signalLoadingError()
                },
                client = client,
                scheduler = httpRequestScheduler
        ).execute(
                URL(GITHUB_API_BASE_URL + GITHUB_API_USERS_ENDPOINT + createSinceParam(0L))
        )
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, GitHubUser>) {
        signalLoading()
        GitHubUserDataStream(
                onSuccess = { (userList, nextId) ->
                    signalLoadingSuccessful()
                    callback.onResult(userList, nextId.value)
                },
                onFailure = { e ->
                    Log.e(javaClass.simpleName, e.toString())
                    signalLoadingError()
                },
                onException = { e ->
                    Log.e(javaClass.simpleName, e.toString())
                    signalLoadingError()
                },
                client = client,
                scheduler = httpRequestScheduler
        ).execute(
                URL(GITHUB_API_BASE_URL + GITHUB_API_USERS_ENDPOINT + createSinceParam(params.key))
        )
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, GitHubUser>) {
        // ignore
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

}