package com.teblung.dicodingstory.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote")
class RemoteEntity(
    @PrimaryKey val id: String,
    val prevKey: Int?,
    val nextKey: Int?
)