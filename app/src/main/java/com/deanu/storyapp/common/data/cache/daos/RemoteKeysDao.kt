package com.deanu.storyapp.common.data.cache.daos

import androidx.room.*
import com.deanu.storyapp.common.data.cache.model.RemoteKeys

@Dao
interface RemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRemoteKeys(remoteKeys: List<RemoteKeys>)

    @Query("SELECT * FROM remote_keys WHERE id = :id")
    suspend fun getRemoteKeysId(id: String): RemoteKeys?

    @Query("DELETE FROM remote_keys")
    suspend fun deleteRemoteKeys()

    @Transaction
    suspend fun deleteAndInsertRemoteKeys(remoteKeys: List<RemoteKeys>) {
        deleteRemoteKeys()
        insertRemoteKeys(remoteKeys)
    }
}