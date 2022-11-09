package com.teblung.dicodingstory.ui.maps

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth.assertThat
import com.teblung.dicodingstory.data.source.remote.response.StoryResponse
import com.teblung.dicodingstory.utils.DataDummy
import com.teblung.dicodingstory.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MapsVMTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mapsVM: MapsVM

    @Test
    fun `getStoryWithLocation works`() {
        val dummyToken = "dummyToken"
        val dummyStoriesList = DataDummy.generateDummyStory()
        val expectedData = MutableLiveData<List<StoryResponse>>()
        expectedData.value = dummyStoriesList

        mapsVM.getStoryWithLocation(dummyToken)

        verify(mapsVM).getStoryWithLocation(dummyToken)

        `when`(mapsVM.listStoryData).thenReturn(expectedData)

        val actualData = mapsVM.listStoryData.getOrAwaitValue()

        verify(mapsVM).listStoryData

        assertThat(actualData).isEqualTo(expectedData.value)
        assertThat(actualData[0].lon).isNotNull()
        assertThat(actualData[0].lat).isNotNull()
    }
}