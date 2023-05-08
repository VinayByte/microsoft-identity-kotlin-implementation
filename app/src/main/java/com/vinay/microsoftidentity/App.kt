package com.vinay.microsoftidentity

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.microsoft.identity.client.PublicClientApplication
import com.microsoft.identity.client.PublicClientApplicationConfiguration
import com.microsoft.identity.client.PublicClientApplicationConfigurationFactory

/**
 * Created by Vinay on 05,May,2023
 */
class App: Application() {
    override fun onCreate() {
        super.onCreate()
    }
    override fun attachBaseContext(base: Context?) {
        // Get the current configuration
        val configuration = base?.resources?.configuration

        // Create a new configuration with font scaling disabled
        val newConfiguration = Configuration(configuration)
        newConfiguration.fontScale = 1f

        // Create a new context with the updated configuration
        val newContext = base?.createConfigurationContext(newConfiguration)

        // Set the new context as the base context
        super.attachBaseContext(newContext)
    }
}