package com.aura

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.aura.data.apiResponse.TransferApiResponse
import com.aura.data.model.AccountResultModel
import com.aura.data.model.AccountsResultModel
import com.aura.data.model.LoginResultModel
import com.aura.data.model.TransferResultModel
import com.aura.data.repository.AccountRepository
import com.aura.data.repository.LoginRepository
import com.aura.data.repository.TransferRepository
import com.aura.ui.account.AccountState
import com.aura.ui.account.AccountViewModel
import com.aura.ui.login.LoginState
import com.aura.ui.login.LoginViewModel
import com.aura.ui.transfer.TransferState
import com.aura.ui.transfer.TransferViewModel
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

// Declarations correctes qui causent une erreur log d lors de l'execution du premier test
//@ExperimentalCoroutinesApi
//class UnitTestForAuraApp {
//
//    // Mocks and view models for Login
//    @Mock
//    private lateinit var mockLoginRepository: LoginRepository
//
//    private lateinit var loginViewModel: LoginViewModel
//
//    // Mocks and view models for Account
//    @Mock
//    private lateinit var mockAccountRepository: AccountRepository
//
//    private lateinit var accountViewModel: AccountViewModel
//
//    // Mocks for Transfer
//    @Mock
//    private lateinit var mockTransferRepository: TransferRepository
//
//    private lateinit var transferViewModel: TransferViewModel
//
//    @Mock
//    private lateinit var mockSharedPreferences: SharedPreferences
//
//    @Mock
//    private lateinit var mockEditor: SharedPreferences.Editor
//    private lateinit var viewModel: LoginViewModel
//
//    private val mainThreadSurrogate = newSingleThreadContext("UI thread")
//
//    @Before
//    fun setUp() {
//        Dispatchers.setMain(mainThreadSurrogate)
//        MockitoAnnotations.openMocks(this)
//        `when`(mockSharedPreferences.edit()).thenReturn(mockEditor)
//        // Initialize the login view model
//        loginViewModel = LoginViewModel(mockLoginRepository, mockSharedPreferences, shouldSaveToSharedPreferences = false)
//
//        // Initialize the account view model
//        accountViewModel = AccountViewModel(mockAccountRepository, mockSharedPreferences, shouldSaveToSharedPreferences = false)
//
//        // Initialize the transfer view model
//        transferViewModel = TransferViewModel(mockTransferRepository, mockSharedPreferences)
//    }
//
//    @After
//    fun tearDown() {
//        Dispatchers.resetMain()
//        mainThreadSurrogate.close()
//    }


@ExperimentalCoroutinesApi
class UnitTestForAuraApp {

    @Mock
    private lateinit var mockLoginRepository: LoginRepository

    @Mock
    private lateinit var mockAccountRepository: AccountRepository

    @Mock
    private lateinit var mockTransferRepository: TransferRepository

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
            println("test loginWithCorrectCredentials : LoginState = $state")

