package com.a7tv.codingchallenge.codingchallengefor7tv.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.a7tv.codingchallenge.codingchallengefor7tv.model.GitHubUser
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.data.GitHubAllUsersDataStream
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.data.GitHubUserSearchDataStream
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.http.HttpClientInterface
import com.a7tv.codingchallenge.codingchallengefor7tv.util.LinkHeaderParser
import io.reactivex.Scheduler
import java.net.URL

class GitHubDataSource(private val client: HttpClientInterface,
                       private val httpRequestScheduler: Scheduler,
                       initialSourceId: SourceId,
                       private var currentSearchText: String) :
        PageKeyedDataSource<Long, GitHubUser>() {

    sealed class SourceId {
        object AllUsers : SourceId()
        object UserSearch : SourceId()
    }

    private companion object GitHubApi {
        const val BASE_URL = "https://api.github.com"
        const val SEARCH_ENDPOINT = "/search"
        const val USERS_ENDPOINT = "/users"
        fun buildSinceParam(userId: Long) = "?since=$userId"
        fun buildQueryParam(query: String) = "?q=$query"
        fun buildPagedQueryParam(query: String, pageId: Long) = buildQueryParam(query) + "&page=$pageId"
    }

    object State {
        const val ERROR = -1
        const val INIT = 0
        const val LOADING = 1
        const val LOADED = 2
    }

    val stateLiveData: LiveData<Int> = MutableLiveData()

    var sourceId = initialSourceId

    override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<Long, GitHubUser>) {
        signalInitialLoading()
        when (sourceId) {
            SourceId.AllUsers -> startAllUsersDataStreamInitial(callback)
            SourceId.UserSearch -> startSearchUsersDataStreamInitial(callback)
        }
    }

    private fun startSearchUsersDataStreamInitial(callback: LoadInitialCallback<Long, GitHubUser>) {
        GitHubUserSearchDataStream(
                onSuccess = { (userList, nextPageId) ->
                    signalLoadingSuccessful()
                    callback.onResult(userList, null, nextPageId.number)
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
                URL(BASE_URL + SEARCH_ENDPOINT + USERS_ENDPOINT + buildQueryParam(currentSearchText))
        )
    }

    private fun startAllUsersDataStreamInitial(callback: LoadInitialCallback<Long, GitHubUser>) {
        GitHubAllUsersDataStream(
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
                URL(BASE_URL + USERS_ENDPOINT + buildSinceParam(0L))
        )
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, GitHubUser>) {
        if (params.key == LinkHeaderParser.FINAL_ID) {
            Log.w(javaClass.simpleName, "Skip loading - next page id is -1")
            return
        }
        signalLoading()
        when (sourceId) {
            SourceId.AllUsers -> startAllUsersDataStreamAfter(params, callback)
            SourceId.UserSearch -> startSearchUsersDataStreamAfter(params, callback)
        }
    }

    private fun startAllUsersDataStreamAfter(params: LoadParams<Long>,
                                             callback: LoadCallback<Long, GitHubUser>) {
        GitHubAllUsersDataStream(
                onSuccess = { (userList, nextId) ->
                    signalLoadingSuccessful()
                    callback.onResult(userList, nextId.value)
                },
                onFailure = { t ->
                    Log.e(javaClass.simpleName, t.toString())
                    signalLoadingError()
                },
                onException = { e ->
                    Log.e(javaClass.simpleName, e.toString())
                    signalLoadingError()
                },
                client = client,
                scheduler = httpRequestScheduler
        ).execute(
                URL(BASE_URL + USERS_ENDPOINT + buildSinceParam(params.key))
        )
    }

    private fun startSearchUsersDataStreamAfter(params: LoadParams<Long>,
                                                callback: LoadCallback<Long, GitHubUser>) {
        GitHubUserSearchDataStream(
                onSuccess = { (userList, nextPageId) ->
                    signalLoadingSuccessful()
                    callback.onResult(userList, nextPageId.number)
                },
                onFailure = { t ->
                    Log.e(javaClass.simpleName, t.toString())
                    signalLoadingError()
                },
                onException = { e ->
                    Log.e(javaClass.simpleName, e.toString())
                    signalLoadingError()
                },
                client = client,
                scheduler = httpRequestScheduler
        ).execute(
                URL(BASE_URL + SEARCH_ENDPOINT + USERS_ENDPOINT +
                        buildPagedQueryParam(currentSearchText, params.key))
        )
    }

    // ignored
    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, GitHubUser>) = Unit

    fun onNewSearch(searchText: String) {
        currentSearchText = searchText
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