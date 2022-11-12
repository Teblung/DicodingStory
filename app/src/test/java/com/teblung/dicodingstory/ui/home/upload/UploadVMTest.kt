package com.teblung.dicodingstory.ui.home.upload

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.teblung.dicodingstory.data.Repository
import com.teblung.dicodingstory.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class UploadVMTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: Repository
    private lateinit var uploadVM: UploadVM

    @Mock
    private lateinit var dummyMockFile: File

    @Test
    fun `upload story without location success`() {
        val dummyToken = "dummyToken"
        val expectedData = MutableLiveData<Boolean>()
        expectedData.value = true

        repository.uploadStory(
            dummyToken,
            dummyMockFile,
            "description"
        )
        `when`(repository.isLoading).thenReturn(expectedData)

        uploadVM = UploadVM(repository)
        val actualData = uploadVM.loading.getOrAwaitValue()

        Assert.assertEquals(actualData, expectedData.value)
    }

    @Test
    fun `upload story with location success`() {
        val dummyToken = "dummyToken"
        val expectedData = MutableLiveData<Boolean>()
        expectedData.value = true

        repository.uploadStoryWithLocation(
            dummyToken,
            dummyMockFile,
            "description",
            -2.548926F,
            118.0148634F
        )

        `when`(repository.isLoading).thenReturn(expectedData)

        uploadVM = UploadVM(repository)
        val actualData = uploadVM.loading.getOrAwaitValue()

        Assert.assertEquals(actualData, expectedData.value)
    }
}