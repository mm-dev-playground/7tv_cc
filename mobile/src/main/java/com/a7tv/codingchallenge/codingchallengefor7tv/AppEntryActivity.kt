package com.a7tv.codingchallenge.codingchallengefor7tv

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.a7tv.codingchallenge.codingchallengefor7tv.domain.GitHubUserListViewModel
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.GithubDataSource
import com.a7tv.codingchallenge.codingchallengefor7tv.view.GitHubUserListAdapter
import kotlinx.android.synthetic.main.activity_app_entry.*

class AppEntryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_entry)

        val viewModel = ViewModelProviders.of(this).get(GitHubUserListViewModel::class.java)

        viewModel.loadingStateData.observe(this, Observer {
            applyLoadingState(it)
        })

        val listAdapter = GitHubUserListAdapter()

        viewModel.userListLiveData.observe(this, Observer {
            listAdapter.submitList(it)
        })

        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = listAdapter
    }

    private fun applyLoadingState(loadingState: Int) {
        when (loadingState) {
            GithubDataSource.State.INIT -> userListInitializing()
            GithubDataSource.State.LOADING -> userListIsLoading()
            GithubDataSource.State.LOADED -> userListLoaded()
            GithubDataSource.State.ERROR -> userListLoadingFailed()
            else -> Log.e(javaClass.simpleName, "Loading state not handled: $loadingState")
        }
    }

    private fun userListInitializing() {
        loading_indicator.visibility = View.VISIBLE
    }

    private fun userListIsLoading() {
        loading_indicator.visibility = View.VISIBLE
        recycler_view.alpha = 0.25f
    }

    private fun userListLoaded() {
        loading_indicator.visibility = View.INVISIBLE
        recycler_view.alpha = 1.0f
    }

    private fun userListLoadingFailed() {
        loading_indicator.visibility = View.INVISIBLE
        recycler_view.visibility = View.INVISIBLE
        error_txt.visibility = View.VISIBLE
    }

}
