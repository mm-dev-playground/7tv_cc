package com.a7tv.codingchallenge.codingchallengefor7tv.repo

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.a7tv.codingchallenge.codingchallengefor7tv.model.GitHubUser
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.http.SimpleHttpClient
import io.reactivex.schedulers.Schedulers

class GitHubDataFactory : DataSource.Factory<Long, GitHubUser>() {

    private val gitHubDataSource = GithubDataSource(SimpleHttpClient(), Schedulers.io()) // TODO inject with di!
    val dataSourceLiveData: LiveData<Int> = gitHubDataSource.stateLiveData

    override fun create(): DataSource<Long, GitHubUser> {
        return gitHubDataSource
    }

}