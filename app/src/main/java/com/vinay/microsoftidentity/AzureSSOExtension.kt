package com.vinay.microsoftidentity

/**
 * Created by Vinay on 08,May,2023
 */
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IAccount
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.microsoft.identity.client.exception.MsalException


/**
 * Extension function to perform Microsoft Single Account Mode SOS sign-in using a custom tab.
 * Retrieves the token and account details when the user clicks on a sign-in button in the activity.
 */
fun AppCompatActivity.performSignIn(
    publicClientApplication: ISingleAccountPublicClientApplication?,
    scopes: Array<String>,
    authorityUrl: String,
    redirectUri: String,
    signInButtonClickListener: () -> Unit,
    tokenCallback: (String, String, String) -> Unit,
    errorCallback: (String) -> Unit
) {
    // Call createSingleAccountPublicClientApplication in a background thread
    if (publicClientApplication != null) {
        // do something with pca
        // Create a custom tab intent and launch the authentication URL
        val customTabsIntent = CustomTabsIntent.Builder()
            .setToolbarColor(
                ContextCompat.getColor(
                    this@performSignIn,
                    android.R.color.holo_red_dark
                )
            )
            .build()

        publicClientApplication.signIn(
            this@performSignIn,
            null,
            scopes,
            object : AuthenticationCallback {
                override fun onSuccess(authenticationResult: com.microsoft.identity.client.IAuthenticationResult) {
                    // Retrieve the token and account details from the authentication result
                    val accessToken = authenticationResult.accessToken
                    val account: IAccount = authenticationResult.account

                    val tenantId = authenticationResult.tenantId ?: ""
//                val idToken = authenticationResult.idToken ?: ""

                    tokenCallback(accessToken, tenantId, "idToken")
                }

                override fun onCancel() {
                    errorCallback("Sign-in cancelled")
                }

                override fun onError(exception: MsalException) {
                    errorCallback("Error during sign-in: ${exception.message}")
                }
            })

        // Launch the custom tab and set a callback for when the sign-in button is clicked
        customTabsIntent.launchUrl(this@performSignIn, Uri.parse(authorityUrl))
        signInButtonClickListener()
    } else {
        // handle error
    }
}





