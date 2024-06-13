package com.aura.ui.home

sealed interface HomeState{

    data object Loading: HomeState


    data class Success(val balance: Int): HomeState

    data class Error(val message: String) : HomeState

}