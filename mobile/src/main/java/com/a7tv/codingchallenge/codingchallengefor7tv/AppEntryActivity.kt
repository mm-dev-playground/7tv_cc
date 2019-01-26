package com.a7tv.codingchallenge.codingchallengefor7tv

import android.os.Bundle
import android.view.View
import androidx.annotation.IntRange
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

        val listAdapter = initializeListDataAdapter()
        initializeViewModel(listAdapter)
    }

    private fun initializeViewModel(listAdapter: GitHubUserListAdapter) =
            ViewModelProviders.of(this).get(GitHubUserListViewModel::class.java).run {
                this.loadingStateData.observe(this@AppEntryActivity, Observer { loadingState ->
                    applyLoadingState(loadingState)
                })
                userListLiveData.observe(this@AppEntryActivity, Observer { userList ->
                    listAdapter.submitList(userList)
                })
            }

    private fun initializeListDataAdapter() =
            GitHubUserListAdapter().run {
                recycler_view.layoutManager = LinearLayoutManager(this@AppEntryActivity)
                recycler_view.adapter = this
                this
            }

    private fun applyLoadingState(@IntRange(from=-1, to=2) loadingState: Int) {
        when (loadingState) {
            GithubDataSource.State.INIT -> userListInitializing()
            GithubDataSource.State.LOADING -> userListIsLoading()
            GithubDataSource.State.LOADED -> userListLoaded()
            GithubDataSource.State.ERROR -> userListLoadingFailed()
            //else -> Log.e(javaClass.simpleName, "Loading state not handled: $loadingState")
        }
    }

    private fun userListInitializing() {
        loading_indicator.visibility = View.VISIBLE
    }

    private fun userListIsLoading() {
        loading_indicator.visibility = View.VISIBLE
        recycler_view.alpha = 0.25f // TODO extract magic number
    }

    private fun userListLoaded() {
        loading_indicator.visibility = View.INVISIBLE
        recycler_view.alpha = 1.0f // TODO extract magic number
    }

    private fun userListLoadingFailed() {
        loading_indicator.visibility = View.INVISIBLE
        recycler_view.visibility = View.INVISIBLE
        error_txt.visibility = View.VISIBLE
    }

}
