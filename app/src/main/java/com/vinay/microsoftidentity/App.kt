package com.vinay.microsoftidentity

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.microsoft.identity.client.IPublicClientApplication
import com.microsoft.identity.client.PublicClientApplication
import com.microsoft.identity.client.PublicClientApplicationConfiguration
import com.microsoft.identity.client.PublicClientApplicationConfigurationFactory
import com.microsoft.identity.client.SingleAccountPublicClientApplication
import com.microsoft.identity.client.exception.MsalException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Created by Vinay on 05,May,2023
 */
class App: Application() {
    private lateinit var pca: IPublicClientApplication
    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        initializePca()
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

    private fun initializePca() = runBlocking {
        applicationScope.launch {
            try {
                pca = SingleAccountPublicClientApplication.create(
                    applicationContext,
                    R.raw.auth_config_single_account
                )
            } catch (e: MsalException) {
                // Handle exception
            }
        }
    }

    fun getPca(): IPublicClientApplication? {
        // Return the initialized pca
        return if (::pca.isInitialized) pca else null
    }
}