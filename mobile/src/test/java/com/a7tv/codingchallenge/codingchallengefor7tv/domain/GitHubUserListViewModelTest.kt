package com.a7tv.codingchallenge.codingchallengefor7tv.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.GitHubDataFactory
import org.junit.Rule
import org.junit.Test


internal class GitHubUserListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `foo test`() {
        val dataFactory = GitHubDataFactory()
        val listBuilder = LivePagedListBuilder(
                dataFactory,
                PagedList.Config.Builder()
                        .setPrefetchDistance(1)
                        .setPageSize(20) // TODO extract magic number
                        .build()
        )
        val viewModel = GitHubUserListViewModel(dataFactory, listBuilder)

        val dataSource = dataFactory.create()

        viewModel.searchTextEdited("TEST!")

        viewModel.loadingStateData.observeForever { loadingState ->
            println(loadingState)
        }

        viewModel.usersLiveData.observeForever { pagedUserList ->
            println(pagedUserList.joinToString())
        }

        dataSource.invalidate()
    }

}