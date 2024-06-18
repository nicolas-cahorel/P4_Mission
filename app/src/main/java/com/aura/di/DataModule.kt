package com.aura.di

import com.aura.data.network.LoginClient
import com.aura.data.network.AccountClient
import com.aura.data.repository.LoginRepository
import com.aura.data.repository.AccountRepository
import org.koin.dsl.module
import org.koin.core.module.Module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

val dataModule: Module = module {

    single {
        OkHttpClient.Builder().apply {
            addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        }.build()
    }

    single {
        Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080") // ou l'adresse IP de votre ordinateur pour un appareil physique
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .client(get())
            .build()
    }

    single {
        get<Retrofit>().create(LoginClient::class.java)
    }

    single {
        get<Retrofit>().create(AccountClient::class.java)
    }

    single { LoginRepository(get()) }

    single { AccountRepository(get()) }
}
