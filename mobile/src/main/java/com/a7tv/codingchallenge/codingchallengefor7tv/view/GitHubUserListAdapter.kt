package com.a7tv.codingchallenge.codingchallengefor7tv.view

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.a7tv.codingchallenge.codingchallengefor7tv.R
import com.a7tv.codingchallenge.codingchallengefor7tv.model.GitHubUser
import com.squareup.picasso.Picasso

class GitHubUserListAdapter(private val userTappedCallback: (GitHubUser) -> Unit) :
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
        return UserListEntryViewHolder(view, userTappedCallback)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        with(getItem(position)) {
            when (this) {
                null -> Log.e(javaClass.simpleName, "No user found at position: $position") // TODO signal error
                else -> {
                    holder as UserListEntryViewHolder
                    holder.model = this
                    holder.textView.text = this.login
                    Picasso.get().load(avatarUrl).into(holder.imageView)
                }
            }
        }
    }

    private data class UserListEntryViewHolder(val view: View,
                                               val userTappedCallback: (GitHubUser) -> Unit) :
            RecyclerView.ViewHolder(view) {

        val textView: TextView = view.findViewById(R.id.user_name)
        val imageView: ImageView = view.findViewById(R.id.avatar_thumbnail)
        var model: GitHubUser? = null

        init {
            view.setOnClickListener {
                model?.let { nonNullModel -> userTappedCallback(nonNullModel) }
            }
        }
    }

}