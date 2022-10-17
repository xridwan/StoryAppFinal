package com.xridwan.mystoryfinal.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.xridwan.mystoryfinal.R
import com.xridwan.mystoryfinal.databinding.ItemStoryBinding
import com.xridwan.mystoryfinal.data.network.response.ListStoryItem

class StoryAdapter(
    private val listener: Listener
) : PagingDataAdapter<ListStoryItem, StoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
            holder.itemView.setOnClickListener {
                listener.onListener(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    class MyViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListStoryItem) {
            binding.ivStory.load(item.photoUrl) {
                crossfade(true)
                crossfade(500)
                placeholder(android.R.color.darker_gray)
                error(R.drawable.ic_placeholder)
            }
            binding.tvStoryName.text = item.name
        }
    }

    interface Listener {
        fun onListener(story: ListStoryItem)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}