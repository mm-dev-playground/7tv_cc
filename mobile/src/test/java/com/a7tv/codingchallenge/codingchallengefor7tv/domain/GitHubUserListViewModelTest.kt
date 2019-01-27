package com.a7tv.codingchallenge.codingchallengefor7tv.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.paging.LivePagedListBuilder
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import com.a7tv.codingchallenge.codingchallengefor7tv.model.GitHubUser
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.GitHubUserDataFactory
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.GitHubUserDataSource
import com.nhaarman.mockitokotlin2.mock
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals


internal class GitHubUserListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `loading state is forwarded to view`() {
        val dataFactory = GitHubUserDataFactory()
        val listBuilder = LivePagedListBuilder(
                dataFactory,
                PagedList.Config.Builder()
                        .setPrefetchDistance(1)
                        .setPageSize(20)
                        .build()
        )
        val viewModel = GitHubUserListViewModel(dataFactory, listBuilder)
        val source = dataFactory.create()
        source as PageKeyedDataSource<Long, GitHubUser>


        var currentLoadingState = -1

        val lifecycleOwner = mock<LifecycleOwner> {  }
        val lifecycle = LifecycleRegistry(lifecycleOwner).apply {
            handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }

        viewModel.loadingStateData.observe({ lifecycle }) { loadingState ->
            currentLoadingState = loadingState
        }

        source.loadInitial(
                PageKeyedDataSource.LoadInitialParams(20, true),
                object: PageKeyedDataSource.LoadInitialCallback<Long, GitHubUser>() {
                    override fun onResult(data: MutableList<GitHubUser>, position: Int,
                                          totalCount: Int, previousPageKey: Long?,
                                          nextPageKey: Long?) = Unit
                    override fun onResult(data: MutableList<GitHubUser>, previousPageKey: Long?,
                                          nextPageKey: Long?) = Unit
                }
        )

        assertEquals(GitHubUserDataSource.State.LOADING, currentLoadingState)
    }

}