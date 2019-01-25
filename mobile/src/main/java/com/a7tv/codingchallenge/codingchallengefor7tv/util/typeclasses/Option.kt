package com.a7tv.codingchallenge.codingchallengefor7tv.util.typeclasses

// Basic implementation of the Option Monad (java.util.Optional is not available under SDKv24)
// For a comprehensive implementation of the Option Monad see: https://github.com/arrow-kt/arrow
sealed class Option<out T> {

    companion object {
        fun <V> just(v: V) = Some(v)
    }

    object None : Option<Nothing>()

    data class Some<out T>(val value: T) : Option<T>()

    inline fun <V> flatMap(f: (T) -> Option<V>): Option<V> =
            when (this) {
                is None -> this
                is Some -> f(value)
            }

    inline fun <V> map(f: (T) -> V): Option<V> =
            when (this) {
                is None -> this
                is Some -> flatMap { just(f(value)) }
            }

}