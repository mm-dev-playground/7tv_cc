package com.a7tv.codingchallenge.codingchallengefor7tv.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LivePagedListBuilder
import com.a7tv.codingchallenge.codingchallengefor7tv.model.GitHubUser
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.GitHubDataFactory

class GitHubUserListViewModelFactory(private val dataFactory: GitHubDataFactory,
                                     private val livePagedListBuilder: LivePagedListBuilder<Long, GitHubUser>) :
        ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        return GitHubUserListViewModel(dataFactory, livePagedListBuilder) as T
    }

}