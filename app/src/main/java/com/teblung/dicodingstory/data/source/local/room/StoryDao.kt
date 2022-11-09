package com.teblung.dicodingstory.data.source.local.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.teblung.dicodingstory.data.source.remote.response.StoryResponse

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(storyList: List<StoryResponse>)

    @Query("SELECT * FROM storyresponse")
    fun getAllStory(): PagingSource<Int, StoryResponse>

    @Query("DELETE FROM storyresponse")
    suspend fun deleteAllStory()
}