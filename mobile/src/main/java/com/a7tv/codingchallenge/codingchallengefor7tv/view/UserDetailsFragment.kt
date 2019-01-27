package com.a7tv.codingchallenge.codingchallengefor7tv.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.a7tv.codingchallenge.codingchallengefor7tv.R
import com.a7tv.codingchallenge.codingchallengefor7tv.domain.GitHubUserDetailsViewModel
import com.a7tv.codingchallenge.codingchallengefor7tv.domain.GitHubUserDetailsViewModelFactory
import com.a7tv.codingchallenge.codingchallengefor7tv.model.GitHubUserProfile
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.GitHubRepoDataFactory
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.http.SimpleHttpClient
import com.squareup.picasso.Picasso
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.user_details_fragment.*
import java.net.URL

class UserDetailsFragment : Fragment() {

    companion object {
        const val BUNDLE_KEY_USER_PROFILE_URL = "user_profile_url"
        const val BUNDLE_KEY_USER_REPO_URL = "user_repo_url"
    }

    private var userProfileUrl: String? = null
    private var userRepoUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userProfileUrl = it.getString(BUNDLE_KEY_USER_PROFILE_URL)
            userRepoUrl = it.getString(BUNDLE_KEY_USER_REPO_URL)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.user_details_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        userProfileUrl?.let { url -> // we do not check for a valid repo url since it is less important for the user

            val repoListAdapter = RepositoryListAdapter().also {
                repositories_recycler_view.layoutManager = LinearLayoutManager(this@UserDetailsFragment.context)
                repositories_recycler_view.adapter = it
            }

            val viewModel = createViewModel(url)

            viewModel.userDetailsLiveData.observe(viewLifecycleOwner, Observer { profileInfo ->
                progress_bar.visibility = View.INVISIBLE
                bindUserInfoToViews(profileInfo)
            })

            viewModel.repoLiveData.observe(viewLifecycleOwner, Observer { pagedGitHubRepoList ->
                repoListAdapter.submitList(pagedGitHubRepoList)
            })
        } ?: { Log.e(javaClass.simpleName, "User profile URL is not available") }()
    }

    private fun createViewModel(url: String): GitHubUserDetailsViewModel {
        val pagedListConfig = PagedList.Config.Builder()
                .setPrefetchDistance(1)
                .setPageSize(20) // 20 as param should be evaluated by the UX department
                .build()
        val repositoriesDataFactory = GitHubRepoDataFactory(
                URL(userRepoUrl ?: "")
        )
        val livePagedListBuilder = LivePagedListBuilder(repositoriesDataFactory, pagedListConfig)
        val viewModelFactory = GitHubUserDetailsViewModelFactory(
                url, SimpleHttpClient(), Schedulers.io(), livePagedListBuilder
        )
        return ViewModelProviders.of(this@UserDetailsFragment, viewModelFactory)
                .get(GitHubUserDetailsViewModel::class.java)
    }

    private fun bindUserInfoToViews(profileInfo: GitHubUserProfile) {
        Picasso.get().load(profileInfo.avatarUrl).into(avatar_image_view)
        user_name_text_view.text =
                getString(R.string.user_name, profileInfo.name.withNullPlaceholder())
        follower_count_text_view.text =
                getString(R.string.followers_count,
                        profileInfo.followers.toString().withNullPlaceholder())
        followings_count_text_view.text =
                getString(R.string.followings_count,
                        profileInfo.following.toString().withNullPlaceholder())
        company_text_view.text =
                getString(R.string.company, profileInfo.company.withNullPlaceholder())
        location_text_view.text =
                getString(R.string.location, profileInfo.location.withNullPlaceholder())
    }

    private fun String?.withNullPlaceholder() = this ?: "n/a"
}
