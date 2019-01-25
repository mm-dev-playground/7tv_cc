package com.a7tv.codingchallenge.codingchallengefor7tv

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.a7tv.codingchallenge.codingchallengefor7tv.domain.GitHubUserListViewModel
import com.a7tv.codingchallenge.codingchallengefor7tv.view.GitHubUserListAdapter
import kotlinx.android.synthetic.main.activity_app_entry.*

class AppEntryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_entry)

        val viewModel = ViewModelProviders.of(this).get(GitHubUserListViewModel::class.java)

        val listAdapter = GitHubUserListAdapter()

        viewModel.userListLiveData.observe(this, Observer {
            listAdapter.submitList(it)
        })

        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = listAdapter

    }
}
