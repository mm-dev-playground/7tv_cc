package com.a7tv.codingchallenge.codingchallengefor7tv.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.a7tv.codingchallenge.codingchallengefor7tv.model.GitHubUser
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.GitHubDataFactory
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.GitHubDataSource
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class GitHubUserListViewModel(private val dataFactory: GitHubDataFactory,
                              private val livePagedListBuilder: LivePagedListBuilder<Long, GitHubUser>) : ViewModel() {

    var loadingStateData: LiveData<Int> = MutableLiveData()
    val usersLiveData: LiveData<PagedList<GitHubUser>>

    private val searchTextRxSubject = PublishSubject.create<String>()
    private val searchTextDisposable: Disposable

    init {
        loadingStateData = dataFactory.dataSourceLiveData
        usersLiveData = livePagedListBuilder.build()

        searchTextDisposable = searchTextRxSubject.hide()
                .debounce(750, TimeUnit.MILLISECONDS)
                .doOnNext {
                    if (it.length > 2) {
                        dataFactory.setCurrentSearchText(it) // causes invalidate!
                        loadingStateData = dataFactory.dataSourceLiveData
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
                    loadingStateData = dataFactory.dataSourceLiveData
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