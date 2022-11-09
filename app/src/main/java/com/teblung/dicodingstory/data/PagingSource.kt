package com.teblung.dicodingstory.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.teblung.dicodingstory.data.source.local.preference.SessionUser
import com.teblung.dicodingstory.data.source.remote.response.StoryResponse
import com.teblung.dicodingstory.data.source.remote.service.ApiService
import kotlinx.coroutines.flow.first

class PagingSource(private val preference: SessionUser, private val apiService: ApiService) :
    PagingSource<Int, StoryResponse>() {
    override fun getRefreshKey(state: PagingState<Int, StoryResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryResponse> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val token = preference.getLoginData().first().token
            val responseData = apiService.getAllStory("Bearer $token", position, params.loadSize)
            LoadResult.Page(
                data = responseData.listStory,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.listStory.isEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}