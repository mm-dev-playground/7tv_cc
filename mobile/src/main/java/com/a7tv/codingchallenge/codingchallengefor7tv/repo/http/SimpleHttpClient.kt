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
        const val HTTP_RESP_OK_CODE = 200
    }

    override fun get(url: URL): Single<Try<String>> {
        return Single.fromCallable {
            try {
                val connection = openHttpUrlConnection(url)
                when (connection.responseCode) {
                    HTTP_RESP_OK_CODE ->Try.just(readContent(connection))
                    else -> Try.Failure(HttpException(connection.responseCode))
                }

            } catch (e: Exception) {
                Try.Failure(e)
            }
        }
    }

    private fun readContent(connection: HttpURLConnection): String {
        val content = with(connection.inputStream) {
            val bufferedReader = BufferedReader(InputStreamReader(this))
            bufferedReader.readToString()
        }
        return content
    }

    private fun openHttpUrlConnection(url: URL): HttpURLConnection {
        return (url.openConnection() as HttpURLConnection).apply {
            requestMethod = GET_REQ_METHOD
            setRequestProperty(CONTENT_TYPE_PROPERTY_KEY, CONTENT_TYPE_VAlUE_JSON)
        }.also { connection ->
            connection.connect()
        }
    }

    private tailrec fun BufferedReader.readToString(line: String? = "",
                                              stringBuffer: StringBuffer = StringBuffer()): String {
        return when (line) {
            null -> stringBuffer.toString()
            else -> readToString(readLine(), stringBuffer.append(line))
        }
    }

}