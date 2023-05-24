package com.vinay.microsoftidentity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.microsoft.identity.client.IPublicClientApplication
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.microsoft.identity.client.PublicClientApplication
import com.microsoft.identity.client.PublicClientApplicationConfiguration
import com.microsoft.identity.client.SingleAccountPublicClientApplication
import com.microsoft.identity.client.exception.MsalException
import com.vinay.microsoftidentity.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val clientId = "<your_client_id>"
    private val redirectUri = "msauth://<your_package_name>/<your_scheme>"
    val scopes = arrayOf("user.read")
    val authorityUrl = "https://login.microsoftonline.com/common"

    /* Azure AD Variables */
    private var singleAccountApp: ISingleAccountPublicClientApplication? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        val resourceId = R.raw.auth_config_single_account

        PublicClientApplication.createSingleAccountPublicClientApplication(
            this,
            R.raw.auth_config_single_account,
            object : IPublicClientApplication.ISingleAccountApplicationCreatedListener {
                override fun onCreated(application: ISingleAccountPublicClientApplication) {
                    /**
                     * This test app assumes that the app is only going to support one account.
                     * This requires "account_mode" : "SINGLE" in the config json file.
                     *
                     */
                    singleAccountApp = application

                }

                override fun onError(exception: MsalException) {
                }
            })
        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAnchorView(R.id.fab)
                .setAction("Action", null).show()
            val application = applicationContext as App
            val pca = application.getPca()

//            pca?.signIn(this, ::onSignInResult)
            performSignIn(
                publicClientApplication = singleAccountApp,
                scopes = scopes,
                signInButtonClickListener = {
                    // This is called when the sign-in button is clicked
                },
                tokenCallback = { accessToken, tenantId, idToken ->
                    // This is called when the sign-in is successful
                },
                errorCallback = { error ->
                    // This is called when there is an error during the sign-in process
                }
            )
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
    private fun onSignInResult(result: SignInResult) {
        when (result) {
            is SignInResult.Success -> {
                // Handle successful sign-in
                val accessToken = result.accessToken
                Log.d("Sign In", "Access token: $accessToken")
            }
            is SignInResult.Cancel -> {
                // Handle canceled sign-in
                Log.d("Sign In", "Canceled by user")
            }
            is SignInResult.Failure -> {
                // Handle sign-in failure
                val exception = result.exception
                Log.e("Sign In", "Error: ${exception?.message}", exception)
            }
        }
    }

}