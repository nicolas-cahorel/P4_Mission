package com.aura.di

import android.content.Context
import android.content.SharedPreferences
import com.aura.ui.login.LoginViewModel
import com.aura.ui.account.AccountViewModel
import com.aura.ui.transfer.TransferViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Définir le ViewModel pour LoginFragment
    viewModelOf(::LoginViewModel)

    viewModelOf(::AccountViewModel)

    viewModel { TransferViewModel(get(),get()) }

    // Define SharedPreferences
    single<SharedPreferences> { androidContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE) }

    // Définir le client API pour UserAccount
    //single { UserAccountClient(get()) }

    // Définir le repository pour UserAccount
    //single { UserAccountRepository(get()) }

    // Définir le ViewModel pour UserAccountFragment avec LoginViewModel en dépendance
    //viewModel { AccountViewModel(get(), get()) }



    // Ajouter d'autres définitions de dépendances ici si nécessaire
}
