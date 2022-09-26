package com.deanu.storyapp.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.deanu.storyapp.common.domain.model.Story
import com.deanu.storyapp.databinding.ItemStoryBinding

class StoryAdapter : ListAdapter<Story, StoryAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(story: Story) {
            // TODO: nanti ganti pake placeholder
            binding.tvUsername.text = "Uploaded by ${story.name}"
            // TODO: nanti bikin loadingnya 
            Glide.with(binding.root)
                .load(story.photoUrl)
                .into(binding.ivPhoto)
        }

        companion object {
            fun inflateFrom(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemStoryBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.inflateFrom(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = getItem(position)
        holder.bind(story)
    }

    class DiffCallback : DiffUtil.ItemCallback<Story>() {
        override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
            return (oldItem.photoUrl == newItem.photoUrl && oldItem.name == newItem.name)
        }
    }
}