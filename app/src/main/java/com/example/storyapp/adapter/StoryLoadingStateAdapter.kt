package com.example.storyapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.storyapp.databinding.LoadingStateBinding

class StoryLoadingStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<StoryLoadingStateAdapter.LoadingStateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadingStateViewHolder {
        return LoadingStateViewHolder(
            LoadingStateBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: LoadingStateViewHolder, loadState: LoadState) {
        if (loadState is LoadState.Loading) {
            holder.binding.progressBar.visibility = View.VISIBLE
            holder.binding.btnRetry.visibility = View.INVISIBLE
            holder.binding.tvErrorMessage.visibility = View.INVISIBLE
        } else {
            holder.binding.progressBar.visibility = View.INVISIBLE
        }

        if (loadState is LoadState.Error) {
            holder.binding.tvErrorMessage.text = loadState.error.localizedMessage
            holder.binding.tvErrorMessage.visibility = View.VISIBLE
            holder.binding.btnRetry.visibility = View.VISIBLE
        }

        holder.binding.btnRetry.setOnClickListener {
            retry.invoke()
        }
    }

    class LoadingStateViewHolder(val binding: LoadingStateBinding): RecyclerView.ViewHolder(binding.root)
}