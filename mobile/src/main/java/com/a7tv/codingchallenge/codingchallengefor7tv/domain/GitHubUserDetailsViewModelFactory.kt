package com.a7tv.codingchallenge.codingchallengefor7tv.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.http.HttpClientInterface
import io.reactivex.Scheduler

class GitHubUserDetailsViewModelFactory(private val userProfileUrl: String,
                                        private val httpClient: HttpClientInterface,
                                        private val scheduler: Scheduler) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        return GitHubUserDetailsViewModel(userProfileUrl, httpClient, scheduler) as T
    }

}