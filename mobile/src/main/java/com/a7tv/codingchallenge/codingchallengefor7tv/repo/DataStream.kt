package com.a7tv.codingchallenge.codingchallengefor7tv.repo

interface DataStream<T, V> {

    val onSuccess: (T) -> Unit
    val onFailure: (Throwable) -> Unit
    val onException: (Throwable) -> Unit

    fun execute(input: V)

}