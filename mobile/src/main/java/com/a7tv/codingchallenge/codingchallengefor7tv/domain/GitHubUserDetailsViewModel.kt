package com.a7tv.codingchallenge.codingchallengefor7tv.domain

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.a7tv.codingchallenge.codingchallengefor7tv.model.GitHubRepository
import com.a7tv.codingchallenge.codingchallengefor7tv.model.GitHubUserProfile
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.data.GitHubProfileDataStream
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.http.HttpClientInterface
import io.reactivex.Scheduler
import java.net.URL

class GitHubUserDetailsViewModel(userProfileUrl: String,
                                 httpClient: HttpClientInterface,
                                 scheduler: Scheduler,
                                 livePagedListBuilder: LivePagedListBuilder<Long, GitHubRepository>)
    : ViewModel() {

    val userDetailsLiveData: LiveData<GitHubUserProfile> = MutableLiveData()

    val repoLiveData: LiveData<PagedList<GitHubRepository>> = livePagedListBuilder.build()

    init {
        GitHubProfileDataStream(
                onFailure = { t -> Log.e(javaClass.simpleName, t.toString()) },
                onException = { e -> Log.e(javaClass.simpleName, e.toString()) },
                client = httpClient,
                scheduler = scheduler
        ).execute(URL(userProfileUrl)) { profile ->
            (userDetailsLiveData as MutableLiveData).postValue(profile)
        }
    }

}