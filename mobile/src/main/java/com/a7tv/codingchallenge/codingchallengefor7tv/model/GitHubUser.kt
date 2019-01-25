package com.a7tv.codingchallenge.codingchallengefor7tv.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

data class GitHubUser(
        @Json(name = "avatar_url")
        val avatarUrl: String,
        @Json(name = "events_url")
        val eventsUrl: String,
        @Json(name = "followers_url")
        val followersUrl: String,
        @Json(name = "following_url")
        val followingUrl: String,
        @Json(name = "gists_url")
        val gistsUrl: String,
        @Json(name = "gravatar_id")
        val gravatarId: String,
        @Json(name = "html_url")
        val htmlUrl: String,
        @Json(name = "id")
        val id: Int,
        @Json(name = "login")
        val login: String,
        @Json(name = "node_id")
        val nodeId: String,
        @Json(name = "organizations_url")
        val organizationsUrl: String,
        @Json(name = "received_events_url")
        val receivedEventsUrl: String,
        @Json(name = "repos_url")
        val reposUrl: String,
        @Json(name = "site_admin")
        val siteAdmin: Boolean,
        @Json(name = "starred_url")
        val starredUrl: String,
        @Json(name = "subscriptions_url")
        val subscriptionsUrl: String,
        @Json(name = "type")
        val type: String,
        @Json(name = "url")
        val url: String
) {

    companion object {
        val jsonListAdapter: JsonAdapter<List<GitHubUser>> = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
                .adapter<List<GitHubUser>>(
                        Types.newParameterizedType(List::class.java, GitHubUser::class.java)
                )
    }

}