package com.echo.app.auth

import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class GoogleAccountInfo(
    val email: String,
    val displayName: String,
    val photoUrl: String? = null,
    val idToken: String? = null
)

object GoogleAuthManager {

    private const val TAG = "GoogleAuthManager"

    /**
     * Fetches real Google accounts registered on the Android device via AccountManager.
     */
    fun fetchDeviceGoogleAccounts(context: Context): List<GoogleAccountInfo> {
        val detectedAccounts = mutableListOf<GoogleAccountInfo>()
        try {
            val accountManager = AccountManager.get(context)
            val accounts: Array<Account> = accountManager.getAccountsByType("com.google")
            for (acc in accounts) {
                val email = acc.name
                val namePart = email.substringBefore("@")
                    .replace(".", " ")
                    .split(" ")
                    .filter { it.isNotEmpty() }
                    .joinToString(" ") { word -> word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } }
                detectedAccounts.add(
                    GoogleAccountInfo(
                        email = email,
                        displayName = if (namePart.isNotBlank()) namePart else email.substringBefore("@"),
                        photoUrl = "https://lh3.googleusercontent.com/a/default-user"
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error querying device Google accounts via AccountManager", e)
        }

        return detectedAccounts.distinctBy { it.email }
    }

    /**
     * Creates an Intent to launch Android's native Account Picker dialog.
     */
    fun createGoogleAccountPickerIntent(): Intent {
        return AccountManager.newChooseAccountIntent(
            null,
            null,
            arrayOf("com.google"),
            true,
            null,
            null,
            null,
            null
        )
    }

    /**
     * Triggers Android Credential Manager API for Google Sign In.
     */
    fun launchCredentialManagerSignIn(
        activity: Activity,
        coroutineScope: CoroutineScope,
        serverClientId: String = "101010101010-example.apps.googleusercontent.com",
        onSuccess: (GoogleAccountInfo) -> Unit,
        onError: (String) -> Unit
    ) {
        val credentialManager = CredentialManager.create(activity)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        coroutineScope.launch {
            try {
                val result: GetCredentialResponse = withContext(Dispatchers.IO) {
                    credentialManager.getCredential(request = request, context = activity)
                }

                val credential = result.credential
                if (credential is GoogleIdTokenCredential) {
                    val idToken = credential.idToken
                    val email = credential.id
                    val displayName = credential.displayName ?: credential.givenName ?: email.substringBefore("@")
                    val photoUrl = credential.profilePictureUri?.toString()

                    onSuccess(
                        GoogleAccountInfo(
                            email = email,
                            displayName = displayName,
                            photoUrl = photoUrl,
                            idToken = idToken
                        )
                    )
                } else {
                    onError("Unrecognized credential type: ${credential.type}")
                }
            } catch (e: GetCredentialException) {
                Log.w(TAG, "CredentialManager sign in notice: ${e.message}")
                onError(e.message ?: "Google Credential Manager notification")
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error in CredentialManager flow", e)
                onError(e.message ?: "Google authentication error")
            }
        }
    }
}

