package com.a7tv.codingchallenge.codingchallengefor7tv.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

data class GitHubSearchResult(
        @Json(name = "incomplete_results")
        val incompleteResults: Boolean,
        @Json(name = "items")
        val items: List<GitHubUser>,
        @Json(name = "total_count")
        val totalCount: Int
) {

    companion object {
        val jsonAdapter: JsonAdapter<GitHubSearchResult> = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
                .adapter<GitHubSearchResult>(GitHubSearchResult::class.java)
    }

}