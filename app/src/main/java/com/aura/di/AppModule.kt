package com.aura.di

import com.aura.ui.login.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    // Définir le ViewModel pour LoginFragment
    viewModelOf(::LoginViewModel)

    // Ajouter d'autres définitions de dépendances ici si nécessaire
}
