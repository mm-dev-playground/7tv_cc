package com.a7tv.codingchallenge.codingchallengefor7tv.repo.http

import org.junit.jupiter.api.Test
import java.net.URL
import java.util.concurrent.CountDownLatch

internal class SimpleHttpClientTest {

    @Test
    fun `foo test`() {

        val latch = CountDownLatch(1)
        val client = SimpleHttpClient()
        client.getJsonFrom(URL("https://api.github.com/users"))
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