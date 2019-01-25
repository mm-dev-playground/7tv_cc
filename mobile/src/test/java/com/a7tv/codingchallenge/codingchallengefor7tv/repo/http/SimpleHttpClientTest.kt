package com.a7tv.codingchallenge.codingchallengefor7tv.repo.http

import com.a7tv.codingchallenge.codingchallengefor7tv.model.GitHubUser
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.GithubDataSource
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch

internal class SimpleHttpClientTest {

    @Test
    fun `foo test`() {

        val latch = CountDownLatch(1)
        /*
        val client = SimpleHttpClient()
        client.getJsonFrom(URL("https://api.github.com/users"))
                .doFinally {
                    latch.countDown()
                }
                .subscribe(
                        { content -> println(content) },
                        { error -> System.err.println(error) }
                )
                */
        GithubDataSource(SimpleHttpClient()).getNextUserDataset()
                .map {
                    it.map {
                        GitHubUser.jsonListAdapter.fromJson(it.jsonString)
                    }
                }
                .doFinally {
                    latch.countDown()
                }
                .subscribe(
                        { content -> println(content) },
                        { error -> System.err.println(error) }
                )

        latch.await()

    }

}