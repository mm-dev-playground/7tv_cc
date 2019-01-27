package com.a7tv.codingchallenge.codingchallengefor7tv.repo.data

interface DataStream<T, V> {

    val onFailure: (Throwable) -> Unit
    val onException: (Throwable) -> Unit

    fun execute(input: V, onSuccess: (T) -> Unit)

}