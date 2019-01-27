package com.a7tv.codingchallenge.codingchallengefor7tv.util

object GitHubApiConstants {
    const val BASE_URL = "https://api.github.com"
    const val SEARCH_ENDPOINT = "/search"
    const val USERS_ENDPOINT = "/users"
    fun buildSinceParam(userId: Long) = "?since=$userId"
    fun buildQueryParam(query: String) = "?q=$query"
    fun buildPagedQueryParam(query: String, pageId: Long) = buildQueryParam(query) + "&page=$pageId"
}