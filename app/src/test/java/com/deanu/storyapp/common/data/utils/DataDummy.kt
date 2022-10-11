package com.deanu.storyapp.common.data.utils

import com.deanu.storyapp.common.data.api.model.ApiStory
import com.deanu.storyapp.common.data.cache.model.CachedStory

object DataDummy {
    fun generateDummy(): List<CachedStory> {
        val items: MutableList<CachedStory> = arrayListOf()
        for (i in 0..20) {
            val story = CachedStory(
                i.toString(),
                "username $i",
                "description $i",
                "photoUrl_$i",
                "quote $i",
                0.0,
                0.0
            )
            items.add(story)
        }
        return items
    }

    fun generateApiStory(): List<ApiStory> {
        val items: MutableList<ApiStory> = arrayListOf()
        for (i in 0..20) {
            val story = ApiStory(
                i.toString(),
                "username $i",
                "description $i",
                "photoUrl_$i",
                "quote $i",
                0.0,
                0.0
            )
            items.add(story)
        }
        return items
    }
}