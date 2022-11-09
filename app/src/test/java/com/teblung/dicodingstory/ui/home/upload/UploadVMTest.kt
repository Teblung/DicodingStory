package com.teblung.dicodingstory.ui.home.upload

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth.assertThat
import com.teblung.dicodingstory.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class UploadVMTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var uploadVM: UploadVM

    @Mock
    private lateinit var dummyMockFile: File

    @Test
    fun `upload story without location success`() {
        val dummyToken = "dummyToken"
        val expectedData = MutableLiveData<Boolean>()
        expectedData.value = true

        uploadVM.uploadWithoutLocation(
            dummyToken,
            dummyMockFile,
            "description"
        )

        verify(uploadVM).uploadWithoutLocation(
            dummyToken,
            dummyMockFile,
            "description"
        )

        `when`(uploadVM.loading).thenReturn(expectedData)

        val actualData = uploadVM.loading.getOrAwaitValue()

        verify(uploadVM).loading
        assertThat(actualData).isEqualTo(expectedData.value)
    }

    @Test
    fun `upload story with location success`() {
        val dummyToken = "dummyToken"
        val expectedData = MutableLiveData<Boolean>()
        expectedData.value = true

        uploadVM.uploadWithLocation(
            dummyToken,
            dummyMockFile,
            "description",
            -2.548926,
            118.0148634
        )

        verify(uploadVM).uploadWithLocation(
            dummyToken,
            dummyMockFile,
            "description",
            -2.548926,
            118.0148634
        )

        `when`(uploadVM.loading).thenReturn(expectedData)

        val actualData = uploadVM.loading.getOrAwaitValue()

        verify(uploadVM).loading
        assertThat(actualData).isEqualTo(expectedData.value)
    }
}