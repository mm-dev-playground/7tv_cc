package com.a7tv.codingchallenge.codingchallengefor7tv.repo.http

import com.a7tv.codingchallenge.codingchallengefor7tv.util.HttpException
import com.a7tv.codingchallenge.codingchallengefor7tv.util.typeclasses.Try
import io.reactivex.Single
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class SimpleHttpClient : HttpClientInterface {

    private companion object {
        const val GET_REQ_METHOD = "GET"
        const val CONTENT_TYPE_PROPERTY_KEY = "Content-Type"
        const val CONTENT_TYPE_VAlUE_JSON = "application/json"
    }

    override fun getJsonFrom(url: URL) = singleFromSafeCallable {
        val connection = openHttpUrlConnection(url)
        when (connection.responseCode) {
            HttpURLConnection.HTTP_OK -> Try.just(readContentFrom(connection))
            else -> Try.Failure(HttpException(connection.responseCode))
        }
    }

    private fun readContentFrom(connection: HttpURLConnection) =
            with(connection.inputStream) {
                val bufferedReader = BufferedReader(InputStreamReader(this))
                bufferedReader.readToString()
            }

    private fun openHttpUrlConnection(url: URL) =
            (url.openConnection() as HttpURLConnection).apply {
                requestMethod = GET_REQ_METHOD
                setRequestProperty(CONTENT_TYPE_PROPERTY_KEY, CONTENT_TYPE_VAlUE_JSON)
            }.also { connection ->
                connection.connect()
            }

    private tailrec fun BufferedReader.readToString(line: String? = "",
                                                    stringBuffer: StringBuffer = StringBuffer()): String =
            when (line) {
                null -> stringBuffer.toString()
                else -> readToString(readLine(), stringBuffer.append(line))
            }

    private fun <T> singleFromSafeCallable(f: () -> Try<T>) = Single.fromCallable {
        try {
            f()
        } catch (e: Exception) {
            Try.Failure(e)
        }
    }

}