package com.teblung.dicodingstory.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import com.google.common.truth.Truth.assertThat
import com.teblung.dicodingstory.data.Repository
import com.teblung.dicodingstory.data.source.remote.response.StoryResponse
import com.teblung.dicodingstory.utils.DataDummy
import com.teblung.dicodingstory.utils.MainCoroutineRule
import com.teblung.dicodingstory.utils.PagedTestDataSources
import com.teblung.dicodingstory.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainVMTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var mainVM: MainVM
    @Mock
    private lateinit var repository: Repository

    @Test
    fun `get stories not null`() = runTest {
        val dummyListStory = DataDummy.generateDummyStory()

        val storiesData: PagingData<StoryResponse> = PagedTestDataSources.itemSnapshot(dummyListStory)
        val stories = MutableLiveData<PagingData<StoryResponse>>()
        stories.value = storiesData
        `when`(repository.getAllStory()).thenReturn(stories)

        mainVM = MainVM(repository)
        `when`(mainVM.stories).thenReturn(stories)
        val actualData: PagingData<StoryResponse> = mainVM.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = MainAdapter.storyCallback,
            updateCallback = PagedTestDataSources.listUpdateCallback,
            mainDispatcher = mainCoroutineRule.dispatcher,
            workerDispatcher = mainCoroutineRule.dispatcher
        )

        differ.submitData(actualData)
        advanceUntilIdle()

        verify(mainVM).stories
        assertThat(differ.snapshot()).isNotNull()
        assertThat(actualData).isNotNull()
        assertThat(dummyListStory.size).isEqualTo(differ.snapshot().size)
        assertThat(dummyListStory[0].name).isEqualTo(differ.snapshot()[0]?.name)
    }
}