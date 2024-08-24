package com.example.guideapp.presentation.fragments.auth

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.example.guideapp.infrastructure.utils.GoogleSignInProvider
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val googleSignInProvider: GoogleSignInProvider): ViewModel() {
    fun getAccount(context: Context): GoogleSignInAccount? {
        return googleSignInProvider.getAccount(context)
    }
    fun getClient(context: Context): GoogleSignInClient {
        return googleSignInProvider.getClient(context)
    }

    fun login(data: Intent?, onSuccess: () -> Unit, onFailure: (error: String) -> Unit) {
        googleSignInProvider.login(data, onSuccess, onFailure)
    }
}
