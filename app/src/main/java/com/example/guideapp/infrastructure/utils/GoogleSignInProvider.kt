package com.example.guideapp.infrastructure.utils

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import javax.inject.Inject

class GoogleSignInProvider @Inject constructor(private val context: Context){
    private val serverClientId = "362347497673-acdd6gjgg5c3ep1ae7hah2a4tihdloch.apps.googleusercontent.com"

    fun getAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    fun getClient(): GoogleSignInClient {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(serverClientId)
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context, googleSignInOptions)
    }

    fun login(data: Intent?, onSuccess: () -> Unit, onFailure: (error: String) -> Unit) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val result = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(result.idToken, null)
            val auth = FirebaseAuth.getInstance()
            auth.signInWithCredential(credential).addOnCompleteListener {
                if (it.isSuccessful) onSuccess()
                else onFailure("Login is not successful")
            }
        } catch (e: ApiException) {
            onFailure(e.message ?: "")
        }
    }

    fun logout() {
        getClient().signOut()
    }
}
