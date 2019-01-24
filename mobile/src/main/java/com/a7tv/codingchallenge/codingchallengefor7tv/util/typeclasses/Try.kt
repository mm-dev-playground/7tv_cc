package com.a7tv.codingchallenge.codingchallengefor7tv.util.typeclasses

// Naive implementation of the Try Monad: Wraps the result of a computation into a success or fail context.
// For a comprehensive implementation of the Try Monad see: https://github.com/arrow-kt/arrow
sealed class Try<out T> {

    companion object {
        fun <V> just(v: V): Try<V> = Success(v)
    }

    data class Failure(val error: Throwable) : Try<Nothing>()

    data class Success<out T>(val value: T) : Try<T>()

    inline fun <V> flatMap(f: (T) -> Try<V>): Try<V> =
            when (this) {
                is Failure -> this
                is Success -> f(value)
            }

    inline fun <B> map(f: (T) -> B): Try<B> =
            flatMap { Try.just(f(it)) }

}