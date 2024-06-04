package com.aura

import android.app.Application

class AuraApplication : Application() {

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