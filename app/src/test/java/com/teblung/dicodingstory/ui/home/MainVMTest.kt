package com.teblung.dicodingstory.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import com.teblung.dicodingstory.data.Repository
import com.teblung.dicodingstory.data.source.remote.response.StoryResponse
import com.teblung.dicodingstory.utils.DataDummy
import com.teblung.dicodingstory.utils.MainCoroutineRule
import com.teblung.dicodingstory.utils.PagedTestDataSources
import com.teblung.dicodingstory.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainVMTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: Repository
    private lateinit var mainVM: MainVM

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun `get stories not null`() = runTest {
        val dummyListStory = DataDummy.generateDummyStory()
        val storiesData: PagingData<StoryResponse> =
            PagedTestDataSources.itemSnapshot(dummyListStory)
        val stories = MutableLiveData<PagingData<StoryResponse>>()
        stories.value = storiesData

        `when`(repository.getAllStory()).thenReturn(stories)

        mainVM = MainVM(repository)
        val actualData: PagingData<StoryResponse> = mainVM.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = MainAdapter.storyCallback,
            updateCallback = PagedTestDataSources.listUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        differ.submitData(actualData)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyListStory, differ.snapshot())
        Assert.assertEquals(dummyListStory.size, differ.snapshot().size)
        Assert.assertEquals(dummyListStory[0].name, differ.snapshot()[0]?.name)
    }
}