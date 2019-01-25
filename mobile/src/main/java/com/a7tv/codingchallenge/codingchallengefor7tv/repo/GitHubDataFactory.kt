package com.a7tv.codingchallenge.codingchallengefor7tv.repo

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.a7tv.codingchallenge.codingchallengefor7tv.model.GitHubUser
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.http.SimpleHttpClient

class GitHubDataFactory : DataSource.Factory<Long, GitHubUser>() {

    val dataSourceLiveData = MutableLiveData<GithubDataSource>()

    override fun create(): DataSource<Long, GitHubUser> {
        val gitHubDataSource = GithubDataSource(SimpleHttpClient()) // TODO inject with di!
        dataSourceLiveData.postValue(gitHubDataSource)
        return gitHubDataSource
    }

}