package com.aura

import android.content.SharedPreferences
import androidx.lifecycle.viewModelScope
import com.aura.data.model.TransferResultModel
import com.aura.data.repository.TransferRepository
import com.aura.ui.transfer.TransferState
import com.aura.ui.transfer.TransferViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
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
class TransferViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var mockTransferRepository: TransferRepository

    @Mock
    private lateinit var mockSharedPreferences: SharedPreferences

    @Mock
    private lateinit var mockEditor: SharedPreferences.Editor

    private lateinit var viewModel: TransferViewModel


    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        MockitoAnnotations.openMocks(this)

        `when`(mockSharedPreferences.edit()).thenReturn(mockEditor)
        `when`(mockEditor.putFloat(anyString(), anyFloat())).thenReturn(mockEditor)
        `when`(mockEditor.apply()).then { }

        viewModel = TransferViewModel(mockTransferRepository, mockSharedPreferences)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Test case for successful transfer.
     */
    @Test
    fun successTransfer() = runTest {
        println("test successTransfer : ARRANGE")
        val transferSender = "1234"
        val transferRecipient = "5678"
        val transferAmount = 100.0
        val mainAccountBalance = 2354.23
        val expectedTransferResult =
            TransferResultModel(isTransferSuccessful = true, transferStatusCode = 200)

        `when`(
            mockTransferRepository.fetchTransferData(
                transferSender,
                transferRecipient,
                transferAmount
            )
        )
            .thenReturn(flowOf(expectedTransferResult))

        // Call the method to be tested
        println("test successTransfer : ACT")
        // Ensure the transferSender and mainAccountBalance are initialized
        viewModel.viewModelScope.launch {
            viewModel.transferSender = transferSender
            viewModel.mainAccountBalance = mainAccountBalance
        }
        viewModel.onFieldTransferRecipientChanged(transferRecipient)
        viewModel.onFieldTransferAmountChanged(transferAmount)
        viewModel.onButtonMakeTransferClicked()

        // Simulates time passed and the flow went to the end
        testDispatcher.scheduler.advanceUntilIdle()
        println("test successTransfer : STATE = ${viewModel.state.value}")

        // Check the value after the time passed is Success
        println("test successTransfer : ASSERT")
        try {
            assertEquals(TransferState.Success, viewModel.state.value)
            println("test successTransfer : SUCCESS")
        } catch (e: AssertionError) {
            println("test successTransfer : FAIL, ${viewModel.state.value}")
            throw e
        }
    }

    /**
     * Test case for insufficient balance during transfer.
     */
    @Test
    fun errorTransfer() = runTest {
        println("test successTransfer : ARRANGE")
        val transferSender = "1234"
        val transferRecipient = "5678"
        val transferAmount = 10000.0
        val mainAccountBalance = 2354.23
        val expectedTransferResult =
            TransferResultModel(isTransferSuccessful = true, transferStatusCode = 200)

        `when`(
            mockTransferRepository.fetchTransferData(
                transferSender,
                transferRecipient,
                transferAmount
            )
        )
            .thenReturn(flowOf(expectedTransferResult))

        // Call the method to be tested
        println("test successTransfer : ACT")
        // Ensure the transferSender and mainAccountBalance are initialized
        viewModel.viewModelScope.launch {
            viewModel.transferSender = transferSender
            viewModel.mainAccountBalance = mainAccountBalance
        }
        viewModel.onFieldTransferRecipientChanged(transferRecipient)
        viewModel.onFieldTransferAmountChanged(transferAmount)
        viewModel.onButtonMakeTransferClicked()

        // Simulates time passed and the flow went to the end
        testDispatcher.scheduler.advanceUntilIdle()
        println("test successTransfer : STATE = ${viewModel.state.value}")

        // Check the value after the time passed is Success
        println("test successTransfer : ASSERT")
        try {
            assertEquals(
                TransferState.Error("The main account balance is too low for this transfer, please try again."),
                viewModel.state.value
            )
            println("test successTransfer : SUCCESS")
        } catch (e: AssertionError) {
            println("test successTransfer : FAIL, ${viewModel.state.value}")
            throw e
        }
    }
}