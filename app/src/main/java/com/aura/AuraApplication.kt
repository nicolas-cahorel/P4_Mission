package com.aura

import android.app.Application

/**
 * Custom application class for the Aura app.
 */
class AuraApplication : Application() {

    /**
     * Called when the application is starting.
     */
    override fun onCreate() {
        super.onCreate()
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