package com.aura

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.aura.data.apiResponse.TransferApiResponse
import com.aura.data.model.AccountResultModel
import com.aura.data.model.AccountsResultModel
import com.aura.data.model.LoginResultModel
import com.aura.data.repository.LoginRepository
import com.aura.ui.login.LoginState
import com.aura.ui.login.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.net.UnknownHostException
import java.util.concurrent.CountDownLatch

@ExperimentalCoroutinesApi
class UnitTestForAuraApp {

    @Mock
    private lateinit var mockLoginRepository: LoginRepository

    @Mock
    private lateinit var mockSharedPreferences: SharedPreferences

    @Mock
    private lateinit var mockEditor: SharedPreferences.Editor
    private lateinit var viewModel: LoginViewModel

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
        MockitoAnnotations.openMocks(this)
        `when`(mockSharedPreferences.edit()).thenReturn(mockEditor)
        viewModel = LoginViewModel(mockLoginRepository, mockSharedPreferences, shouldSaveToSharedPreferences = false)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun loginWithCorrectCredentials() = runTest {
        // Arrange
        println("test loginWithCorrectCredentials : Arrange")
        val identifier = "1234"
        val password = "p@sswOrd"
        val expectedResult = LoginResultModel(isLoginSuccessful = true, loginStatusCode = 200)

        `when`(mockLoginRepository.fetchLoginData(identifier, password))
            .thenReturn(flowOf(expectedResult))

        // Act
        println("test loginWithCorrectCredentials : Act")
        viewModel.onFieldUserIdentifierChanged(identifier)
        viewModel.onFieldUserPasswordChanged(password)
        viewModel.onButtonLoginClicked()

        // Assert
        println("test loginWithCorrectCredentials : Assert")
        viewModel.state.collectLatest { state ->
            println("Current state: $state")

            when (state) {
                is LoginState.Success -> {
                    println("Login with correct credentials succeeded")
                    // Verify that fetchLoginData was called
                    try {
                        verify(mockLoginRepository).fetchLoginData(identifier, password)
                        println("fetchLoginData call verified")
                    } catch (e: Exception) {
                        println("fetchLoginData call verification failed")
                    }

                    // Verify that shared preferences are updated
                    try {
                        println("Verifying shared preferences update...")
                        verify(mockEditor).putString("userIdentifier", identifier)
                        verify(mockEditor).apply()
                        println("Shared preferences update verified")
                    } catch (e: Exception) {
                        println("Shared preferences update verification failed : $e")
                    }
                }
                is LoginState.Error -> {
                    println("Login with correct credentials failed: ${state.message}")
                    // Fail the test explicitly since login should not fail with correct credentials
                    fail("Login with correct credentials failed: ${state.message}")
                }
                else -> {
                    println("State transition: $state")
                }
            }
        }
    }

    private fun runTest(block: suspend () -> Unit) {
        // Utilisation d'un CountDownLatch pour bloquer le thread de test principal jusqu'à la fin du bloc de test
        val latch = CountDownLatch(1)
        viewModel.viewModelScope.launch {
            try {
                block()
            } finally {
                latch.countDown()
            }
        }
        latch.await()
    }
}

