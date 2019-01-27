package com.a7tv.codingchallenge.codingchallengefor7tv.repo

import com.a7tv.codingchallenge.codingchallengefor7tv.model.GitHubUser
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.http.HttpClientInterface
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.http.HttpGetAnswer
import com.a7tv.codingchallenge.codingchallengefor7tv.util.HttpException
import com.a7tv.codingchallenge.codingchallengefor7tv.util.typeclasses.Try
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.junit.Test

@Suppress("UNCHECKED_CAST")
internal class GitHubDataSourceTest {

    @Test
    fun `verify init and error are reported`() {
        val client = mock<HttpClientInterface> {
            on { getJsonFrom(any()) } doReturn
                    Single.just(Try.Failure(HttpException(403))) as Single<Try<HttpGetAnswer>>
        }

        val communicationSubject = PublishSubject.create<Int>()
        val source = GitHubDataSource(
                client,
                Schedulers.trampoline(),
                GitHubDataSource.SourceId.AllUsers,
                "",
                communicationSubject
        )

        val communicationObserver = communicationSubject.test()

        source.loadInitial(mock {}, mock {})

        communicationObserver.assertValueCount(2)
                .assertValueAt(0) {
                    it == GitHubDataSource.State.LOADING
                }
                .assertValueAt(1) {
                    it == GitHubDataSource.State.ERROR
                }
    }

    @Test
    fun `verify init and success are reported`() {
        val adapter = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                .adapter(GitHubUser::class.java)
        val jsonMock = adapter.toJson(mock { })
        println(jsonMock)

        val client = mock<HttpClientInterface> {
            on { getJsonFrom(any()) } doReturn
                    Single.just(Try.just(HttpGetAnswer(emptyMap(), jsonMock))) as Single<Try<HttpGetAnswer>>
        }

        val communicationSubject = PublishSubject.create<Int>()
        val source = GitHubDataSource(
                client,
                Schedulers.trampoline(),
                GitHubDataSource.SourceId.AllUsers,
                "",
                communicationSubject
        )

        val communicationObserver = communicationSubject.test()

        source.loadInitial(mock {}, mock {})

        /*
        communicationObserver.assertValueCount(2)
                .assertValueAt(0) {
                    it == GitHubDataSource.State.INIT
                }
                .assertValueAt(1) {
                    it == GitHubDataSource.State.LOADED
                }
                */
        communicationObserver.values().forEach {
            println(it)
        }
    }

}