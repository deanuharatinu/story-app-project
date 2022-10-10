package com.deanu.storyapp.home

import android.animation.ObjectAnimator
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.deanu.storyapp.R
import com.deanu.storyapp.common.data.cache.model.CachedStory
import com.deanu.storyapp.common.domain.model.Story
import com.deanu.storyapp.databinding.ItemStoryBinding

class StoryAdapter constructor(
    private val viewModel: HomeViewModel,
    private val clickListener: (story: Story, binding: ItemStoryBinding) -> Unit
) : PagingDataAdapter<CachedStory, StoryAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(
        private val binding: ItemStoryBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            story: Story,
            viewModel: HomeViewModel,
            position: Int,
            clickListener: (story: Story, binding: ItemStoryBinding) -> Unit
        ) {
            binding.cvStory.setOnClickListener {
                clickListener(story, binding)
                viewModel.setAdapterPosition(position)
            }
            binding.tvUsername.text = binding.root.context.getString(
                R.string.uploaded_by,
                story.name
            )
            binding.tvUsername.transitionName = story.id

            Glide.with(binding.root)
                .load(story.photoUrl)
                .placeholder(R.drawable.ic_default_photo_300)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        animatePhoto()
                        return false
                    }

                })
                .into(binding.ivPhoto)
        }

        private fun animatePhoto() {
            val objectAnimator = ObjectAnimator.ofFloat(
                binding.ivPhoto,
                "alpha",
                0f,
                1f
            )
            objectAnimator.duration = 500
            objectAnimator.start()
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
        story?.let {
            holder.bind(it.toDomain(), viewModel, position, clickListener)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<CachedStory>() {
        override fun areItemsTheSame(oldItem: CachedStory, newItem: CachedStory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CachedStory, newItem: CachedStory): Boolean {
            return (oldItem.photoUrl == newItem.photoUrl && oldItem.name == newItem.name)
        }
    }
}