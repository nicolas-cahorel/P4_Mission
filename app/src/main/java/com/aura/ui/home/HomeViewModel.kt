package com.aura.ui.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel responsible for handling logic related to the HomeFragment.
 */
class HomeViewModel : ViewModel() {

    // MutableStateFlow representing the balance
    private val _balance = MutableStateFlow("2654,54€")

    // Expose balance as StateFlow
    val balance: StateFlow<String> get() = _balance

    /**
     * Logic to execute when the transfer button is clicked.
     */
    fun onTransferClicked() {
        // Add logic here if needed

    }
}