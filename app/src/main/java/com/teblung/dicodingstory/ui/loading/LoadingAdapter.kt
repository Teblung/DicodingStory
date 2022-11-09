package com.teblung.dicodingstory.ui.loading

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teblung.dicodingstory.databinding.ActivityLoadingBinding

class LoadingAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<LoadingAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ActivityLoadingBinding, retry: () -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.btnRetry.setOnClickListener { retry.invoke() }
        }

        fun bind(state: LoadState) {
            if (state is LoadState.Error) {
                Toast.makeText(itemView.context, state.error.localizedMessage, Toast.LENGTH_LONG)
                    .show()
            }
            binding.apply {
                progressBar.isVisible = state is LoadState.Loading
                btnRetry.isVisible = state is LoadState.Error
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder {
        val binding =
            ActivityLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, retry)
    }
}