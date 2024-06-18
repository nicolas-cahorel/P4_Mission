package com.aura.di

import com.aura.ui.login.LoginViewModel
import com.aura.ui.account.UserAccountViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Définir le ViewModel pour LoginFragment
    viewModelOf(::LoginViewModel)

    // Définir le client API pour UserAccount
    //single { UserAccountClient(get()) }

    // Définir le repository pour UserAccount
    //single { UserAccountRepository(get()) }

    // Définir le ViewModel pour UserAccountFragment avec LoginViewModel en dépendance
    viewModel { UserAccountViewModel(get(), get()) }

    // Ajouter d'autres définitions de dépendances ici si nécessaire
}
