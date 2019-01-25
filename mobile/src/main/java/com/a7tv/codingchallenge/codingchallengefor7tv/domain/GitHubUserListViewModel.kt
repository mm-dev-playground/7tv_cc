package com.a7tv.codingchallenge.codingchallengefor7tv.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.a7tv.codingchallenge.codingchallengefor7tv.model.GitHubUser
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.GitHubDataFactory

class GitHubUserListViewModel : ViewModel() {

    val userListLiveData: LiveData<PagedList<GitHubUser>>

    init {

        val userListDataFactory = GitHubDataFactory()

        val pagedListConfig = PagedList.Config.Builder()
                .setPrefetchDistance(1)
                .setPageSize(100)
                .build()

        userListLiveData = LivePagedListBuilder(userListDataFactory, pagedListConfig)
                .build()
    }

}