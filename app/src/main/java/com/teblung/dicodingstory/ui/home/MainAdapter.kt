package com.teblung.dicodingstory.ui.home

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.teblung.dicodingstory.R
import com.teblung.dicodingstory.data.source.remote.response.StoryResponse
import com.teblung.dicodingstory.databinding.ItemStoryBinding
import com.teblung.dicodingstory.ui.home.detail.DetailActivity

class MainAdapter : PagingDataAdapter<StoryResponse, MainAdapter.ViewHolder>(storyCallback) {

    inner class ViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: StoryResponse) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(data.photoUrl)
                    .apply(
                        RequestOptions.placeholderOf(R.drawable.icon_dicoding)
                            .error(R.drawable.ic_baseline_error_48)
                    )
                    .into(imgStory)
                tvNameStory.text = data.name
                itemView.setOnClickListener {
                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            Pair(imgStory, "photo"),
                            Pair(tvNameStory, "name")
                        )
                    itemView.context.startActivity(
                        Intent(
                            itemView.context, DetailActivity::class.java
                        ).apply {
                            putExtra("IMAGE", data.photoUrl)
                            putExtra("NAME", data.name)
                            putExtra("DESC", data.description)
                        }, optionsCompat.toBundle()
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainAdapter.ViewHolder {
        val view = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainAdapter.ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    companion object {
        val storyCallback = object : DiffUtil.ItemCallback<StoryResponse>() {
            override fun areItemsTheSame(oldItem: StoryResponse, newItem: StoryResponse): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: StoryResponse,
                newItem: StoryResponse
            ): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }

}