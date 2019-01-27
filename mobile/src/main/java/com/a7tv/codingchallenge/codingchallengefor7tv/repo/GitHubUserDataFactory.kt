package com.a7tv.codingchallenge.codingchallengefor7tv.repo

import android.util.Log
import androidx.paging.DataSource
import com.a7tv.codingchallenge.codingchallengefor7tv.model.GitHubUser
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.data.AllUsersDataStream
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.data.SearchUsersDataStream
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.http.SimpleHttpClient
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

// TODO to make this class and components relying on it properly testable, inject the
// httpClient and RX scheduler to it - otherwise it is hard to test E2E based on the underlying
// data streams
class GitHubUserDataFactory : DataSource.Factory<Long, GitHubUser>() {

    private var currentSourceIdUser: GitHubUserDataSource.SourceId = GitHubUserDataSource.SourceId.AllUsers
    private var currentSearchText: String = ""

    private lateinit var currentUserDataSource: GitHubUserDataSource

    private var dataSourceInitialized = false

    private val stateCommunicationSubject = PublishSubject.create<Int>()

    private val onLoadingFailure: (Throwable) -> Unit = { e ->
        Log.e(javaClass.simpleName, e.toString())
        stateCommunicationSubject.onNext(GitHubUserDataSource.State.ERROR)
    }

    private val onLoadingException: (Throwable) -> Unit = { e ->
        Log.e(javaClass.simpleName, e.toString())
        stateCommunicationSubject.onNext(GitHubUserDataSource.State.ERROR)
    }

    override fun create(): DataSource<Long, GitHubUser> {
        currentUserDataSource = GitHubUserDataSource(
                currentSourceIdUser,
                currentSearchText,
                AllUsersDataStream(SimpleHttpClient(), Schedulers.io(), onLoadingFailure, onLoadingException),
                SearchUsersDataStream(SimpleHttpClient(), Schedulers.io(), onLoadingFailure, onLoadingException),
                stateCommunicationSubject
        )
        dataSourceInitialized = true
        return currentUserDataSource
    }

    fun setCurrentSearchText(text: String) {
        if (text != currentSearchText) {
            currentSearchText = text
            currentUserDataSource.invalidate()
        }
    }

    fun setNewSourceId(id: GitHubUserDataSource.SourceId) {
        Log.d(javaClass.simpleName, "received source id: $id")
        if (id != currentSourceIdUser) {
            currentUserDataSource.sourceId = id // forward to data source to let it choose correct stream
            currentSourceIdUser = id // save new id for create()
            currentUserDataSource.invalidate() // invalidate the source (hence, the list)
        }
    }

    fun getSourceStatus(): Observable<Int> = stateCommunicationSubject.hide()

}