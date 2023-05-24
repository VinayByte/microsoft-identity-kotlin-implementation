package com.vinay.microsoftidentity

/**
 * Created by Vinay on 08,May,2023
 */
import androidx.appcompat.app.AppCompatActivity
import com.microsoft.identity.client.AcquireTokenParameters
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IAccount
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.IPublicClientApplication
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.microsoft.identity.client.exception.MsalException


/**
 * Extension function to perform Microsoft Single Account Mode SOS sign-in using a custom tab.
 * Retrieves the token and account details when the user clicks on a sign-in button in the activity.
 */
fun AppCompatActivity.performSignIn(
    publicClientApplication: ISingleAccountPublicClientApplication?,
    scopes: Array<String>,
    signInButtonClickListener: () -> Unit,
    tokenCallback: (String, String, String) -> Unit,
    errorCallback: (String) -> Unit
) {
    // Call createSingleAccountPublicClientApplication in a background thread
    if (publicClientApplication != null) {
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
        signInButtonClickListener()
    } else {
        // handle error
    }
}

fun AppCompatActivity.performSignOut(
    publicClientApplication: ISingleAccountPublicClientApplication?,
    signOutCallback: () -> Unit,
    errorCallback: (String) -> Unit
) {
    if (publicClientApplication != null) {
        val account = publicClientApplication.currentAccount

        if (account != null) {
            publicClientApplication.signOut(object : ISingleAccountPublicClientApplication.SignOutCallback {
                override fun onSignOut() {
                    signOutCallback()
                }

                override fun onError(exception: MsalException) {
                    errorCallback("Error during sign-out: ${exception.message}")
                }
            })
        } else {
            errorCallback("No signed-in account found.")
        }
    } else {
        // Handle error
    }
}
fun IPublicClientApplication.signIn(
    activity: AppCompatActivity,
    callback: (SignInResult) -> Unit
) {
    val scopes = arrayOf("User.Read") // Specify the desired scopes for your application

    val signInParameters = AcquireTokenParameters.Builder()
        .startAuthorizationFromActivity(activity)
        .withScopes(listOf(*scopes))
        .withCallback(object : AuthenticationCallback {
            override fun onSuccess(authenticationResult: IAuthenticationResult?) {
                val accessToken = authenticationResult?.accessToken
                callback(SignInResult.Success(accessToken))
            }

            override fun onCancel() {
                callback(SignInResult.Cancel)
            }

            override fun onError(exception: MsalException?) {
                callback(SignInResult.Failure(exception))
            }
        })
        .build()

    acquireToken(signInParameters)
}

sealed class SignInResult {
    data class Success(val accessToken: String?) : SignInResult()
    object Cancel : SignInResult()
    data class Failure(val exception: MsalException?) : SignInResult()
}
 fun IPublicClientApplication.logout() {

}

