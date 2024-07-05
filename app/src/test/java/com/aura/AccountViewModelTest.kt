package com.aura

import android.content.SharedPreferences
import com.aura.data.model.AccountResultModel
import com.aura.data.model.AccountsResultModel
import com.aura.data.repository.AccountRepository
import com.aura.ui.account.AccountState
import com.aura.ui.account.AccountViewModel
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
import org.mockito.Mockito.anyFloat
import org.mockito.Mockito.anyString
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class AccountViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var mockAccountRepository: AccountRepository

    @Mock
    private lateinit var mockSharedPreferences: SharedPreferences

    @Mock
    private lateinit var mockEditor: SharedPreferences.Editor

    private lateinit var viewModel: AccountViewModel


    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        MockitoAnnotations.openMocks(this)

        `when`(mockSharedPreferences.edit()).thenReturn(mockEditor)
        `when`(mockEditor.putFloat(anyString(), anyFloat())).thenReturn(mockEditor)
        `when`(mockEditor.apply()).then { }

        viewModel = AccountViewModel(mockAccountRepository, mockSharedPreferences)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Test case for successful loading of account data.
     */
    @Test
    fun successAccount() = runTest {
        println("test successAccount : ARRANGE")
        val userIdentifier = "1234"
        val expectedBalance = 2354.23
        val expectedAccountResult = AccountsResultModel(
            accounts = listOf(
                AccountResultModel(
                    accountId = "1",
                    isAccountMain = true,
                    accountBalance = expectedBalance
                ),
                AccountResultModel(accountId = "2", isAccountMain = false, accountBalance = 235.22)
            ),
            accountStatusCode = 200
        )

        `when`(mockAccountRepository.fetchAccountData(userIdentifier))
            .thenReturn(flowOf(expectedAccountResult))

        // Call the method to be tested
        println("test successAccount : ACT")
        viewModel.loadAccountData(userIdentifier)

        // Simulates time passed and the flow went to the end
        testDispatcher.scheduler.advanceUntilIdle()
        println("test successAccount : STATE = ${viewModel.state.value}")

        // Check the value after the time passed is Success
        println("test successAccount : ASSERT")
        try {
            assertEquals(AccountState.Success(expectedBalance), viewModel.state.value)
            println("test successAccount : SUCCESS")
        } catch (e: AssertionError) {
            println("test successAccount : FAIL, ${viewModel.state.value}")
            throw e
        }
    }

    /**
     * Test case for error handling when account data is not found.
     */
    @Test
    fun errorAccount() = runTest {
        println("test errorAccount : ARRANGE")
        val userIdentifier = "1234"
        val expectedBalance = 2354.23
        val expectedAccountResult = AccountsResultModel(
            accounts = emptyList(), accountStatusCode = 200
        )

        `when`(mockAccountRepository.fetchAccountData(userIdentifier))
            .thenReturn(flowOf(expectedAccountResult))

        // Call the method to be tested
        println("test errorAccount : ACT")
        viewModel.loadAccountData(userIdentifier)

        // Simulates time passed and the flow went to the end
        testDispatcher.scheduler.advanceUntilIdle()
        println("test errorAccount : STATE = ${viewModel.state.value}")

        // Check the value after the time passed is Success
        println("test errorAccount : ASSERT")
        try {
            assertEquals(AccountState.Error("Main account not found."), viewModel.state.value)
            println("test errorAccount : SUCCESS")
        } catch (e: AssertionError) {
            println("test errorAccount : FAIL, ${viewModel.state.value}")
            throw e
        }
    }
}