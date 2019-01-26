package com.a7tv.codingchallenge.codingchallengefor7tv

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.annotation.IntRange
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.a7tv.codingchallenge.codingchallengefor7tv.domain.GitHubUserListViewModel
import com.a7tv.codingchallenge.codingchallengefor7tv.domain.GitHubUserListViewModelFactory
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.GitHubDataFactory
import com.a7tv.codingchallenge.codingchallengefor7tv.repo.GitHubDataSource
import com.a7tv.codingchallenge.codingchallengefor7tv.view.GitHubUserListAdapter
import kotlinx.android.synthetic.main.activity_app_entry.*

class AppEntryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_entry)

         initializeListDataAdapter().run {
            initAndConnectViewModel(this)
        }
    }

    private fun createViewModel(): GitHubUserListViewModel {
        val dataFactory = GitHubDataFactory()
        val pagedListConfig = PagedList.Config.Builder()
                .setPrefetchDistance(1)
                .setPageSize(20) // TODO extract magic number
                .build()
        val viewModelFactory = GitHubUserListViewModelFactory(
                dataFactory,
                LivePagedListBuilder(dataFactory, pagedListConfig)
        )
        return ViewModelProviders.of(
                this,
                viewModelFactory
        ).get(GitHubUserListViewModel::class.java)
    }

    private fun initAndConnectViewModel(listAdapter: GitHubUserListAdapter) =
            createViewModel().run {
                loadingStateData.observe(this@AppEntryActivity, Observer { loadingState ->
                    Log.d(javaClass.simpleName, "new loading state: $loadingState")
                    applyLoadingState(loadingState)
                })
                usersLiveData.observe(this@AppEntryActivity, Observer { userList ->
                    listAdapter.submitList(userList)
                })
                search_edit_text.addTextChangedListener(object: TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        s?.let {
                            this@run.searchTextEdited(it.toString())
                        }
                    }
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
                })
            }

    private fun initializeListDataAdapter() =
            GitHubUserListAdapter().also {
                recycler_view.layoutManager = LinearLayoutManager(this@AppEntryActivity)
                recycler_view.adapter = it
            }

    private fun applyLoadingState(@IntRange(from=-1, to=2) loadingState: Int) {
        when (loadingState) {
            GitHubDataSource.State.INIT -> userListInitializing()
            GitHubDataSource.State.LOADING -> userListIsLoading()
            GitHubDataSource.State.LOADED -> userListLoaded()
            GitHubDataSource.State.ERROR -> userListLoadingFailed()
            3 -> {
                //listAdapter.notifyDataSetChanged()
            }
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
