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

class GitHubDataFactory : DataSource.Factory<Long, GitHubUser>() {

    private var currentSourceId: GitHubDataSource.SourceId = GitHubDataSource.SourceId.AllUsers
    private var currentSearchText: String = ""

    private lateinit var currentDataSource: GitHubDataSource

    private var dataSourceInitialized = false

    private val stateCommunicationSubject = PublishSubject.create<Int>()

    private val onLoadingFailure: (Throwable) -> Unit = { e ->
        Log.e(javaClass.simpleName, e.toString())
        stateCommunicationSubject.onNext(GitHubDataSource.State.ERROR)
    }

    private val onLoadingException: (Throwable) -> Unit = { e ->
        Log.e(javaClass.simpleName, e.toString())
        stateCommunicationSubject.onNext(GitHubDataSource.State.ERROR)
    }

    override fun create(): DataSource<Long, GitHubUser> {
        currentDataSource = GitHubDataSource(
                currentSourceId,
                currentSearchText,
                AllUsersDataStream(SimpleHttpClient(), Schedulers.io(), onLoadingFailure, onLoadingException),
                SearchUsersDataStream(SimpleHttpClient(), Schedulers.io(), onLoadingFailure, onLoadingException),
                stateCommunicationSubject
        )
        dataSourceInitialized = true
        return currentDataSource
    }

    fun setCurrentSearchText(text: String) {
        if (text != currentSearchText) {
            currentSearchText = text
            currentDataSource.invalidate()
        }
    }

    fun setNewSourceId(id: GitHubDataSource.SourceId) {
        Log.d(javaClass.simpleName, "received source id: $id")
        if (id != currentSourceId) {
            currentDataSource.sourceId = id // forward to data source to let it choose correct stream
            currentSourceId = id // save new id for create()
            currentDataSource.invalidate() // invalidate the source (hence, the list)
        }
    }

    fun getSourceStatus(): Observable<Int> = stateCommunicationSubject.hide()

}