package com.aura.di

import com.aura.data.network.AccountClient
import com.aura.data.network.LoginClient
import com.aura.data.network.TransferClient
import com.aura.data.repository.AccountRepository
import com.aura.data.repository.LoginRepository
import com.aura.data.repository.TransferRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val dataModule: Module = module {

    // OkHttpClient for network operations, with logging interceptor
    single {
        OkHttpClient.Builder().apply {
            addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        }.build()
    }

    // Moshi instance for JSON serialization/deserialization
    single {
        Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }

    // Retrofit instance for API calls, configured with base URL, Moshi converter, and OkHttpClient
    single {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080") // ou l'adresse IP de votre ordinateur pour un appareil physique
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .client(get())
            .build()
    }

    // LoginClient instance created from Retrofit
    single {
        get<Retrofit>().create(LoginClient::class.java)
    }

    // AccountClient instance created from Retrofit
    single {
        get<Retrofit>().create(AccountClient::class.java)
    }

    // TransferClient instance created from Retrofit
    single {
        get<Retrofit>().create(TransferClient::class.java)
    }

    // LoginRepository with dependency on LoginClient
    single { LoginRepository(get()) }

    // AccountRepository with dependency on AccountClient
    single { AccountRepository(get()) }

    // TransferRepository with dependency on TransferClient
    single { TransferRepository(get()) }
}