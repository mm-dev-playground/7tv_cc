package com.a7tv.codingchallenge.codingchallengefor7tv.repo

import android.util.Log
import androidx.paging.DataSource
import com.a7tv.codingchallenge.codingchallengefor7tv.model.GitHubRepository
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.data.GitHubReposDataStream
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.http.SimpleHttpClient
import io.reactivex.schedulers.Schedulers
import java.net.URL

// TODO to make this class and components relying on it properly testable, inject the
// httpClient and RX scheduler to it - otherwise it is hard to test E2E based on the underlying
// data streams
class GitHubRepoDataFactory(private val repoUrl: URL) : DataSource.Factory<Long, GitHubRepository>() {

    override fun create(): DataSource<Long, GitHubRepository> {
        return GitHubRepoDataSource(
                repoUrl,
                GitHubReposDataStream(
                        SimpleHttpClient(), Schedulers.io(),
                        { e ->
                            Log.e(javaClass.simpleName, e.toString()) // TODO this is poor error recovery
                        },
                        { e ->
                            Log.e(javaClass.simpleName, e.toString()) // TODO this is poor error recovery
                        }
                )
        )
    }

}