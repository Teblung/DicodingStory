package com.teblung.dicodingstory.ui.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.teblung.dicodingstory.data.Repository
import com.teblung.dicodingstory.data.source.remote.response.UserLoginResult
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
class AuthVMTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: Repository
    private lateinit var authVM: AuthVM

    @Test
    fun `register works`() {
        val dummyName = "tebs"
        val dummyEmail = "teblung@gmail.com"
        val dummyPassword = "teblung"

        val expectedData = MutableLiveData<Boolean>()
        expectedData.value = true

        repository.registerUser(dummyName, dummyEmail, dummyPassword)
        `when`(repository.isLoading).thenReturn(expectedData)

        authVM = AuthVM(repository)
        val actualData = authVM.loading.getOrAwaitValue()

        Assert.assertEquals(actualData, expectedData.value)
    }

    @Test
    fun `login works`() {
        val dummyEmail = "teblung@gmail.com"
        val dummyPassword = "teblung"
        val dummyLoginResult = UserLoginResult("teblung@gmail.com", "teblung", "teblungId")

        val expectedData = MutableLiveData<UserLoginResult>()
        expectedData.value = dummyLoginResult

        repository.loginUser(dummyEmail, dummyPassword)
        `when`(repository.userLogin).thenReturn(expectedData)

        authVM = AuthVM(repository)
        val actualData = authVM.userLogin.getOrAwaitValue()

        Assert.assertEquals(actualData, expectedData.value)
    }
}