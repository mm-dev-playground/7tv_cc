package com.a7tv.codingchallenge.codingchallengefor7tv.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

data class GitHubRepository(
        @Json(name = "full_name")
        val fullName: String? = ""
) {

    companion object {
        val jsonListAdapter: JsonAdapter<List<GitHubRepository>> = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
                .adapter<List<GitHubRepository>>(
                        Types.newParameterizedType(List::class.java, GitHubRepository::class.java)
                )
    }

}