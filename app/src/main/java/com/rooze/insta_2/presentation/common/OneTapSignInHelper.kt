package com.rooze.insta_2.presentation.common

import android.content.Intent
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class OneTapSignInHelper(private val signInClient: SignInClient) {
    suspend fun getSignInRequestIntent(): IntentSenderRequest? = suspendCoroutine { continuation ->
        signInClient.beginSignIn(getSignInRequest())
            .addOnSuccessListener { beginSignInResult ->
                continuation.resume(IntentSenderRequest.Builder(
                    beginSignInResult.pendingIntent.intentSender
                ).build())
            }.addOnFailureListener {
                continuation.resume(null)
            }
    }

    suspend fun getSignUpRequestIntent(): IntentSenderRequest? = suspendCoroutine { continuation ->
        signInClient.beginSignIn(getSignInRequest(false))
            .addOnSuccessListener { beginSignInResult ->
                continuation.resume(IntentSenderRequest.Builder(
                    beginSignInResult.pendingIntent.intentSender
                ).build())
            }.addOnFailureListener {
                continuation.resume(null)
            }
    }

    fun getCredentialTokenId(data: Intent?): String? = signInClient.getSignInCredentialFromIntent(data).googleIdToken

    fun signOut() {
        signInClient.signOut()
    }

    private fun getSignInRequest(filterAccount: Boolean = true): BeginSignInRequest =
        BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(ViewConstants.WEB_CLIENT_ID)
                    .setFilterByAuthorizedAccounts(filterAccount)
                    .build()
            ).setAutoSelectEnabled(filterAccount)
            .build()
}