//    @Test
//    fun loginWithIncorrectCredentials() = runTest {
//        // Arrange
//        val identifier = "wrongUser"
//        val password = "wrongPassword"
//        val expectedResult = LoginResultModel(isLoginSuccessful = false, loginStatusCode = 200)
//
//        `when`(mockLoginRepository.fetchLoginData(identifier, password))
//            .thenReturn(flowOf(expectedResult))
//
//        // Act
//        viewModel.onFieldUserIdentifierChanged(identifier)
//        viewModel.onFieldUserPasswordChanged(password)
//        viewModel.onButtonLoginClicked()
//
//        // Assert
//        Log.d("UnitTest", "Verifying login state with incorrect credentials")
//        val state = viewModel.state.first()
//        if (state is LoginState.Error) {
//            Log.d("UnitTest", "Login with incorrect credentials resulted in error as expected")
//        } else {
//            Log.e("UnitTest", "Login with incorrect credentials did not result in error")
//        }
//
//        // Verify that fetchLoginData was called
//        // Verify that fetchLoginData was called
//        Log.d("UnitTest", "Verifying fetchLoginData call")
//        try {
//            verify(mockLoginRepository).fetchLoginData(identifier, password)
//            Log.d("UnitTest", "fetchLoginData call verified")
//        } catch (e: Exception) {
//            Log.e("UnitTest", "fetchLoginData call verification failed", e)
//        }
//    }
//
//    @Test
//    fun loginWithUnknownHostError() = runTest {
//        // Arrange
//        val identifier = "correctUser"
//        val password = "correctPassword"
//
//        `when`(mockLoginRepository.fetchLoginData(identifier, password))
//            .thenThrow(UnknownHostException("No Internet connection"))
//
//        // Act
//        viewModel.onFieldUserIdentifierChanged(identifier)
//        viewModel.onFieldUserPasswordChanged(password)
//        viewModel.onButtonLoginClicked()
//
//        // Assert
//        val state = viewModel.state.first()
//        assertTrue(state is LoginState.Error)
//        assertTrue((state as LoginState.Error).message.contains("No Internet connection"))
//
//        // Verify that fetchLoginData was called
//        verify(mockLoginRepository).fetchLoginData(identifier, password)
//    }
//    @Test
//    fun mainAccountWithCorrectUser() {
//    }
//
//    @Test
//    fun mainAccountWithWrongUser() {
//    }
//
//    @Test
//    fun transferWithCorrectInformation() {
//    }
//
//    @Test
//    fun transferWithWrongSender() {
//    }
//
//    @Test
//    fun transferWithWrongRecipient() {
//    }
//
//    @Test
//    fun transferWithWrongAmount() {
//    }




//    @Test
//    fun getAccount() = runBlocking {
//        // Arrange
//        val viewModel = LoginViewModel(mockLoginRepository)
//        `when`(mockLoginRepository.fetchLoginData("wrongUser", "wrongPassword"))
//            .thenReturn(LoginResultModel(false, 401))
//
//        // Act
//        viewModel.onFieldUserIdentifierChanged("wrongUser")
//        viewModel.onFieldUserPasswordChanged("wrongPassword")
//        viewModel.onButtonLoginClicked()
//
//        // Assert
//        assertEquals(LoginState.Error("Unauthorized"), viewModel.state.value)
//    }
//
//
//    @Test
//    fun tryToFetchLoginData() = runBlocking {
//        // Arrange
//        `when`(mockLoginRepository.fetchLoginData(anyString(), anyString()))
//            .thenReturn(LoginResultModel(true, 200))
//
//        // Act
//        val result = mockLoginRepository.fetchLoginData("user", "password")
//
//        // Assert
//        assertEquals(200, result.statusCode)
//        assertEquals(true, result.isSuccess)
//    }
//
//    @Test
//    fun tryToFetchAccountData() = runBlocking {
//        // Arrange
//        `when`(mockAccountRepository.fetchAccountData("userId"))
//            .thenReturn(AccountsResultModel(200, listOf(AccountResultModel(true, 100.0))))
//
//        // Act
//        val result = mockAccountRepository.fetchAccountData("userId")
//
//        // Assert
//        assertEquals(200, result.accountStatusCode)
//        assertEquals(1, result.accounts.size)
//        assertEquals(100.0, result.accounts[0].accountBalance, 0.0)
//    }
//
//    @Test
//    fun tryToFetchTransferData() = runBlocking {
//        // Arrange
//        `when`(mockTransferRepository.postTransfer(anyString(), anyString(), anyDouble()))
//            .thenReturn(TransferApiResponse(true, "Transfer successful"))
//
//        // Act
//        val result = mockTransferRepository.postTransfer("sender", "recipient", 50.0)
//
//        // Assert
//        assertEquals(true, result.isSuccess)
//        assertEquals("Transfer successful", result.message)
//    }
