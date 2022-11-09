package com.teblung.dicodingstory.data.source.local.room

import androidx.room.*
import com.teblung.dicodingstory.data.source.local.entity.RemoteEntity

@Dao
interface RemoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllData(remoteEntity: List<RemoteEntity>)

    @Query("SELECT * FROM remote WHERE id = :id")
    suspend fun getRemoteId(id: String): RemoteEntity?

    @Delete
    suspend fun deleteRemote(remoteEntity: RemoteEntity)

    @Query("DELETE FROM remote")
    suspend fun deleteAllRemote()
}