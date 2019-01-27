package com.a7tv.codingchallenge.codingchallengefor7tv.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.a7tv.codingchallenge.codingchallengefor7tv.R
import com.a7tv.codingchallenge.codingchallengefor7tv.domain.GitHubUserDetailsViewModel
import com.a7tv.codingchallenge.codingchallengefor7tv.domain.GitHubUserDetailsViewModelFactory
import com.a7tv.codingchallenge.codingchallengefor7tv.model.GitHubUserProfile
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.http.SimpleHttpClient
import com.squareup.picasso.Picasso
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.user_details_fragment.*

class UserDetailsFragment : Fragment() {

    companion object {
        const val BUNDLE_KEY_USER_PROFILE_URL = "user_profile_url"
    }

    private var userProfileUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userProfileUrl = it.getString(BUNDLE_KEY_USER_PROFILE_URL)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.user_details_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        userProfileUrl?.let { url ->
            val viewModel = createViewModel(url)
            viewModel.userDetailsLiveData.observe(viewLifecycleOwner, Observer { profileInfo ->
                progress_bar.visibility = View.INVISIBLE
                bindUserInfoToViews(profileInfo)
            })
        } ?: { Log.e(javaClass.simpleName, "User profile URL is not available") }()
    }

    private fun createViewModel(url: String): GitHubUserDetailsViewModel {
        val viewModelFactory = GitHubUserDetailsViewModelFactory(
                url, SimpleHttpClient(), Schedulers.io()
        )
        return ViewModelProviders.of(this@UserDetailsFragment, viewModelFactory)
                .get(GitHubUserDetailsViewModel::class.java)
    }

    private fun bindUserInfoToViews(profileInfo: GitHubUserProfile) {
        Picasso.get().load(profileInfo.avatarUrl).into(avatar_image_view)
        user_name_text_view.text = getString(R.string.user_name, profileInfo.name)
        follower_count_text_view.text = getString(R.string.followers_count,
                profileInfo.followers.toString())
        followings_count_text_view.text = getString(R.string.followings_count,
                profileInfo.following.toString())
        company_text_view.text = getString(R.string.company, profileInfo.company)
        location_text_view.text = getString(R.string.location, profileInfo.location)
    }
}