            when (state) {
                is LoginState.Success -> {
                    println("test loginWithCorrectCredentials : SUCCESS")
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
                    println("test loginWithCorrectCredentials : FAIL")
                    // Fail the test explicitly since login should not fail with correct credentials
                    fail("Login with correct credentials failed: ${state.message}")
                }
                else -> {
                    println("State transition: $state")
                }
            }
        }
    }

    @Test
    fun loginWithWrongCredentials() = runTest {
        // Arrange
        println("test loginWithWrongCredentials : Arrange")
        val identifier = "1111"
        val password = "p@sswOrd"
        val expectedResult = LoginResultModel(isLoginSuccessful = false, loginStatusCode = 200)

        `when`(mockLoginRepository.fetchLoginData(identifier, password))
            .thenReturn(flowOf(expectedResult))

        // Act
        println("test loginWithWrongCredentials : Act")
        viewModel.onFieldUserIdentifierChanged(identifier)
        viewModel.onFieldUserPasswordChanged(password)
        viewModel.onButtonLoginClicked()

        // Assert
        println("test loginWithWrongCredentials : Assert")
        viewModel.state.collectLatest { state ->
            println("test loginWithWrongCredentials : LoginState = $state")

            when (state) {
                is LoginState.Success -> {
                    println("test loginWithWrongCredentials : FAIL, LoginState should be Error ")
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
                    println("test loginWithWrongCredentials : SUCCESS")
                    // Succeed the test explicitly since login should fail with wrong credentials
                    fail("Login with wrong credentials failed: ${state.message}")
                }
                else -> {
                    println("State transition: $state")
                }
            }
        }
    }

    @Test
    fun mainAccountWithCorrectUser() = runTest {
        // Arrange
        println("test mainAccountWithCorrectUser : Arrange")
        val userIdentifier = "1234"
        val expectedBalance = 2354.23
        val expectedAccountResult = AccountsResultModel(
            accounts = listOf(
                AccountResultModel(accountId = "1", isAccountMain = true, accountBalance = expectedBalance),
                AccountResultModel(accountId = "2", isAccountMain = false, accountBalance = 235.22)
            ),
                accountStatusCode = 200
            )

        // Mock the repository to return the expected result
        `when`(mockAccountRepository.fetchAccountData(userIdentifier))
            .thenReturn(flowOf(expectedAccountResult))

        // Mock SharedPreferences to return the user identifier
        `when`(mockSharedPreferences.getString("userIdentifier", null)).thenReturn(userIdentifier)

        // Create AccountViewModel with the mocked dependencies
        val accountViewModel = AccountViewModel(mockAccountRepository, mockSharedPreferences)

        // Act
        println("test mainAccountWithCorrectUser : Act")
        accountViewModel.loadAccountData(userIdentifier)

        // Assert
        println("test mainAccountWithCorrectUser : Assert")
        accountViewModel.state.collectLatest { state ->
            println("test mainAccountWithCorrectUser : AccountState = $state")

            when (state) {
                is AccountState.Success -> {
                    println("test mainAccountWithCorrectUser : SUCCESS")
                }
                is AccountState.Error -> {
                    println("test mainAccountWithCorrectUser : FAIL")
                    fail("Account loading failed with error: ${state.message}")
                }
                else -> {
                    println("State transition: $state")
                }
            }
        }
    }

    @Test
    fun mainAccountWithWrongUser() = runTest {
        // Arrange
        println("test mainAccountWithWrongUser : Arrange")
        val userIdentifier = "1111"
        val expectedAccountResult = AccountsResultModel(
            accounts = emptyList(),accountStatusCode = 200
        )

        // Mock the repository to return the expected result
        `when`(mockAccountRepository.fetchAccountData(userIdentifier))
            .thenReturn(flowOf(expectedAccountResult))

        // Mock SharedPreferences to return the user identifier
        `when`(mockSharedPreferences.getString("userIdentifier", null)).thenReturn(userIdentifier)

        // Create AccountViewModel with the mocked dependencies
        val accountViewModel = AccountViewModel(mockAccountRepository, mockSharedPreferences)

        // Act
        println("test mainAccountWithWrongUser : Act")
        accountViewModel.loadAccountData(userIdentifier)

        // Assert
        println("test mainAccountWithWrongUser : Assert")
        accountViewModel.state.collectLatest { state ->
            println("test mainAccountWithWrongUser : AccountState = $state")

            when (state) {
                is AccountState.Success -> {
                    println("test mainAccountWithWrongUser : FAIL")
                }
                is AccountState.Error -> {
                    println("test mainAccountWithWrongUser : SUCCESS")
                    fail("Account loading failed with error: ${state.message}")
                }
                else -> {
                    println("State transition: $state")
                }
            }
        }
    }

    @Test
    fun transferWithCorrectInformation() = runTest {
        // Arrange
        println("test transferWithCorrectInformation : Arrange")
        val transferSender = "1234"
        val transferRecipient = "5678"
        val transferAmount = 100.0
        val mainAccountBalance = 500.0
        val expectedTransferResult = TransferResultModel(isTransferSuccessful = true, transferStatusCode = 200)

        // Mock SharedPreferences to return the user identifier and main account balance
        println("test transferWithCorrectInformation : Arrange2")
        `when`(mockSharedPreferences.getString("userIdentifier", null)).thenReturn(transferSender)
        println("test transferWithCorrectInformation : Arrange3")
        `when`(mockSharedPreferences.getFloat("mainAccountBalance", 0.0F)).thenReturn(mainAccountBalance.toFloat())

        // Mock the repository to return the expected transfer result
        println("test transferWithCorrectInformation : Arrange4")
        `when`(mockTransferRepository.fetchTransferData(transferSender, transferRecipient, transferAmount))
            .thenReturn(flowOf(expectedTransferResult))

        // Create TransferViewModel with the mocked dependencies
        println("test transferWithCorrectInformation : Arrange5")
        val transferViewModel = TransferViewModel(mockTransferRepository, mockSharedPreferences)

        // Act
        println("test transferWithCorrectInformation : Act")
        println("test transferWithCorrectInformation : Arrange6")
        transferViewModel.onFieldTransferRecipientChanged(transferRecipient)
        transferViewModel.onFieldTransferAmountChanged(transferAmount)
        transferViewModel.onButtonMakeTransferClicked()

        // Assert
        println("test transferWithCorrectInformation : Arrange7")
        println("test transferWithCorrectInformation : Assert")
        transferViewModel.state.collectLatest { state ->
            println("test transferWithCorrectInformation : TransferState = $state")

            when (state) {
                is TransferState.Success -> {
                    println("test transferWithCorrectInformation : SUCCESS")
                    // Verify that fetchTransferData was called
                    try {
                        verify(mockTransferRepository).fetchTransferData(transferSender, transferRecipient, transferAmount)
                        println("fetchTransferData call verified")
                    } catch (e: Exception) {
                        println("fetchTransferData call verification failed")
                    }
                }
                is TransferState.Error -> {
                    println("test transferWithCorrectInformation : FAIL")
                    fail("Transfer with correct information failed: ${state.message}")
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