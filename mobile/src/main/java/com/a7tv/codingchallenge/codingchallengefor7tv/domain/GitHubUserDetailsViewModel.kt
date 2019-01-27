package com.a7tv.codingchallenge.codingchallengefor7tv.domain

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.a7tv.codingchallenge.codingchallengefor7tv.model.GitHubUserProfile
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.data.GitHubProfileDataStream
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.http.HttpClientInterface
import io.reactivex.Scheduler
import java.net.URL

class GitHubUserDetailsViewModel(userProfileUrl: String,
                                 httpClient: HttpClientInterface,
                                 scheduler: Scheduler) : ViewModel() {

    val userDetailsLiveData: LiveData<GitHubUserProfile> = MutableLiveData()

    init {
        GitHubProfileDataStream(
                onSuccess = { profile -> (userDetailsLiveData as MutableLiveData).postValue(profile) },
                onFailure = { t -> Log.e(javaClass.simpleName, t.toString()) },
                onException = { e -> Log.e(javaClass.simpleName, e.toString()) },
                client = httpClient,
                scheduler = scheduler
        ).execute(URL(userProfileUrl))
    }

}