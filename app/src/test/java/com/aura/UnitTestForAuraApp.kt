package com.aura

import android.content.Context
import android.util.Log
import com.aura.data.model.LoginResultModel
import com.aura.data.model.AccountResultModel
import com.aura.data.model.AccountsResultModel
import com.aura.data.apiResponse.TransferApiResponse
import com.aura.data.repository.AccountRepository
import com.aura.data.repository.LoginRepository
import com.aura.data.repository.TransferRepository
import com.aura.ui.login.LoginState
import com.aura.ui.login.LoginViewModel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */


class UnitTestForAuraApp
{

  private val mockLoginRepository = mock(LoginRepository::class.java)
  private val mockAccountRepository = mock(AccountRepository::class.java)
  private val mockTransferRepository = mock(TransferRepository::class.java)

  @Test
  fun loginWithCorrectCredentials()= runBlocking {
    // Arrange
    val viewModel = LoginViewModel(mockLoginRepository)
    `when`(mockLoginRepository.fetchLoginData("correctUser", "correctPassword"))
      .thenReturn(LoginResultModel(true, 200))

    // Act
    viewModel.onFieldUserIdentifierChanged("correctUser")
    viewModel.onFieldUserPasswordChanged("correctPassword")
    viewModel.onButtonLoginClicked()

    // Assert
    try {
      assertEquals(LoginState.Success, viewModel.state.value)
      Log.d("UnitTestForAuraApp", "Test loginWithCorrectCredentials succeeded.")
    } catch (e: AssertionError) {
      Log.d("UnitTestForAuraApp", "Test loginWithCorrectCredentials failed: ${e.message}")
      throw e // rethrow the exception to ensure the test fails
    }
  }

  @Test
  fun loginWithWrongCredentials() = runBlocking {
    // Arrange
    val viewModel = LoginViewModel(mockLoginRepository)
    `when`(mockLoginRepository.fetchLoginData("wrongUser", "wrongPassword"))
      .thenReturn(LoginResultModel(false, 401))

    // Act
    viewModel.onFieldUserIdentifierChanged("wrongUser")
    viewModel.onFieldUserPasswordChanged("wrongPassword")
    viewModel.onButtonLoginClicked()

    // Assert
    assertEquals(LoginState.Error("Unauthorized"), viewModel.state.value)
  }
  }

  @Test
  fun tryToFetchLoginData()= runBlocking {
    // Arrange
    `when`(mockLoginRepository.fetchLoginData(anyString(), anyString()))
      .thenReturn(LoginResultModel(true, 200))

    // Act
    val result = mockLoginRepository.fetchLoginData("user", "password")

    // Assert
    assertEquals(200, result.statusCode)
    assertEquals(true, result.isSuccess)
  }

@Test
fun tryToFetchAccountData() = runBlocking {
  // Arrange
  `when`(mockAccountRepository.fetchAccountData("userId"))
    .thenReturn(AccountsResultModel(200, listOf(AccountResultModel(true, 100.0))))

  // Act
  val result = mockAccountRepository.fetchAccountData("userId")

  // Assert
  assertEquals(200, result.accountStatusCode)
  assertEquals(1, result.accounts.size)
  assertEquals(100.0, result.accounts[0].accountBalance, 0.0)
}

@Test
fun tryToFetchTransferData() = runBlocking {
  // Arrange
  `when`(mockTransferRepository.postTransfer(anyString(), anyString(), anyDouble()))
    .thenReturn(TransferApiResponse(true, "Transfer successful"))

  // Act
  val result = mockTransferRepository.postTransfer("sender", "recipient", 50.0)

  // Assert
  assertEquals(true, result.isSuccess)
  assertEquals("Transfer successful", result.message)
}