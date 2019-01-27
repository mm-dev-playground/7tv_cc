package com.a7tv.codingchallenge.codingchallengefor7tv.repo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.a7tv.codingchallenge.codingchallengefor7tv.model.GitHubUser
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.http.SimpleHttpClient
import io.reactivex.schedulers.Schedulers

class GitHubDataFactory : DataSource.Factory<Long, GitHubUser>() {

    private var currentSourceId: GitHubDataSource.SourceId = GitHubDataSource.SourceId.AllUsers
    private var currentSearchText: String = ""

    private lateinit var currentDataSource: GitHubDataSource

    private var dataSourceInitialized = false

    override fun create(): DataSource<Long, GitHubUser> {
        currentDataSource = GitHubDataSource(
                SimpleHttpClient(), Schedulers.io(), currentSourceId, currentSearchText
        )
        dataSourceInitialized = true
        // dataSourceLiveData = currentDataSource.stateLiveData
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

    fun getStatusLiveData() = if (dataSourceInitialized) {
        currentDataSource.stateLiveData
    } else {
        MutableLiveData()
    }

}