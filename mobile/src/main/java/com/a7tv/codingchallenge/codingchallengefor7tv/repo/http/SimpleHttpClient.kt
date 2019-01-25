package com.a7tv.codingchallenge.codingchallengefor7tv.repo.http

import com.a7tv.codingchallenge.codingchallengefor7tv.util.HttpException
import com.a7tv.codingchallenge.codingchallengefor7tv.util.typeclasses.Option
import com.a7tv.codingchallenge.codingchallengefor7tv.util.typeclasses.Try
import io.reactivex.Single
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class SimpleHttpClient : HttpClientInterface {

    private companion object {
        const val GET_REQ_METHOD = "GET"
        const val CONTENT_TYPE_PROPERTY_KEY = "Content-Type"
        const val CONTENT_TYPE_VAlUE_JSON = "application/json"
    }

    private var currentUrlConnectionOption: Option<HttpURLConnection> = Option.None
    private val urlConnectionLock = ReentrantLock()

    override fun getJsonFrom(url: URL) = Single.fromCallable {
        try {
            val connection = createAndRegisterUrlConnection(url)
            when (connection.responseCode) {
                HttpURLConnection.HTTP_OK ->
                    Try.just(
                            HttpGetAnswer(connection.headerFields, readContentFrom(connection))
                    )
                else -> Try.Failure(HttpException(connection.responseCode))
            }
        } catch (e: Exception) {
            Try.Failure(e)
        } finally {
            disconnectRegisteredUrlConnection()
        }
    }

    override fun cancelRunningRequest() {
        disconnectRegisteredUrlConnection()
    }

    private fun disconnectRegisteredUrlConnection() {
        urlConnectionLock.withLock {
            currentUrlConnectionOption.map {
                it.disconnect()
            }
            currentUrlConnectionOption = Option.None
        }
    }

    private fun createAndRegisterUrlConnection(url: URL): HttpURLConnection {
        val connection = openHttpUrlConnection(url)
        urlConnectionLock.withLock {
            currentUrlConnectionOption = Option.just(connection)
        }
        return connection
    }

    private fun readContentFrom(connection: HttpURLConnection) =
            with(connection.inputStream) {
                connection.headerFields.forEach {
                    println(it)
                }
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

}