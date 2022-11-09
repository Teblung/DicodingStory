package com.teblung.dicodingstory.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.teblung.dicodingstory.data.source.local.entity.RemoteEntity
import com.teblung.dicodingstory.data.source.local.preference.SessionUser
import com.teblung.dicodingstory.data.source.local.room.Database
import com.teblung.dicodingstory.data.source.remote.response.StoryResponse
import com.teblung.dicodingstory.data.source.remote.service.ApiService
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalPagingApi::class)
class RemoteMediator(
    private val database: Database,
    private val apiService: ApiService,
    private val preference: SessionUser
) : RemoteMediator<Int, StoryResponse>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryResponse>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKey = getRemoteKeyClosestToCurrentPosition(state)
                remoteKey?.nextKey?.minus(1) ?: 1
            }
            LoadType.PREPEND -> {
                val remoteKey = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKey?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKey != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKey = getRemoteKeyForLastItem(state)
                val nextKey = remoteKey?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKey != null)
                nextKey
            }
        }

        try {
            val token: String = preference.getLoginData().first().token

            val response =
                apiService.getAllStory("Bearer $token", page, state.config.pageSize).listStory

            val endPage = response.isEmpty()
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteDao().deleteAllRemote()
                    database.storyDao().deleteAllStory()
                }

                val previousKey = if (page == 1) null else page - 1
                val nextKey = if (endPage) null else page + 1

                val key = response.map {
                    RemoteEntity(
                        id = it.id,
                        prevKey = previousKey,
                        nextKey = nextKey
                    )
                }

                database.remoteDao().insertAllData(key)
                database.storyDao().insertStory(response)
            }
            return MediatorResult.Success(endOfPaginationReached = endPage)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryResponse>): RemoteEntity? {
        return state.pages.lastOrNull()?.data?.lastOrNull()?.id?.let {
            database.remoteDao().getRemoteId(it)
        }
    }


    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryResponse>): RemoteEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let {
            database.remoteDao().getRemoteId(it.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoryResponse>): RemoteEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.last()?.id?.let {
            database.remoteDao().getRemoteId(it)
        }
    }
}