package com.a7tv.codingchallenge.codingchallengefor7tv.repo

import androidx.paging.PageKeyedDataSource
import com.a7tv.codingchallenge.codingchallengefor7tv.model.GitHubRepository
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.data.GitHubReposDataStream
import com.a7tv.codingchallenge.codingchallengefor7tv.util.GitHubApiConstants
import com.a7tv.codingchallenge.codingchallengefor7tv.util.LinkHeaderParser
import java.net.URL

class GitHubRepoDataSource(private val repoUrl: URL, private val repoStream: GitHubReposDataStream)
    : PageKeyedDataSource<Long, GitHubRepository>() {

    override fun loadInitial(params: LoadInitialParams<Long>,
                             callback: LoadInitialCallback<Long, GitHubRepository>) {
        repoStream.execute(repoUrl) { (repos, nextPageId) ->
            callback.onResult(repos, null, nextPageId.number)
        }
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, GitHubRepository>) {
        if (params.key == LinkHeaderParser.FINAL_ID) {
            callback.onResult(emptyList(), null) // signal end of data with emptyList() [see doc]
        } else {
            val nextUrl = repoUrl.toString() + GitHubApiConstants.buildPageParam(params.key)
            repoStream.execute(URL(nextUrl)) { (repos, nextPageId) ->
                callback.onResult(repos, nextPageId.number)
            }
        }
    }

    // ignore
    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, GitHubRepository>) =
            Unit

}