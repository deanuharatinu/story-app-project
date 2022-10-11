package com.deanu.storyapp.common.data.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import com.deanu.storyapp.common.data.cache.daos.RemoteKeysDao
import com.deanu.storyapp.common.data.cache.daos.StoryAppDao
import com.deanu.storyapp.common.data.cache.model.CachedStory
import com.deanu.storyapp.common.data.cache.model.RemoteKeys

@Database(
    entities = [CachedStory::class, RemoteKeys::class],
    version = 1,
    exportSchema = true
)
abstract class StoryAppDatabase : RoomDatabase() {
    abstract fun storyAppDao(): StoryAppDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}