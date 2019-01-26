package com.a7tv.codingchallenge.codingchallengefor7tv.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.a7tv.codingchallenge.codingchallengefor7tv.R
import com.a7tv.codingchallenge.codingchallengefor7tv.model.GitHubUser

class GitHubUserListAdapter :
        PagedListAdapter<GitHubUser, RecyclerView.ViewHolder>(GitHubUserItemComparator) {

    private object GitHubUserItemComparator : DiffUtil.ItemCallback<GitHubUser>() {
        override fun areItemsTheSame(oldItem: GitHubUser, newItem: GitHubUser): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: GitHubUser, newItem: GitHubUser): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.user_list_entry, parent, false)
        return UserListEntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as UserListEntryViewHolder).textView.text = getItem(position)?.login // TODO nullable - not pretty!
    }

    private data class UserListEntryViewHolder(val view: View) :
            RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.txt)
    }

}