package com.aura

import android.app.Application
import com.aura.di.appModule
import com.aura.di.dataModule // Importez ici le module Koin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

/**
 * Custom application class for the Aura app.
 */
class AuraApplication : Application() {

    /**
     * Called when the application is starting.
     */
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger() // Active le logger Android pour Koin
            androidContext(this@AuraApplication)
            modules(dataModule, appModule)
        }

        // Initialize global resources here, for example:
        // - Logging frameworks
        // - Dependency injection libraries
        // - Application-wide configurations
        initializeGlobalResources()
    }

    private fun initializeGlobalResources() {
        // Initialization code goes here
    }
}
