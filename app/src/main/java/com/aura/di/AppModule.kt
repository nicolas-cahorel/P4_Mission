package com.aura.di

import android.content.Context
import android.content.SharedPreferences
import com.aura.ui.account.AccountViewModel
import com.aura.ui.login.LoginViewModel
import com.aura.ui.transfer.TransferViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    // Define ViewModel for LoginFragment
    viewModelOf(::LoginViewModel)

    // Define ViewModel for AccountFragment
    viewModelOf(::AccountViewModel)

    // Define ViewModel for TransferFragment and inject dependencies
    viewModel { TransferViewModel(get(), get()) }

    // Define SharedPreferences
    single<SharedPreferences> {
        androidContext().getSharedPreferences(
            "my_prefs",
            Context.MODE_PRIVATE
        )
    }
}