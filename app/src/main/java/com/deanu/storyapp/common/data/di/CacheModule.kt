package com.deanu.storyapp.common.data.di

import android.content.Context
import androidx.room.Room
import com.deanu.storyapp.common.data.cache.Cache
import com.deanu.storyapp.common.data.cache.RoomCache
import com.deanu.storyapp.common.data.cache.StoryAppDatabase
import com.deanu.storyapp.common.data.cache.daos.RemoteKeysDao
import com.deanu.storyapp.common.data.cache.daos.StoryAppDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CacheModule {
    @Binds
    abstract fun bindCache(cache: RoomCache): Cache

    companion object {
        private const val DATABASE_NAME = "STORY_APP_DB"

        @Provides
        @Singleton
        fun provideDatabase(@ApplicationContext context: Context): StoryAppDatabase {
            return Room.databaseBuilder(
                context,
                StoryAppDatabase::class.java,
                DATABASE_NAME
            ).fallbackToDestructiveMigration().build()
        }

        @Provides
        fun provideStoryAppDao(storyAppDatabase: StoryAppDatabase): StoryAppDao {
            return storyAppDatabase.storyAppDao()
        }

        @Provides
        fun provideRemoteKeysDao(storyAppDatabase: StoryAppDatabase): RemoteKeysDao {
            return storyAppDatabase.remoteKeysDao()
        }
    }
}