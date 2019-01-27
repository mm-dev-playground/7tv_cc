package com.a7tv.codingchallenge.codingchallengefor7tv.view

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.a7tv.codingchallenge.codingchallengefor7tv.R
import com.a7tv.codingchallenge.codingchallengefor7tv.model.GitHubRepository

class RepositoryListAdapter :
        PagedListAdapter<GitHubRepository, RecyclerView.ViewHolder>(StringItemComparator) {

    private object StringItemComparator : DiffUtil.ItemCallback<GitHubRepository>() {
        override fun areItemsTheSame(oldItem: GitHubRepository, newItem: GitHubRepository): Boolean {
            return oldItem === newItem
        }
        override fun areContentsTheSame(oldItem: GitHubRepository, newItem: GitHubRepository): Boolean {
            return oldItem.fullName == newItem.fullName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.repository_list_entry, parent, false)
        return RepositoryEntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        with(getItem(position)) {
            when (this) {
                null -> Log.e(javaClass.simpleName, "No repository found at position: $position") // TODO signal error
                else -> {
                    holder as RepositoryEntryViewHolder
                    holder.textView.text = this.fullName ?: "n/a"
                }
            }
        }
    }

    private class RepositoryEntryViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.repository_name_text_view)
    }

}