package com.teblung.dicodingstory.data.source.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.teblung.dicodingstory.data.source.local.entity.RemoteEntity
import com.teblung.dicodingstory.data.source.remote.response.StoryResponse

@Database(entities = [StoryResponse::class, RemoteEntity::class], version = 1, exportSchema = false)
abstract class Database : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun remoteDao(): RemoteDao
}