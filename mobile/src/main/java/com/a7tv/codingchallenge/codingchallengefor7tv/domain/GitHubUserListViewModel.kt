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

class GitHubUserListViewModel(private val dataFactory: GitHubDataFactory,
                              livePagedListBuilder: LivePagedListBuilder<Long, GitHubUser>) : ViewModel() {

    var loadingStateData: LiveData<Int> = dataFactory.getStatusLiveData() // TODO still not working!
        private set

    val usersLiveData: LiveData<PagedList<GitHubUser>> = livePagedListBuilder.build()

    private val searchTextRxSubject = PublishSubject.create<String>()
    private val searchTextDisposable: Disposable

    init {
        searchTextDisposable = searchTextRxSubject.hide()
                .debounce(750, TimeUnit.MILLISECONDS)
                .doOnNext {
                    if (it.length > 2) {
                        dataFactory.setCurrentSearchText(it) // causes invalidate!
                        loadingStateData = dataFactory.getStatusLiveData()
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
                    loadingStateData = dataFactory.getStatusLiveData()
                    // loadingStateData = dataFactory.getStatusLiveData()
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