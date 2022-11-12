package com.teblung.dicodingstory.ui.maps

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.teblung.dicodingstory.data.Repository
import com.teblung.dicodingstory.data.source.remote.response.StoryResponse
import com.teblung.dicodingstory.utils.DataDummy
import com.teblung.dicodingstory.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MapsVMTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: Repository
    private lateinit var mapsVM: MapsVM

    @Test
    fun `getStoryWithLocation works`() {
        val dummyToken = "dummyToken"
        val dummyStoriesList = DataDummy.generateDummyStory()
        val expectedData = MutableLiveData<List<StoryResponse>>()
        expectedData.value = dummyStoriesList

        repository.getStoryWithLocation(dummyToken)
        `when`(repository.storyList).thenReturn(expectedData)

        mapsVM = MapsVM(repository)
        val actualData = mapsVM.listStoryData.getOrAwaitValue()

        Assert.assertEquals(actualData, expectedData.value)
        Assert.assertNotNull(actualData[0].lon)
        Assert.assertNotNull(actualData[0].lat)
    }
}