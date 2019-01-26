package com.a7tv.codingchallenge.codingchallengefor7tv.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.a7tv.codingchallenge.codingchallengefor7tv.model.GitHubUser
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.GitHubDataFactory
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.GitHubDataSource
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class GitHubUserListViewModel : ViewModel() {

    val loadingStateData: LiveData<Int>
    val usersLiveData: LiveData<PagedList<GitHubUser>>

    private val dataFactory = GitHubDataFactory()

    private val searchTextRxSubject = PublishSubject.create<String>()
    private val searchTextDisposable: Disposable

    init {

        val pagedListConfig = PagedList.Config.Builder()
                .setPrefetchDistance(1)
                .setPageSize(20) // TODO extract magic number
                .build()

        loadingStateData = dataFactory.dataSourceLiveData
        usersLiveData = LivePagedListBuilder(dataFactory, pagedListConfig).build()

        searchTextDisposable = searchTextRxSubject.hide()
                .debounce(750, TimeUnit.MILLISECONDS)
                .doOnNext {
                    if (it.length > 2) {
                        dataFactory.setCurrentSearchText(it) // causes invalidate!
                    }
                }
                .map {
                    it.length
                }
                .map {
                    when (it > 2) {
                        true -> GitHubDataSource.SourceId.UserSearch
                        false -> GitHubDataSource.SourceId.AllUsers
                    }
                }
                .distinctUntilChanged()
                .doOnNext {
                    dataFactory.setNewSourceId(it) // causes invalidate
                }
                .subscribe()
    }

    fun searchTextEdited(text: String) {
        searchTextRxSubject.onNext(text)
    }

    override fun onCleared() {
        searchTextDisposable.dispose()
        super.onCleared()
    }

}