package com.deanu.storyapp.common.data.cache.daos

import androidx.paging.PagingSource
import androidx.room.*
import com.deanu.storyapp.common.data.cache.model.CachedStory

@Dao
interface StoryAppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(storyList: List<CachedStory>)

    @Query("SELECT * FROM story")
    fun getAllStory(): PagingSource<Int, CachedStory>

    @Query("DELETE FROM story")
    suspend fun deleteAllStory()

    @Transaction
    suspend fun deleteAndInsertStory(storyList: List<CachedStory>) {
        deleteAllStory()
        insertStory(storyList)
    }

    @Query("SELECT * FROM story")
    suspend fun getStories(): List<CachedStory>
}