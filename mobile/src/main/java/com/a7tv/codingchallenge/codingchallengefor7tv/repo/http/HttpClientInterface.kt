package com.a7tv.codingchallenge.codingchallengefor7tv.repo.http

import com.a7tv.codingchallenge.codingchallengefor7tv.util.typeclasses.Try
import io.reactivex.Single
import java.net.URL

interface HttpClientInterface {

    fun get(url: URL): Single<Try<String>>

}