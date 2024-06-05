package com.aura.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for handling user login logic.
 */
class LoginViewModel : ViewModel() {

    // Event to trigger navigation to HomeFragment
    private val _navigateToHomeEvent = MutableSharedFlow<Unit>()
    val navigateToHomeEvent: SharedFlow<Unit> get() = _navigateToHomeEvent

    /**
     * Function to navigate to the home screen.
     * Triggers the navigation event to HomeFragment.
     */
    fun navigateToHome() {
        // Emitting the navigation event to navigate to HomeFragment
        viewModelScope.launch {
            _navigateToHomeEvent.emit(Unit)
        }
    }
}
