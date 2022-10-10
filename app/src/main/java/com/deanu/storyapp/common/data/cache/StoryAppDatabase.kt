package com.deanu.storyapp.common.data.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import com.deanu.storyapp.common.data.cache.daos.StoryAppDao
import com.deanu.storyapp.common.data.cache.model.CachedStory

@Database(
    entities = [CachedStory::class],
    version = 1,
    exportSchema = false
)
abstract class StoryAppDatabase : RoomDatabase() {
    abstract fun storyAppDao(): StoryAppDao
}