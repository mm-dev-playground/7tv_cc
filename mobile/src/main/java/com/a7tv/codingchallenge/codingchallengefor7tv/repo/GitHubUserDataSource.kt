package com.a7tv.codingchallenge.codingchallengefor7tv.repo

import androidx.paging.PageKeyedDataSource
import com.a7tv.codingchallenge.codingchallengefor7tv.model.GitHubUser
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.data.AllUsersDataStream
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.data.SearchUsersDataStream
import com.a7tv.codingchallenge.codingchallengefor7tv.util.GitHubApiConstants.BASE_URL
import com.a7tv.codingchallenge.codingchallengefor7tv.util.GitHubApiConstants.SEARCH_ENDPOINT
import com.a7tv.codingchallenge.codingchallengefor7tv.util.GitHubApiConstants.USERS_ENDPOINT
import com.a7tv.codingchallenge.codingchallengefor7tv.util.GitHubApiConstants.buildPagedQueryParam
import com.a7tv.codingchallenge.codingchallengefor7tv.util.GitHubApiConstants.buildQueryParam
import com.a7tv.codingchallenge.codingchallengefor7tv.util.GitHubApiConstants.buildSinceParam
import com.a7tv.codingchallenge.codingchallengefor7tv.util.LinkHeaderParser
import io.reactivex.subjects.Subject
import java.net.URL

class GitHubUserDataSource(initialSourceId: SourceId,
                           private var currentSearchText: String,
                           private val allUsersDataStream: AllUsersDataStream,
                           private val searchUsersDataStream: SearchUsersDataStream,
                           private val stateCommunicationSubject: Subject<Int>) :
        PageKeyedDataSource<Long, GitHubUser>() {

    sealed class SourceId {
        object AllUsers : SourceId()
        object UserSearch : SourceId()
    }

    object State {
        const val ERROR = -1
        const val LOADING = 1
        const val LOADED = 2
    }

    var sourceId = initialSourceId

    override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<Long, GitHubUser>) {
        signalLoading()
        when (sourceId) {
            SourceId.AllUsers -> startAllUsersDataStreamInitial(callback)
            SourceId.UserSearch -> startSearchUsersDataStreamInitial(callback)
        }
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, GitHubUser>) {
        if (params.key == LinkHeaderParser.FINAL_ID) {
            callback.onResult(emptyList(), null) // signal end of data with emptyList() [see doc]
        } else {
            signalLoading()
            when (sourceId) {
                SourceId.AllUsers -> startAllUsersDataStreamAfter(params, callback)
                SourceId.UserSearch -> startSearchUsersDataStreamAfter(params, callback)
            }
        }
    }

    private fun startSearchUsersDataStreamInitial(callback: LoadInitialCallback<Long, GitHubUser>) {
        searchUsersDataStream.execute(
                URL(BASE_URL + SEARCH_ENDPOINT + USERS_ENDPOINT + buildQueryParam(currentSearchText))
        ) { (userList, nextPageId) ->
            signalLoadingSuccessful()
            callback.onResult(userList, null, nextPageId.number)
        }
    }

    private fun startAllUsersDataStreamInitial(callback: LoadInitialCallback<Long, GitHubUser>) {
        allUsersDataStream.execute(
                URL(BASE_URL + USERS_ENDPOINT + buildSinceParam(0L))
        ) { (userList, nextId) ->
            signalLoadingSuccessful()
            callback.onResult(userList, null, nextId.value)
        }
    }

    private fun startAllUsersDataStreamAfter(params: LoadParams<Long>,
                                             callback: LoadCallback<Long, GitHubUser>) {
        allUsersDataStream.execute(
                URL(BASE_URL + USERS_ENDPOINT + buildSinceParam(params.key))
        ) { (userList, nextId) ->
            signalLoadingSuccessful()
            callback.onResult(userList, nextId.value)
        }
    }

    private fun startSearchUsersDataStreamAfter(params: LoadParams<Long>,
                                                callback: LoadCallback<Long, GitHubUser>) {
        searchUsersDataStream.execute(
                URL(BASE_URL + SEARCH_ENDPOINT + USERS_ENDPOINT +
                        buildPagedQueryParam(currentSearchText, params.key))
        ) { (userList, nextPageId) ->
            signalLoadingSuccessful()
            callback.onResult(userList, nextPageId.number)
        }
    }

    // ignored
    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, GitHubUser>) = Unit

    private fun signalLoadingSuccessful() {
        stateCommunicationSubject.onNext(State.LOADED)
    }

    private fun signalLoading() {
        stateCommunicationSubject.onNext(State.LOADING)
    }

}