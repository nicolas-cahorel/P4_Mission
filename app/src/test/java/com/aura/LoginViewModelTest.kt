package com.aura

import android.content.SharedPreferences
import com.aura.data.model.LoginResultModel
import com.aura.data.repository.LoginRepository
import com.aura.ui.login.LoginState
import com.aura.ui.login.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.anyString
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var mockLoginRepository: LoginRepository

    @Mock
    private lateinit var mockSharedPreferences: SharedPreferences

    @Mock
    private lateinit var mockEditor: SharedPreferences.Editor

    private lateinit var viewModel: LoginViewModel


    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        MockitoAnnotations.openMocks(this)

        `when`(mockSharedPreferences.edit()).thenReturn(mockEditor)
        `when`(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor)
        `when`(mockEditor.apply()).then { }

        viewModel = LoginViewModel(mockLoginRepository, mockSharedPreferences)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Test case for successful login.
     */
    @Test
    fun successLogin() = runTest {
        println("test successLogin : ARRANGE")
        val identifier = "1111"
        val password = "p@sswOrd"
        val expectedLoginResult = LoginResultModel(isLoginSuccessful = true, loginStatusCode = 200)

        `when`(mockLoginRepository.fetchLoginData(identifier, password))
            .thenReturn(flowOf(expectedLoginResult))

        // Call the method to be tested
        println("test successLogin : ACT")
        viewModel.onFieldUserIdentifierChanged(identifier)
        viewModel.onFieldUserPasswordChanged(password)
        viewModel.onButtonLoginClicked()

        // Simulates time passed and the flow went to the end
        testDispatcher.scheduler.advanceUntilIdle()
        println("test successLogin : STATE = ${viewModel.state.value}")

        // Check the value after the time passed is Success
        println("test successLogin : ASSERT")
        try {
            assertEquals(LoginState.Success, viewModel.state.value)
            println("test successLogin : SUCCESS")
        } catch (e: AssertionError) {
            println("test successLogin : FAIL, ${viewModel.state.value}")
            throw e
        }
    }

    /**
     * Test case for error handling during login.
     */
    @Test
    fun errorLogin() = runTest {
        println("test errorLogin : ARRANGE")
        val identifier = "1234"
        val password = "p@sswOrd"
        val expectedResult = LoginResultModel(isLoginSuccessful = false, loginStatusCode = 200)

        `when`(mockLoginRepository.fetchLoginData(identifier, password))
            .thenReturn(flowOf(expectedResult))

        // Call the method to be tested
        println("test errorLogin : ACT")
        viewModel.onFieldUserIdentifierChanged(identifier)
        viewModel.onFieldUserPasswordChanged(password)
        viewModel.onButtonLoginClicked()

        // Simulates time passed and the flow went to the end
        testDispatcher.scheduler.advanceUntilIdle()
        println("test errorLogin : STATE = ${viewModel.state.value}")

        // Check the value after the time passed is Success
        println("test errorLogin : ASSERT")
        try {
            assertEquals(
                LoginState.Error("HTTP status code 200: incorrect identifiers"),
                viewModel.state.value
            )
            println("test errorLogin : SUCCESS")
        } catch (e: AssertionError) {
            println("test errorLogin : FAIL, ${viewModel.state.value}")
            throw e
        }
    }
}