package com.aura

import android.app.Application
import com.aura.di.appModule
import com.aura.di.dataModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

/**
 * Custom application class for the Aura app.
 * Initializes Koin for dependency injection and global application resources.
 */
class AuraApplication : Application() {

    /**
     * Called when the application is starting.
     */
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger() // Activate Android logger for Koin
            androidContext(this@AuraApplication)
            modules(dataModule, appModule)
        }
    }
}