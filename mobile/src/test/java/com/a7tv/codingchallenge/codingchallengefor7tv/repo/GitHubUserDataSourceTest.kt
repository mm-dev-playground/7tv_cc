package com.a7tv.codingchallenge.codingchallengefor7tv.repo

import com.a7tv.codingchallenge.codingchallengefor7tv.repo.data.AllUsersDataStream
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.data.SearchUsersDataStream
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.http.HttpClientInterface
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.http.HttpGetAnswer
import com.a7tv.codingchallenge.codingchallengefor7tv.util.typeclasses.Try
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.junit.Test

@Suppress("UNCHECKED_CAST")
internal class GitHubUserDataSourceTest {

    @Test
    fun `verify init and all user stream executed for all user id`() {
        val client = mock<HttpClientInterface> {
            on { getJsonFrom(any()) } doReturn
                    Single.just(Try.Success(HttpGetAnswer(emptyMap(), "Foo"))) as Single<Try<HttpGetAnswer>>
        }

        val allUserDataStream = AllUsersDataStream(
                client, Schedulers.trampoline(), {}, {}
        )
        val searchUsersDataStream = mock<SearchUsersDataStream> { }

        val communicationSubject = PublishSubject.create<Int>()
        val source = GitHubUserDataSource(
                GitHubUserDataSource.SourceId.AllUsers,
                "",
                allUserDataStream,
                searchUsersDataStream,
                communicationSubject
        )

        val communicationObserver = communicationSubject.test()

        source.loadInitial(mock {}, mock {})

        communicationObserver.assertValueCount(1)
                .assertValueAt(0) {
                    it == GitHubUserDataSource.State.LOADING
                }
    }

    @Test
    fun `verify init and search user stream executed for search user id`() {
        val client = mock<HttpClientInterface> {
            on { getJsonFrom(any()) } doReturn
                    Single.just(Try.Success(HttpGetAnswer(emptyMap(), "Foo"))) as Single<Try<HttpGetAnswer>>
        }

        val allUserDataStream = mock<AllUsersDataStream> {}
        val searchUsersDataStream = SearchUsersDataStream(
                client, Schedulers.trampoline(), {}, {}
        )

        val communicationSubject = PublishSubject.create<Int>()
        val source = GitHubUserDataSource(
                GitHubUserDataSource.SourceId.UserSearch,
                "",
                allUserDataStream,
                searchUsersDataStream,
                communicationSubject
        )

        val communicationObserver = communicationSubject.test()

        source.loadInitial(mock {}, mock {})

        communicationObserver.assertValueCount(1)
                .assertValueAt(0) {
                    it == GitHubUserDataSource.State.LOADING
                }
    }

}