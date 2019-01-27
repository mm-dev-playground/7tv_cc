package com.a7tv.codingchallenge.codingchallengefor7tv.repo

@Suppress("UNCHECKED_CAST")
internal class GitHubDataSourceTest {

    /*
    @Test
    fun `verify init and error are reported`() {
        val client = mock<HttpClientInterface> {
            on { getJsonFrom(any()) } doReturn
                    Single.just(Try.Failure(HttpException(403))) as Single<Try<HttpGetAnswer>>
        }

        val communicationSubject = PublishSubject.create<Int>()
        val source = GitHubUserDataSource(
                client,
                Schedulers.trampoline(),
                GitHubUserDataSource.SourceId.AllUsers,
                "",
                communicationSubject
        )

        val communicationObserver = communicationSubject.test()

        source.loadInitial(mock {}, mock {})

        communicationObserver.assertValueCount(2)
                .assertValueAt(0) {
                    it == GitHubUserDataSource.State.LOADING
                }
                .assertValueAt(1) {
                    it == GitHubUserDataSource.State.ERROR
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
        val source = GitHubUserDataSource(
                client,
                Schedulers.trampoline(),
                GitHubUserDataSource.SourceId.AllUsers,
                "",
                communicationSubject
        )

        val communicationObserver = communicationSubject.test()

        source.loadInitial(mock {}, mock {})

        /*
        communicationObserver.assertValueCount(2)
                .assertValueAt(0) {
                    it == GitHubUserDataSource.State.INIT
                }
                .assertValueAt(1) {
                    it == GitHubUserDataSource.State.LOADED
                }
                */
        communicationObserver.values().forEach {
            println(it)
        }
    }
    */

}