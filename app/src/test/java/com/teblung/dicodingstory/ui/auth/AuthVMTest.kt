package com.teblung.dicodingstory.ui.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth.assertThat
import com.teblung.dicodingstory.data.source.remote.response.UserLoginResult
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
class AuthVMTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var authVM: AuthVM

    @Test
    fun `register works`() {
        val dummyName = "tebs"
        val dummyEmail = "teblung@gmail.com"
        val dummyPassword = "teblung"

        val expectedData = MutableLiveData<Boolean>()
        expectedData.value = true

        authVM.register(dummyName, dummyEmail, dummyPassword)
        verify(authVM).register(dummyName, dummyEmail, dummyPassword)

        `when`(authVM.loading).thenReturn(expectedData)

        val actualData = authVM.loading.getOrAwaitValue()

        verify(authVM).loading
        assertThat(actualData).isEqualTo(expectedData.value)
    }

    @Test
    fun `login works`() {
        val dummyEmail = "teblung@gmail.com"
        val dummyPassword = "teblung"
        val dummyLoginResult = UserLoginResult("teblung@gmail.com", "teblung", "teblungId")

        val expectedData = MutableLiveData<UserLoginResult>()
        expectedData.value = dummyLoginResult

        authVM.login(dummyEmail, dummyPassword)
        verify(authVM).login(dummyEmail, dummyPassword)

        `when`(authVM.userLogin).thenReturn(expectedData)

        val actualData = authVM.userLogin.getOrAwaitValue()

        verify(authVM).userLogin
        assertThat(actualData).isEqualTo(expectedData.value)
    }
}